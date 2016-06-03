package cn.xyz.mianshi.service.impl;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.example.AuditionFTExample;
import cn.xyz.mianshi.example.AuditionInviteExample;
import cn.xyz.mianshi.service.AuditionFTManager;
import cn.xyz.mianshi.service.AuditionRTManager;
import cn.xyz.mianshi.service.CompanyManager;
import cn.xyz.mianshi.service.ExamManager;
import cn.xyz.mianshi.service.JobManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.AuditionFT;
import cn.xyz.mianshi.vo.ExamVO;
import cn.xyz.mianshi.vo.JobApply;
import cn.xyz.mianshi.vo.JobVO;
import cn.xyz.mianshi.vo.PageVO;
import cn.xyz.mianshi.vo.PushText;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.JobVO.Profile;
import cn.xyz.repository.MongoRepository;
import cn.xyz.service.KSMSServiceImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

@Service
public class AuditionFTManagerImpl extends MongoRepository implements AuditionFTManager {

	public static final Logger logger = LoggerFactory.getLogger(AuditionFTManagerImpl.class);

	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private ExamManager examManager;
	@Autowired
	private JobManager jobManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private AuditionRTManager artManager;
	@Autowired
	private KSMSServiceImpl pushManager;

	@Override
	public Object agree(int userId, List<ObjectId> idList) {
		User user = userManager.getUser(userId);
		// 普通用户
		if (0 == user.getCompanyId()) {
			Query<AuditionFT> query = dsForRW.createQuery(AuditionFT.class);
			// query.field("toUserId").equal(userId);
			query.filter("_id in", idList);
			query.field("toStatus").equal(AuditionFT.ToStatus.DEFAULT);
			query.field("status").equal(AuditionFT.Status.DEFAULT);

			UpdateOperations<AuditionFT> ops = dsForRW.createUpdateOperations(AuditionFT.class);
			ops.set("toStatus", AuditionFT.ToStatus.AGREE);
			ops.set("t", System.currentTimeMillis());

			List<Object> results = selectId("a_aft", query.getQueryObject());

			dsForRW.update(query, ops);

			return results;
		}
		return null;
	}

	@Override
	public Object refuse(int userId, List<ObjectId> idList) {
		User user = userManager.getUser(userId);
		if (0 == user.getCompanyId()) {
			Query<AuditionFT> q = dsForRW.createQuery(AuditionFT.class);
			q.field("toUserId").equal(userId);
			q.filter("_id in", idList);
			q.filter("toStatus !=", AuditionFT.ToStatus.REFUSE);

			UpdateOperations<AuditionFT> ops = dsForRW.createUpdateOperations(AuditionFT.class);
			ops.set("toStatus", AuditionFT.ToStatus.REFUSE);
			ops.set("t", System.currentTimeMillis());

			DBObject keys = BasicDBObjectBuilder.start().add("_id", 1).add("userId", 1).get();

			return findAndUpdate(q, ops, keys, new Callback() {

				@Override
				public void execute(Object obj) {
					BasicDBObject dbObj = (BasicDBObject) obj;

					// IMPORTANT 个人推送给企业：32、新的拒绝初试
					int type = PushText.AFT_REFUSE;
					int fromUserId = userId;
					int toUserId = dbObj.getInt("userId");
					Object objectId = dbObj.get("_id").toString();
					new PushText(type, fromUserId, toUserId, objectId).send();
				}
			});
		}
		return null;
	}

