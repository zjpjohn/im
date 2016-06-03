package cn.xyz.mianshi.service.impl;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mapper.CompanyMapper;
import cn.xyz.mianshi.example.AuditionInviteExample;
import cn.xyz.mianshi.example.AuditionRTExample;
import cn.xyz.mianshi.service.AuditionRTManager;
import cn.xyz.mianshi.service.CompanyManager;
import cn.xyz.mianshi.service.JobManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.AuditionRT;
import cn.xyz.mianshi.vo.CompanyVO;
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
public class AuditionRTManagerImpl extends MongoRepository implements AuditionRTManager {

	@Autowired
	private JobManager jobManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private CompanyMapper companyMapper;
	@Autowired
	private KSMSServiceImpl pushManager;

	@Override
	public void reserveTime(int userId, ObjectId artId, long startTime) {
		QueryBuilder q = QueryBuilder.start();
		q.and("_id").is(artId);

		DBObject values = new BasicDBObject();
		values.put("startTime", startTime);
		values.put("t", System.currentTimeMillis());

		DBObject o = new BasicDBObject(1);
		o.put("$set", values);

		dsForRW.getDB().getCollection("a_art").update(q.get(), o);

		BasicDBObject dbObj = (BasicDBObject) dsForRW.getDB().getCollection("a_art").findOne(q.get(), new BasicDBObject("toUserId", 1));

		// IMPORTANT 企业推送到个人：25、重设复试开始时间
		int type = PushText.CTU_ART_TIME_SET;
		int fromUserId = userId;// 企业用户Id
		int toUserId = dbObj.getInt("toUserId");// 个人用户Id
		Object objectId = artId;// 复试Id
		Object content = startTime;
		new PushText(type, fromUserId, toUserId, objectId, content).send();
	}

