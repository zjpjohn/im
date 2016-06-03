package cn.xyz.mianshi.service.impl;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.example.JobApplyExample;
import cn.xyz.mianshi.service.AuditionFTManager;
import cn.xyz.mianshi.service.AuditionRTManager;
import cn.xyz.mianshi.service.JobApplyManager;
import cn.xyz.mianshi.service.JobManager;
import cn.xyz.mianshi.service.ResumeManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.JobApply;
import cn.xyz.mianshi.vo.JobVO;
import cn.xyz.mianshi.vo.PushText;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.cv.ResumeV2;
import cn.xyz.repository.MongoRepository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Service
public class JobApplyManagerImpl extends MongoRepository implements JobApplyManager {

	@Autowired
	private AuditionFTManager aftManager;
	@Autowired
	private AuditionRTManager artManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private ResumeManager resumeManager;
	@Autowired
	private JobManager jobManager;

	@Override
	public Object agree(int userId, List<ObjectId> idList, long startTime) {
		// 职位申请列表
		List<Object> results = Lists.newArrayList();

		// 获取用户信息
		User user = userManager.getUser(userId);
		// 企业用户
		if (0 == user.getCompanyId()) {
			throw new ServiceException("权限不足，当前帐号无法进行“同意职位申请”操作。");
		}

		Query<JobApply> q = dsForRW.createQuery(JobApply.class);
		q.field("companyId").equal(user.getCompanyId());
		q.field("status").equal(JobApply.Status.APPLY);
		q.filter("_id in", idList);

		UpdateOperations<JobApply> ops = dsForRW.createUpdateOperations(JobApply.class);
		ops.set("status", JobApply.Status.AGREE).set("read", 1);

		// “idList”中未处理的职位申请记录
		List<JobApply> applyList = q.asList();
		//
		if (null == applyList || applyList.isEmpty()) {

		} else {
			for (JobApply apply : applyList) {
				JobVO job = jobManager.get(apply.getJobId());

				Object aftId = "";
				Object artId = "";
				int type;
				int fromUserId = userId;// 企业用户Id
				int toUserId = apply.getToUserId();// 个人用户Id
				Object objectId;// 初试Id或复试Id或没有
				Object content = apply.getApplyId();// 职位申请Id

				// 复试：职位未设置题库或同意职位申请未指定题库
				if (null == job.getProfile().getExamList() || job.getProfile().getExamList().isEmpty()) {
					if (1 == job.getProfile().getIsVideo()) {
						// IMPORTANT 企业推送到个人：210、申请通过，直接面试
						artId = artManager.invite(userId, apply, startTime);

						type = PushText.CTU_APPLY_PASS_ART;
						objectId = artId;
					} else {
						// IMPORTANT 企业推送到个人：211、申请通过，直接线下复试
						type = PushText.CTU_APPLY_PASS_END;
						objectId = "";

						// IMPORTANT 企业推送到个人：211、申请通过，直接线下复试，文本推送
						String fromUserName = user.getNickname();
						String message = MessageFormat.format("恭喜您通过了我司“{0}”职位的申请！[{1}]", job.getJobName(), job.getCo().getName());
						new PushText(fromUserId, fromUserName, toUserId, message).sendByFromUserId();
					}
				}
				// 初试：
				else {
					// IMPORTANT 企业推送到个人：201、职位申请通过并邀请初试
					aftId = aftManager.invite(userId, apply);

					type = PushText.CTU_APPLY_AGREE;
					objectId = aftId;
				}

				new PushText(type, fromUserId, toUserId, objectId, content).send();

				// IMPORTANT 修改职位已读数：JobVO.count.read+=N
				updateCount(apply.getJobId());

				// 结果
				Map<String, Object> result = Maps.newHashMap();
				result.put("applyId", apply.getApplyId());
				result.put("aftId", aftId);
				result.put("artId", artId);
				results.add(result);
			}

			// 批量更新职位申请状态为企业已同意并已读
			dsForRW.update(q, ops);
		}

		return results;

	}