	@Override
	public Object notpass(int userId, List<ObjectId> idList) {
		User user = userManager.getUser(userId);
		if (0 != user.getCompanyId()) {
			DBObject q = new BasicDBObject(Mapper.ID_KEY, new BasicDBObject("$in", idList));
			DBObject values = new BasicDBObject();
			values.put("status", AuditionFT.Status.NOT_PASS);
			values.put("t", System.currentTimeMillis());
			DBObject ops = new BasicDBObject("$set", values);
			DBObject keys = BasicDBObjectBuilder.start().add("_id", 1).add("toUserId", 1).get();

			return super.findAndUpdate("a_aft", q, ops, keys, new Callback() {

				@Override
				public void execute(Object obj) {
					BasicDBObject dbObj = (BasicDBObject) obj;
					// IMPORTANT 企业推送到个人：23、初试不通过
					int type = PushText.CTU_AFT_NOT_PASS;
					int fromUserId = userId;// 企业用户Id
					int toUserId = dbObj.getInt("toUserId");// 个人用户Id
					Object objectId = dbObj.get("_id").toString();// 初试Id
					new PushText(type, fromUserId, toUserId, objectId).send();
				}
			});
		}
		return null;
	}

	@Override
	public Object pass(int userId, List<ObjectId> idList, long startTime) {
		// 获取用户信息
		User user = userManager.getUser(userId);
		// 非企业用户
		if (0 == user.getCompanyId()) {
			throw new ServiceException("权限不足，当前帐号无法进行“通过初试邀请”操作。");
		}

		DBObject q = new BasicDBObject(Mapper.ID_KEY, new BasicDBObject("$in", idList));
		DBObject values = new BasicDBObject();
		values.put("status", AuditionFT.Status.PASS);
		values.put("t", System.currentTimeMillis());
		DBObject ops = new BasicDBObject("$set", values);
		DBObject keys = null;

		List<Object> results = Lists.newArrayList();

		DBCollection dbColl = dsForRW.getDB().getCollection("a_aft");
		DBCursor cursor = null == keys ? dbColl.find(q) : dbColl.find(q, keys);
		while (cursor.hasNext()) {
			BasicDBObject dbObj = (BasicDBObject) cursor.next();
			if (1 == dbObj.getInt("isVideo")) {
				Object aftId = dbObj.get("_id").toString();
				Object artId = artManager.invite(userId, dbObj, startTime);

				// IMPORTANT 企业推送到个人：22、初试通过并邀请复试
				int type = PushText.CTU_AFT_PASS;
				int fromUserId = userId;// 企业用户Id
				int toUserId = dbObj.getInt("toUserId");// 个人用户Id
				Object objectId = artId;// 复试Id
				Object content = aftId;// 初试Id
				new PushText(type, fromUserId, toUserId, objectId, content).send();

				Map<String, Object> result = Maps.newHashMap();
				result.put("aftId", aftId);
				result.put("artId", artId);
				results.add(result);
			} else {
				Object aftId = dbObj.get("_id").toString();
				// Object artId = artManager.invite(userId, dbObj, startTime);

				// IMPORTANT 企业推送到个人：212、初试通过，直接线下复试
				int type = PushText.CTU_AFT_PASS_END;
				int fromUserId = userId;// 企业用户Id
				int toUserId = dbObj.getInt("toUserId");// 个人用户Id
				Object objectId = "";
				Object content = aftId;// 初试Id
				new PushText(type, fromUserId, toUserId, objectId, content).send();

				// IMPORTANT 企业推送到个人：212、初试通过，直接线下复试，文本推送
				String fromUserName = user.getNickname();
				String message = MessageFormat.format("恭喜您通过了我司“{0}”职位的初试！[{1}]", dbObj.getString("jobName"), user.getCompany()
						.getCompanyName());
				new PushText(fromUserId, fromUserName, toUserId, message).sendByFromUserId();

				Map<String, Object> result = Maps.newHashMap();
				result.put("aftId", aftId);
				// result.put("artId", artId);
				results.add(result);
			}
		}
		cursor.close();
		dbColl.update(q, ops, false, true);

		return results;

		// return super.findAndUpdate("a_aft", q, ops, keys, new Callback() {
		//
		// @Override
		// public void execute(Object obj) {
		// BasicDBObject dbObj = (BasicDBObject) obj;
		//
		// }
		// });
	}