	@Override
	public Object agree(int userId, List<ObjectId> idList) {
		User user = userManager.getUser(userId);
		if (0 == user.getCompanyId()) {
			Query<AuditionRT> q = dsForRW.createQuery(AuditionRT.class);
			// query.field("toUserId").equal(userId);
			q.filter("_id in", idList);
			q.field("toStatus").equal(AuditionRT.ToStatus.DEFAULT);
			q.field("status").equal(AuditionRT.Status.DEFAULT);

			UpdateOperations<AuditionRT> ops = dsForRW.createUpdateOperations(AuditionRT.class);
			ops.set("toStatus", AuditionRT.ToStatus.AGREE);
			ops.set("t", System.currentTimeMillis());

			DBObject keys = BasicDBObjectBuilder.start().add("_id", 1).add("userId", 1).get();

			return super.findAndUpdate(q, ops, keys, new Callback() {

				@Override
				public void execute(Object obj) {
					BasicDBObject dbObj = (BasicDBObject) obj;

					// IMPORTANT 个人推送给企业：33、同意复试
					int type = PushText.ART_AGREE;
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
	public Object refuse(int userId, List<ObjectId> idList) {
		User user = userManager.getUser(userId);
		if (0 == user.getCompanyId()) {
			Query<AuditionRT> q = dsForRW.createQuery(AuditionRT.class);
			q.filter("_id in", idList);
			q.filter("toStatus !=", AuditionRT.ToStatus.REFUSE);

			UpdateOperations<AuditionRT> ops = dsForRW.createUpdateOperations(AuditionRT.class);
			ops.set("toStatus", AuditionRT.ToStatus.REFUSE);
			ops.set("t", System.currentTimeMillis());

			DBObject keys = BasicDBObjectBuilder.start().add("_id", 1).add("userId", 1).get();

			return super.findAndUpdate(q, ops, keys, new Callback() {

				@Override
				public void execute(Object obj) {
					BasicDBObject dbObj = (BasicDBObject) obj;

					// IMPORTANT 个人推送给企业：34、拒绝复试
					int type = PushText.ART_REFUSE;
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
			values.put("status", AuditionRT.Status.NOT_PASS);
			values.put("t", System.currentTimeMillis());
			DBObject ops = new BasicDBObject("$set", values);
			DBObject keys = BasicDBObjectBuilder.start().add("_id", 1).add("toUserId", 1).get();

			return super.findAndUpdate("a_art", q, ops, keys, new Callback() {

				@Override
				public void execute(Object obj) {
					BasicDBObject dbObj = (BasicDBObject) obj;

					// IMPORTANT 企业推送到个人：27、复试不通过
					int type = PushText.CTU_ART_NOT_PASS;
					int fromUserId = userId;// 企业用户Id
					int toUserId = dbObj.getInt("toUserId");// 个人用户Id
					Object objectId = dbObj.get("_id").toString();
					new PushText(type, fromUserId, toUserId, objectId).send();
				}
			});
		}
		return null;
	}

	@Override
	public Object pass(int userId, List<ObjectId> idList) {
		User user = userManager.getUser(userId);
		if (0 != user.getCompanyId()) {
			DBObject q = new BasicDBObject(Mapper.ID_KEY, new BasicDBObject("$in", idList));
			DBObject values = new BasicDBObject();
			values.put("status", AuditionRT.Status.PASS);
			values.put("t", System.currentTimeMillis());
			DBObject ops = new BasicDBObject("$set", values);
			DBObject keys = null;

			return super.findAndUpdate("a_art", q, ops, keys, new Callback() {

				@Override
				public void execute(Object obj) {
					BasicDBObject dbObj = (BasicDBObject) obj;

					// IMPORTANT 企业推送到个人：26、复试通过
					int type = PushText.CTU_ART_PASS;
					int fromUserId = userId;// 企业用户Id
					int toUserId = dbObj.getInt("toUserId");// 个人用户Id
					Object objectId = dbObj.get("_id").toString();
					new PushText(type, fromUserId, toUserId, objectId).send();

					// IMPORTANT 企业推送到个人：26、复试通过，文本推送
					String fromUserName = user.getNickname();
					String message = MessageFormat.format("恭喜您通过了我司“{0}”职位的面试！[{1}]", dbObj.getString("jobName"), user.getCompany()
							.getCompanyName());
					new PushText(fromUserId, fromUserName, toUserId, message).sendByFromUserId();
				}
			});

		}
		return null;
	}

	@Override
	public void start(int userId, ObjectId artId) {
		BasicDBObject dbObj = (BasicDBObject) get(artId);
		// 已支付
		if (1 == dbObj.getInt("paid")) {
			QueryBuilder q = QueryBuilder.start();
			q.and("_id").is(artId);
			q.and("userId").is(userId);

			DBObject o = new BasicDBObject(1);
			o.put("$inc", new BasicDBObject("meetingCount", 1));

			dsForRW.getDB().getCollection("a_art").update(q.get(), o);
		}
		// 未支付
		else {
			// 获取公司信息
			CompanyVO company = companyManager.get(dbObj.getInt("companyId"));
			// 会员已过期
			if (System.currentTimeMillis() / 1000 > company.getPayEndTime()) {
				int vbalance = company.getVbalance() - 1;
				// 余额足够支付本次视频会议
				if (vbalance >= 0) {
					Map<String, Object> parameter = Maps.newHashMap();
					parameter.put("v", -1);
					parameter.put("companyId", company.getCompanyId());
					if (companyMapper.updateOthers(parameter) != 1)
						throw new ServiceException(8099100, "支付失败");
				} else {
					throw new ServiceException(8099100, "视频会议包时已过期或视频会议次数不足");
				}
			}
			// 会员未过期
			else {

			}
			Query<AuditionRT> q = dsForRW.createQuery(AuditionRT.class).field("_id").equal(artId);
			UpdateOperations<AuditionRT> ops = dsForRW.createUpdateOperations(AuditionRT.class).set("paid", 1);
			dsForRW.update(q, ops);
		}
	}

	@Override
	public Object delete(int userId, List<ObjectId> idList) {
		User user = userManager.getUser(userId);

		DBObject q = new BasicDBObject("_id", new BasicDBObject("$in", idList));
		DBObject o = new BasicDBObject(1);
		if (0 == user.getCompanyId()) {
			q.put("toUserId", userId);
			o.put("idu", 1);
		} else {
			q.put("userId", userId);
			o.put("idc", 1);
		}

		dsForRW.getDB().getCollection("a_art").update(q, new BasicDBObject("$set", o));

		return selectId("a_art", q);
	}

	@Override
	public Object get(ObjectId id) {
		QueryBuilder q = QueryBuilder.start("_id").is(id);
		DBObject result = dsForRW.getDB().getCollection("a_art").findOne(q.get());
		if (null == result)
			throw new ServiceException("复试记录不存在");

		result.put("artId", result.get("_id").toString());
		result.removeField("_id");
		result.removeField("idc");
		result.removeField("idu");
		return result;
	}

	@Override
	public Object gets(List<ObjectId> idList) {
		List<DBObject> results = Lists.newArrayList();

		DBObject o = new BasicDBObject("_id", new BasicDBObject("$in", idList));
		DBCursor cursor = dsForRW.getDB().getCollection("a_art").find(o);
		while (cursor.hasNext()) {
			DBObject result = cursor.next();
			result.put("artId", result.get("_id").toString());
			result.removeField("_id");
			result.removeField("idc");
			result.removeField("idu");

			results.add(result);
		}
		cursor.close();

		return results;
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
			jobApply.setToUserId(toUserId);
			if (null != param.getResume())
				jobApply.setToUserName(param.getResume().getP().getName());
			jobApply.setResumeId(resumeId);
			jobApply.setResumeName(resumeName);

			Object aftId = invite(userId, jobApply, 0);

			// PushManager.audition(MsgCode.AUDITION_NEW, userId,
			// user.getNickname(), param.getUserId(), aftId.toString(), 0);

			// IMPORTANT 企业推送到个人：24、后台复试邀请；tjx：加个人推送到企业(⊙ｏ⊙)(⊙ｏ⊙)
			int type = PushText.CTU_ART_INVITE;
			int fromUserId = userId;// 企业用户Id
			Object objectId = aftId;

			new PushText(type, fromUserId, toUserId, objectId).send();
			new PushText(type, toUserId, fromUserId, objectId).send();

			return aftId;
		}

		return null;
	}

	@Override
	public Object invite(int userId, DBObject dbObj, long startTime) {
		ObjectId artId = ObjectId.get();
		// 生成复试记录
		DBObject jo = new BasicDBObject();
		jo.put(Mapper.ID_KEY, artId);
		jo.put("aftId", dbObj.get("_id").toString());// 复试对应的初试

		jo.put("companyId", dbObj.get("companyId"));
		jo.put("companyName", dbObj.get("companyName"));
		jo.put("userId", userId); // 企业用户Id

		jo.put("jobId", dbObj.get("jobId"));// 职位Id
		jo.put("jobName", dbObj.get("jobName"));// 职位名称

		jo.put("toUserId", dbObj.get("toUserId"));// 求职用户Id
		jo.put("toUserName", dbObj.get("toUserName"));// 求职用户姓名
		jo.put("resumeId", dbObj.get("resumeId"));// 简历Id
		jo.put("resumeName", dbObj.get("resumeName"));// 简历名称

		jo.put("isVideo", dbObj.get("isVideo"));
		jo.put("isOffline", dbObj.get("isOffline"));
		jo.put("results", new Object[] {});// 答题结果

		jo.put("createTime", DateUtil.currentTimeSeconds());// 创建时间
		jo.put("startTime", startTime);// 开始时间
		jo.put("expiredTime", 0);// 过期时间
		jo.put("endTime", 0);// 结束时间

		jo.put("paid", 0);// 已支付：0=否；1=是
		jo.put("status", AuditionRT.Status.DEFAULT);// 企业状态：1=已邀请；2＝复试通过；3＝复试不通过
		jo.put("toStatus", AuditionRT.ToStatus.DEFAULT);// 个人状态：1=预约时间；2=同意复试；3=拒绝复试
		jo.put("finalStatus", AuditionRT.FinalStatus.DEFAULT);// 个人状态：1=未答复；2=同意入职；3=拒绝入职
		jo.put("meetingCount", 0);// 会议次数

		jo.put("idu", 0);// 用户是否删除
		jo.put("idc", 0);// 企业是否删除
		jo.put("t", System.currentTimeMillis());

		dsForRW.getDB().getCollection("a_art").save(jo);

		return artId;
	}

	@Override
	public Object invite(int userId, JobApply jobApply, long startTime) {
		ObjectId artId = ObjectId.get();

		JobVO job = jobManager.selectById(jobApply.getJobId());
		Profile profile = job.getProfile();
		// 生成复试记录
		DBObject jo = new BasicDBObject();
		jo.put(Mapper.ID_KEY, artId);

		jo.put("companyId", jobApply.getCompanyId());
		jo.put("companyName", jobApply.getCompanyName());
		jo.put("userId", userId); // 企业用户Id

		jo.put("jobId", jobApply.getJobId());// 职位Id
		jo.put("jobName", job.getJobName());// 职位名称

		jo.put("toUserId", jobApply.getToUserId());// 求职用户Id
		jo.put("toUserName", jobApply.getToUserName());// 求职用户姓名
		jo.put("resumeId", jobApply.getResumeId());// 简历Id
		jo.put("resumeName", jobApply.getResumeName());// 简历名称

		jo.put("isVideo", profile.getIsVideo());
		jo.put("isOffline", profile.getIsOffline());
		jo.put("results", new Object[] {});// 答题结果

		jo.put("createTime", DateUtil.currentTimeSeconds());// 创建时间
		jo.put("startTime", startTime);// 开始时间
		jo.put("expiredTime", 0);// 过期时间
		jo.put("endTime", 0);// 结束时间

		jo.put("paid", 0);// 已支付：0=否；1=是
		jo.put("status", AuditionRT.Status.DEFAULT);// 企业状态：1=已邀请；2＝复试通过；3＝复试不通过
		jo.put("toStatus", AuditionRT.ToStatus.DEFAULT);// 个人状态：1=预约时间；2=同意复试；3=拒绝复试
		jo.put("finalStatus", AuditionRT.FinalStatus.DEFAULT);// 个人状态：1=未答复；2=同意入职；3=拒绝入职
		jo.put("meetingCount", 0);// 会议次数

		jo.put("idu", 0);// 用户是否删除
		jo.put("idc", 0);// 企业是否删除
		jo.put("t", System.currentTimeMillis());

		dsForRW.getDB().getCollection("a_art").save(jo);

		return artId;
	}

	// IMPORTANT 2015-01-08：order by id、id<N、limit分页方式修改为order by
	// t、offset、limit方式
	@Override
	public Object query(int userId, AuditionRTExample example) {
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
		// if (!StringUtils.isEmpty(example.getArtId()))
		// ref.put("_id", new BasicDBObject("$lt", new
		// ObjectId(example.getArtId())));
		// 使用职位Id查询
		if (null != example.getJobId())
			ref.put("jobId", example.getJobId());
		// 不使用职位Id查询，使用职位名称模糊查询
		if (null == example.getJobId() && !StringUtil.isEmpty(example.getCompanyName()))
			ref.put("jobName", example.getJobName());
		// 使用状态查询
		switch (example.getType()) {
		case 1:// 预约时间
			ref.put("status", 1);
			ref.put("toStatus", 1);
			break;
		case 2:// 进 行 中
			ref.put("status", 1);
			ref.put("toStatus", 2);
			break;
		case 3: // 已 答 复
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

		List<DBObject> results = Lists.newArrayList();
		// DBCursor cursor =
		// dsForRW.getDB().getCollection("a_art").find(ref).sort(orderBy).limit(example.getPageSize());
		DBCursor cursor = dsForRW.getDB().getCollection("a_art").find(ref).sort(orderBy).skip(example.getOffset())
				.limit(example.getLimit());
		while (cursor.hasNext()) {
			BasicDBObject result = (BasicDBObject) cursor.next();
			result.put("artId", result.get("_id").toString());
			result.removeField("_id");
			result.removeField("idc");
			result.removeField("idu");

			if (null != user.getCompany()) {
				int toUserId = result.getInt("toUserId");
				User toUser = userManager.getUser(toUserId);
				result.put("toNickname", toUser.getNickname());
			}// 普通用户
			else {
				result.put("userName", userManager.getUser(result.getInt("userId")).getNickname());
			}
			results.add(result);
		}

		return results;
	}

	@Override
	public PageVO query(int userId, int offset, int limit) {
		User user = userManager.getUser(userId);
		DBObject q = new BasicDBObject("companyId", user.getCompanyId());
		BasicDBObject orderBy = new BasicDBObject("_id", -1);
		long total = dsForRW.getDB().getCollection("a_art").count(q);
		List<DBObject> pageData = Lists.newArrayList();
		DBCursor cursor = dsForRW.getDB().getCollection("a_art").find(q).sort(orderBy).skip(offset).limit(limit);
		while (cursor.hasNext()) {
			BasicDBObject result = (BasicDBObject) cursor.next();
			result.put("artId", result.get("_id").toString());
			result.removeField("_id");
			result.removeField("idc");
			result.removeField("idu");

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

	@Override
	public void score(int userId, ObjectId artId, int score) {
		int avgScore = getAvgScore(artId, score);

		DBObject dbObj = new BasicDBObject();
		dbObj.put("userId", userId);
		dbObj.put("score", score);
		dbObj.put("time", DateUtil.currentTimeSeconds());

		DBObject q = new BasicDBObject("_id", artId);
		DBObject o = new BasicDBObject();
		o.put("$set", new BasicDBObject("results.score", avgScore));
		o.put("$addToSet", new BasicDBObject("results.scores", new BasicDBObject("$each", new Object[] { dbObj })));

		dsForRW.getDB().getCollection("a_art").update(q, o);
	}

	int getAvgScore(ObjectId artId, int score) {
		int total = score;

		DBCollection coll = dsForRW.getDB().getCollection("a_art");
		DBObject project = new BasicDBObject("$project", new BasicDBObject("results", 1));
		DBObject match = new BasicDBObject("$match", new BasicDBObject("_id", artId));
		DBObject unwind = new BasicDBObject("$unwind", "$results.scores");
		List<DBObject> pipeline = Arrays.asList(project, match, unwind);
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

}