	@Override
	public Object refuse(int userId, List<ObjectId> idList) {
		List<ObjectId> results = Lists.newArrayList();
		// 获取用户信息
		User user = userManager.getUser(userId);
		// 企业用户
		if (0 != user.getCompanyId()) {
			Query<JobApply> q = dsForRW.createQuery(JobApply.class);
			q.field("companyId").equal(user.getCompanyId());
			q.field("status").equal(1);
			q.filter("_id in", idList);
			// “idList”中未处理的职位申请记录
			List<JobApply> applyList = q.asList();
			if (!applyList.isEmpty()) {
				applyList.forEach(jobApply -> {
					results.add(jobApply.getApplyId());
				});

				// 批量更新职位申请状态为企业已拒绝并已读
				UpdateOperations<JobApply> ops = dsForRW.createUpdateOperations(JobApply.class).set("status", JobApply.Status.REFUSE)
						.set("read", 1);
				dsForRW.update(q, ops);

				// IMPORTANT 修改职位已读数：JobVO.count.read+=N
				applyList.forEach(apply -> {
					updateCount(apply.getJobId());
				});
			}
		}

		return results;
	}

	@Override
	public Object save(int userId, ObjectId resumeId, List<ObjectId> jobIdList) {
		User user = userManager.getUser(userId);
		ResumeV2 resume = resumeManager.selectById(userId, resumeId);
		List<Object> results = Lists.newArrayList();
		List<JobApply> entities = Lists.newArrayList();

		// IMPORTANT 重复投递限制已关闭
		// Object[] avaJobIdList = getAvailableJobId(userId, jobIdList);
		Object[] avaJobIdList = jobIdList.toArray();
		List<JobVO> jobList = dsForRW.createQuery(JobVO.class).filter("_id in", avaJobIdList).asList();

		jobList.forEach(job -> {
			if (1 == job.getStatus()) {
				// IMPORTANT tjx-15-01-02：应聘记录里，userId应为HR的
				JobApply apply = new JobApply();
				apply.setApplyId(ObjectId.get());
				apply.setCompanyId(job.getCo().getId());
				apply.setCompanyName(job.getCo().getName());
				apply.setJobId(job.getJobId());
				apply.setJobName(job.getJobName());
				apply.setUserId(job.getUserId());
				apply.setUserName(job.getUserName());
				apply.setToUserId(userId);
				// 优先取简历，而后取昵称
				apply.setToUserName(null == resume ? user.getNickname() : resume.getP().getName());
				if (null != resume) {
					apply.setResumeId(resume.getResumeId());
					apply.setResumeName(resume.getResumeName());
				}
				apply.setTime(DateUtil.currentTimeSeconds());
				apply.setRead(0);
				apply.setTagId(0);
				apply.setStatus(1);
				apply.setIdc(0);
				apply.setIdu(0);
				entities.add(apply);

				// IMPORTANT 个人推送给企业：30、职位申请
				int type = PushText.APPLY_SAVE;
				int fromUserId = userId;
				int toUserId = job.getUserId();
				Object objectId = job.getJobId();
				Object content = apply.getApplyId();
				new PushText(type, fromUserId, toUserId, objectId, content).send();
			}
		});

		// 保存申请记录
		dsForRW.save(entities).forEach(result -> {
			results.add(result.getId());
		});

		// 更新职位活跃度与申请数
		Query<JobVO> query = dsForRW.createQuery(JobVO.class).filter("_id in", avaJobIdList);
		UpdateOperations<JobVO> ops = dsForRW.createUpdateOperations(JobVO.class);
		ops.inc("active", 1);
		ops.inc("count.active", 1);
		ops.inc("count.apply", 1);
		dsForRW.update(query, ops);

		return results;
	}

	@Override
	public Object delete(int userId, List<ObjectId> idList) {
		if (null != idList && 0 != idList.size()) {
			Query<JobApply> q = dsForRW.createQuery(JobApply.class).filter("_id in", idList);
			dsForRW.delete(q);

			return selectId("job_apply", q.getQueryObject());
		}
		return null;
	}

	@Override
	public void read(int userId, List<ObjectId> applyIdList, int read) {
		// Query<JobApply> q =
		// dsForRW.createQuery(JobApply.class).filter("_id in",
		// applyIdList).field("read").equal(0 == read ? 1 : 0);
		// UpdateOperations<JobApply> ops =
		// dsForRW.createUpdateOperations(JobApply.class).set("read", read);
		// dsForRW.update(q, ops);
		//
		// List<JobApply> jobApplyList = q.asList();
		// jobApplyList.forEach(jobApply -> {
		// updateJobRead(jobApply.getJobId(), read);
		// });
	}