	@Override
	public Object delete(int userId, List<ObjectId> idList) {
		if (null != idList && !idList.isEmpty()) {
			User user = userManager.getUser(userId);

			Query<AuditionFT> query = dsForRW.createQuery(AuditionFT.class).filter("_id in", idList);
			query.field(0 == user.getCompanyId() ? "toUserId" : "userId").equal(userId);
			UpdateOperations<AuditionFT> ops = dsForRW.createUpdateOperations(AuditionFT.class);
			ops.set(0 == user.getCompanyId() ? "idu" : "idc", 1);

			dsForRW.update(query, ops);

			return selectId("a_aft", query.getQueryObject());
		}
		return null;
	}

	@Override
	public Object invite(int userId, AuditionInviteExample param) {
		// 获取邀请用户数据
		User user = userManager.getUser(userId);
		// 是否允许面试邀请
		if (user.getCompanyId() != 0) {
			int toUserId = 0;
			if (0 == param.getUserId()) {
				User toUser = userManager.getUser(param.getTelephone());
				if (null == toUser) {
					String telephone = param.getTelephone();
					String password = StringUtil.randomPassword();
					toUser = userManager.createUser(telephone, password);

					toUserId = toUser.getUserId();

					// IMPORTANT 发送短信邀请
					pushManager.sendInvite(param.getTelephone(), user.getCompany().getName(), param.getTelephone(), password);
				} else {
					toUserId = toUser.getUserId();
				}
			} else {
				toUserId = param.getUserId();
			}
			ObjectId resumeId = null;
			String resumeName = null;
			try {
				if (null != param.getResume()) {
					param.getResume().setUserId(toUserId);
					dsForRW.save(param.getResume());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			JobApply jobApply = new JobApply();
			jobApply.setCompanyId(user.getCompany().getId());
			jobApply.setCompanyName(user.getCompany().getName());
			jobApply.setJobId(param.getJobId());
			jobApply.setUserId(userId);
			// jobApply.setUserName(userName);
			jobApply.setToUserId(toUserId);
			// jobApply.setToUserName(toUserName);

			if (null != param.getResume())
				jobApply.setToUserName(param.getResume().getP().getName());
			jobApply.setResumeId(resumeId);
			jobApply.setResumeName(resumeName);

			Object aftId = invite(userId, jobApply);

			// IMPORTANT 企业推送到个人：后台初试邀请
			// PushManager.audition(MsgCode.AUDITION_NEW, userId,
			// user.getNickname(), param.getUserId(), aftId.toString(), 0);

			return aftId;
		}

		return null;
	}

	@Override
	public Object invite(int userId, JobApply jobApply) {
		return invite(userId, jobApply, null);
	}

	@Override
	public Object invite(int userId, JobApply jobApply, List<Integer> examIdList) {
		JobVO job = jobManager.selectById(jobApply.getJobId());
		Profile profile = job.getProfile();
		List<Integer> idList = null == examIdList || examIdList.isEmpty() ? profile.getExamList() : examIdList;
		// IMPORTANT tjx：2014-12-12 11:53 职位没有设置题库或没有指定题库，则添加复试申请
		if (null == idList) {
			// 返回复试Id
			return artManager.invite(userId, jobApply, 0);
		} else {
			List<ExamVO> examList = examManager.selectByIdList(idList, false);
			List<DBObject> results = Lists.newArrayList();
			for (ExamVO exam : examList) {
				DBObject result = new BasicDBObject();
				result.put("examId", exam.getExamId());
				result.put("examName", exam.getExamName());
				result.put("examType", exam.getExamType());
				result.put("answer", new Object[] {});// 答案列表
				result.put("scores", new Object[] {});// 评分列表
				result.put("score", 0);// 节点总分
				result.put("status", 1);// 状态：1=进行中；2=已完成；3=已打分

				results.add(result);
			}

			DBObject jo = new BasicDBObject();
			jo.put(Mapper.ID_KEY, ObjectId.get());
			jo.put("companyId", jobApply.getCompanyId());
			jo.put("companyName", jobApply.getCompanyName());
			jo.put("userId", userId); // 企业用户Id

			jo.put("jobId", job.getJobId());// 职位Id
			jo.put("jobName", job.getJobName());// 职位名称

			jo.put("toUserId", jobApply.getToUserId());// 求职用户Id
			jo.put("toUserName", jobApply.getToUserName());// 求职用户姓名
			jo.put("resumeId", jobApply.getResumeId());// 简历Id
			jo.put("resumeName", jobApply.getResumeName());// 简历名称

			jo.put("isVideo", profile.getIsVideo());
			jo.put("isOffline", profile.getIsOffline());

			jo.put("results", results);// 答题结果

			jo.put("createTime", DateUtil.currentTimeSeconds());// 创建时间
			jo.put("startTime", DateUtil.currentTimeSeconds());// 开始时间
			jo.put("expiredTime", DateUtil.currentTimeSeconds());// 过期时间
			jo.put("endTime", DateUtil.currentTimeSeconds());// 结束时间

			jo.put("status", AuditionFT.Status.DEFAULT);// 企业状态：1=已邀请；2＝初试通过；3＝初试不通过
			jo.put("toStatus", AuditionFT.Status.DEFAULT);// 个人状态：1=已邀请；2=同意初试；3=拒绝初试；4=完成初试

			jo.put("idu", 0);// 用户是否删除
			jo.put("idc", 0);// 企业是否删除
			jo.put("t", System.currentTimeMillis());

			dsForRW.getDB().getCollection("a_aft").save(jo);

			return jo.get(Mapper.ID_KEY);
		}
	}

	@Override
	public Object get(ObjectId aftId) {
		BasicDBObject result = (BasicDBObject) dsForRW.getDB().getCollection("a_aft").findOne(new BasicDBObject("_id", aftId));
		if (null == result)
			throw new ServiceException("初试记录不存在");

		result.put("aftId", result.get("_id").toString());
		result.removeField("_id");
		result.removeField("idc");
		result.removeField("idu");
		int toUserId = result.getInt("toUserId");
		User toUser = userManager.getUser(toUserId);
		result.put("toNickname", toUser.getNickname());

		return result;
	}

	// IMPORTANT 2015-01-08：order by id、id<N、limit分页方式修改为order by
	// t、offset、limit方式
	@Override
	public List<DBObject> query(int userId, AuditionFTExample example) {
		User user = userManager.getUser(userId);

		DBObject ref = new BasicDBObject();
		ref.put(0 == user.getCompanyId() ? "idu" : "idc", 0);
		// 使用公司Id查询
		if (0 != example.getCompanyId())
			ref.put("companyId", example.getCompanyId());
		// 不使用公司Id查询，使用公司名称模糊查询
		if (0 == example.getCompanyId() && !StringUtil.isEmpty(example.getCompanyName()))
			ref.put("companyName", example.getCompanyName());
		// 只查询小于Id的数据
		// if (!StringUtils.isEmpty(example.getAftId()))
		// ref.put("_id", new BasicDBObject("$lt", new
		// ObjectId(example.getAftId())));
		// 使用职位Id查询
		if (null != example.getJobId())
			ref.put("jobId", example.getJobId());
		// 不使用职位Id查询，使用职位名称模糊查询
		if (null == example.getJobId() && !StringUtil.isEmpty(example.getCompanyName()))
			ref.put("jobName", example.getJobName());
		// 使用状态查询
		switch (example.getType()) {
		case 1:// 待交卷：status=1 and toStatus<=2
			ref.put("status", 1);
			ref.put("toStatus", new BasicDBObject("$lte", 2));
			break;
		case 2:// 已交卷：status=1 and toStatus=4
			ref.put("status", 1);
			ref.put("toStatus", 4);
			break;
		case 3:// 已答复：status!=1 or toStatus=3
			BasicDBList values = new BasicDBList();
			values.add(new BasicDBObject("status", new BasicDBObject("$ne", 1)));
			values.add(new BasicDBObject("toStatus", 3));
			ref.put("$or", values);
			break;
		}
		// 根据用户属性设置索引的选择
		ref.put(null == user.getCompany() ? "toUserId" : "userId", userId);
		// 根据Id降序排序、desc
		// BasicDBObject orderBy = new BasicDBObject("_id", -1);
		BasicDBObject orderBy = new BasicDBObject("t", -1);
		DBObject keys = new BasicDBObject(3);
		keys.put("results.answer", 0);
		keys.put("idc", 0);
		keys.put("idu", 0);
		List<DBObject> results = Lists.newArrayList();
		// DBCursor cursor = dsForRW.getDB().getCollection("a_aft").find(ref,
		// keys).sort(orderBy).limit(example.getPageSize());
		DBCursor cursor = dsForRW.getDB().getCollection("a_aft").find(ref, keys).sort(orderBy).skip(example.getOffset())
				.limit(example.getLimit());
		while (cursor.hasNext()) {
			BasicDBObject result = (BasicDBObject) cursor.next();
			result.put("aftId", result.get("_id").toString());
			result.removeField("_id");

			// 企业用户
			if (null != user.getCompany()) {
				int toUserId = result.getInt("toUserId");
				User toUser = userManager.getUser(toUserId);
				result.put("toNickname", toUser.getNickname());
			}
			// 普通用户
			else {
				result.put("userName", userManager.getUser(result.getInt("userId")).getNickname());
			}

			results.add(result);
		}

		return results;
	}

	@Override
	public void answer(int userId, ObjectId aftId, int examId, Object answer, int score) {
		ExamVO exam = examManager.get(examId, false);
		boolean isWrite = 1 == exam.getExamType();

		DBObject q = new BasicDBObject();
		q.put("_id", aftId);
		q.put("results.examId", examId);

		DBObject o = new BasicDBObject();
		// 笔试题提交答案则为已打分
		// status：0=无、1=进行中、2=已交卷、3：已评分
		o.put("$set", new BasicDBObject("results.$.status", isWrite ? 3 : 2));
		o.put("$inc", new BasicDBObject("results.$.score", score));
		o.put("$addToSet", new BasicDBObject("results.$.answer", new BasicDBObject("$each", answer)));

		dsForRW.getDB().getCollection("a_aft").update(q, o);

		// 初试是否已完成
		if (isCompleted(aftId)) {
			q = new BasicDBObject("_id", aftId);
			o = new BasicDBObject("$set", new BasicDBObject("toStatus", AuditionFT.ToStatus.FINISH));
			//
			dsForRW.getDB().getCollection("a_aft").update(q, o);

			BasicDBObject aft = (BasicDBObject) get(aftId);

			// IMPORTANT 个人推送给企业：31、初试已完成，新的初试交卷
			int type = PushText.AFT_ANSWER;
			int fromUserId = userId;
			int toUserId = aft.getInt("userId");
			Object objectId = aft.get("aftId").toString();
			new PushText(type, fromUserId, toUserId, objectId).send();
		}
	}

	@Override
	public void score(int userId, ObjectId aftId, int examId, int score) {
		int avgScore = getAvgScore(aftId, examId, score);

		DBObject dbObj = new BasicDBObject();
		dbObj.put("userId", userId);
		dbObj.put("score", score);
		dbObj.put("time", DateUtil.currentTimeSeconds());

		QueryBuilder q = QueryBuilder.start();
		q.and("_id").is(aftId);
		q.and("results.examId").is(examId);

		DBObject o = new BasicDBObject();
		// 2014-12-11 19:09 有一个打分则为已打分
		o.put("$set", new BasicDBObject("results.$.status", 3));
		o.put("$inc", new BasicDBObject("results.$.score", avgScore));
		o.put("$addToSet", new BasicDBObject("results.$.scores", new BasicDBObject("$each", new Object[] { dbObj })));

		dsForRW.getDB().getCollection("a_aft").update(q.get(), o);

	}

	/**
	 * 初试是否已完成
	 * 
	 * @param aftId
	 * @return
	 */
	boolean isCompleted(ObjectId aftId) {
		DBCollection coll = dsForRW.getDB().getCollection("a_aft");
		DBObject project = new BasicDBObject("$project", new BasicDBObject("results", 1));
		DBObject match = new BasicDBObject("$match", new BasicDBObject("_id", aftId));
		DBObject unwind = new BasicDBObject("$unwind", "$results");
		List<DBObject> pipeline = Arrays.asList(project, match, unwind);
		AggregationOutput output = coll.aggregate(pipeline);
		List<DBObject> objList = Lists.newArrayList(output.results());

		int completed = 0;
		for (DBObject obj : objList) {
			BasicDBObject results = (BasicDBObject) obj.get("results");
			int status = results.getInt("status");
			if (2 == status || 3 == status) {
				completed++;
			}
		}

		return objList.size() == completed;
	}

	/**
	 * 计算平均分
	 * 
	 * @param aftId
	 * @param examId
	 * @param score
	 * @return
	 */
	int getAvgScore(ObjectId aftId, int examId, int score) {
		int total = score;

		DBCollection coll = dsForRW.getDB().getCollection("a_aft");
		DBObject project = new BasicDBObject("$project", new BasicDBObject("results", 1));
		DBObject match = new BasicDBObject("$match", new BasicDBObject("_id", aftId));
		DBObject unwind = new BasicDBObject("$unwind", "$results");
		DBObject matchChild = new BasicDBObject("$match", new BasicDBObject("results.examId", examId));
		DBObject unwindChild = new BasicDBObject("$unwind", "$results.scores");
		List<DBObject> pipeline = Arrays.asList(project, match, unwind, matchChild, unwindChild);
		AggregationOutput output = coll.aggregate(pipeline);
		List<DBObject> objList = Lists.newArrayList(output.results());

		for (DBObject obj : objList) {
			DBObject results = (DBObject) obj.get("results");
			BasicDBObject scores = (BasicDBObject) results.get("scores");
			total += scores.getInt("score");
		}

		int avg = total / (objList.size() + 1);

		return avg;
	}

	@Override
	public void update(int userId, ObjectId aftId, List<Integer> examIdList) {
		List<ExamVO> examList = examManager.selectByIdList(examIdList, false);
		List<DBObject> results = Lists.newArrayList();
		for (ExamVO exam : examList) {
			DBObject result = new BasicDBObject();
			result.put("examId", exam.getExamId());
			result.put("examName", exam.getExamName());
			result.put("examType", exam.getExamType());
			result.put("answer", new Object[] {});// 答案列表
			result.put("scores", new Object[] {});// 评分列表
			result.put("score", 0);// 节点总分
			result.put("status", 1);// 状态：1=进行中；2=已完成；3=已打分

			results.add(result);
		}

		Query<AuditionFT> q = dsForRW.createQuery(AuditionFT.class);
		q.field(Mapper.ID_KEY).equal(aftId);
		q.field("userId").equal(userId);

		UpdateOperations<AuditionFT> ops = dsForRW.createUpdateOperations(AuditionFT.class);
		ops.set("results", results);

		dsForRW.findAndModify(q, ops);
	}

	@Override
	public PageVO query(int userId, int offset, int limit) {

		User user = userManager.getUser(userId);

		DBObject q = new BasicDBObject("companyId", user.getCompanyId());
		BasicDBObject orderBy = new BasicDBObject("_id", -1);
		DBObject keys = new BasicDBObject(3);
		keys.put("results.answer", 0);
		keys.put("idc", 0);
		keys.put("idu", 0);

		long total = dsForRW.getDB().getCollection("a_aft").count(q);
		List<DBObject> pageData = Lists.newArrayList();

		DBCursor cursor = dsForRW.getDB().getCollection("a_aft").find(q, keys).sort(orderBy).skip(offset).limit(limit);
		while (cursor.hasNext()) {
			BasicDBObject result = (BasicDBObject) cursor.next();
			result.put("aftId", result.get("_id").toString());
			result.removeField("_id");

			if (null != user.getCompany()) {
				int toUserId = result.getInt("toUserId");
				User toUser = userManager.getUser(toUserId);
				result.put("toNickname", toUser.getNickname());
			}

			pageData.add(result);
		}
		PageVO page = new PageVO();
		page.setTotal(total);
		page.setPageData(pageData);
		return page;
	}

}