	@Override
	public List<JobApply> selectByExample(int userId, int status, ObjectId applyId, int pageSize) {
		User user = userManager.getUser(userId);

		Query<JobApply> q = dsForRW.createQuery(JobApply.class).order("-_id").field("status").equal(status);
		if (0 == user.getCompanyId())
			q.field("userId").equal(userId);
		else
			q.field("companyId").equal(user.getCompanyId());
		if (null != applyId)
			q.field(Mapper.ID_KEY).lessThan(applyId);

		List<JobApply> list = q.limit(pageSize).asList();
		list.forEach(apply -> {
			Object personal = resumeManager.selectById(apply.getToUserId(), apply.getResumeId(), "p");
			apply.setPersonal(personal);
		});

		return list;
	}

	@Override
	public List<JobApply> selectByExample(int userId, JobApplyExample example) {
		User user = userManager.getUser(userId);
		Query<JobApply> q = dsForRW.createQuery(JobApply.class).order("-_id");
		if (0 == user.getCompanyId())
			q.field("toUserId").equal(userId);
		else
			q.field("companyId").equal(user.getCompanyId());
		if (null != example.getApplyId())
			q.field(Mapper.ID_KEY).lessThan(example.getApplyId());
		if (null != example.getJobId())
			q.field("jobId").equal(example.getJobId());
		if (null != example.getRead() && 2 != example.getRead())
			q.field("read").equal(example.getRead());
		if (null != example.getStatus() && 0 != example.getStatus())
			q.field("status").equal(example.getStatus());
		if (null != example.getTagId() && 0 != example.getTagId())
			q.field("tagId").equal(example.getTagId());

		List<JobApply> list = q.limit(example.getPageSize()).asList();
		list.forEach(apply -> {
			Object personal = resumeManager.selectById(apply.getToUserId(), apply.getResumeId(), "p");
			apply.setPersonal(personal);
		});

		return list;
	}

	@Override
	public JobApply selectById(ObjectId id) {
		JobApply jobApply = dsForRW.createQuery(JobApply.class).field(Mapper.ID_KEY).equal(id).get();
		Object personal = resumeManager.selectById(jobApply.getToUserId(), jobApply.getResumeId(), "p");
		jobApply.setPersonal(personal);

		return jobApply;
	}

	@Override
	public void tag(ObjectId id, int tagId) {
		Query<JobApply> q = dsForRW.createQuery(JobApply.class).field(Mapper.ID_KEY).equal(id);
		UpdateOperations<JobApply> ops = dsForRW.createUpdateOperations(JobApply.class).set("tagId", tagId);

		dsForRW.findAndModify(q, ops);
	}

	/**
	 * “jobIdList”可用的职位Id列表
	 * 
	 * @param userId
	 * @param jobIdList
	 * @return
	 */
	public Object[] getAvailableJobId(int userId, List<ObjectId> jobIdList) {
		List<ObjectId> last7dJobId = getLast7dJobId(userId, jobIdList);
		HashSet<ObjectId> leftSet = Sets.newHashSet(jobIdList);
		HashSet<ObjectId> rightSet = Sets.newHashSet(last7dJobId);

		Object[] result = Sets.difference(leftSet, rightSet).toArray();

		return result;
	}

	/**
	 * 最近7天内申请过的职位Id列表
	 * 
	 * @param userId
	 * @param jobIdList
	 * @return
	 */
	public List<ObjectId> getLast7dJobId(int userId, List<ObjectId> jobIdList) {
		List<ObjectId> results = Lists.newArrayList();

		Query<JobApply> q = dsForRW.createQuery(JobApply.class);
		q.filter("jobId in", jobIdList);
		q.field("userId").equal(userId);
		// 7天前申请过的职位
		// q.filter("time <=", System.currentTimeMillis() / 1000 - 3600 * 24 *
		// 7);
		// 最近7天内申请过的职位
		q.filter("time >=", System.currentTimeMillis() / 1000 - 3600 * 24 * 7);
		q.asList().forEach(job -> {
			results.add(job.getJobId());
		});

		return results;
	}

	private void updateCount(ObjectId jobId) {
		Query<JobVO> q = dsForRW.createQuery(JobVO.class);
		UpdateOperations<JobVO> ops = dsForRW.createUpdateOperations(JobVO.class).inc("count.read", 1);
		dsForRW.update(q, ops);
	}

}
