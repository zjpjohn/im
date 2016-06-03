package cn.xyz.mianshi.service.impl;

import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.example.JobExample;
import cn.xyz.mianshi.service.AuditionFTManager;
import cn.xyz.mianshi.service.JobManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.JobVO;
import cn.xyz.mianshi.vo.PushText;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.FriendsRepository;
import cn.xyz.repository.MongoRepository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Service
public class JobManagerImpl extends MongoRepository implements JobManager {

	@Autowired
	private AuditionFTManager auditionManager;
	@Autowired
	private FriendsRepository friendsRepository;
	@Autowired
	private UserManager userManager;

	@Override
	public Object save(int userId, JobExample example) {
		User user = userManager.getUser(userId);
		if (null == user || 0 == user.getCompanyId())
			throw new ServiceException("权限不足");

		JobVO.Profile profile = new JobVO.Profile();
		profile.setIsOffline(example.getIsOffline());
		profile.setIsVideo(example.getIsVideo());
		profile.setExamList(example.getExamList());

		JobVO entity = new JobVO();
		entity.setAreaId(example.getAreaId());
		entity.setCityId(example.getCityId());
		entity.setCo(new JobVO.Company(user.getCompany()));
		// job.setCount(count);
		entity.setDescription(example.getDescription());
		entity.setDip(example.getDiploma());
		// job.setExpiredTime(expiredTime);
		// job.setJobId(jobId);
		entity.setJobName(example.getJobName());
		entity.setProfile(profile);
		entity.setProvinceId(example.getProvinceId());
		entity.setPublishTime(System.currentTimeMillis() / 1000);
		entity.setRefreshTime(entity.getPublishTime());
		entity.setSalary(example.getSalary());
		entity.setStatus(1);
		entity.setUserId(userId);
		entity.setUserName(user.getNickname());
		entity.setVacancy(example.getVacancy());
		entity.setWorkExp(example.getWorkExp());
		entity.setAddress(StringUtil.isEmpty(example.getAddress()) ? user.getCompany().getAddress() : example.getAddress());
		entity.setFnId(example.getFnId());

		Object jobId = dsForRW.save(entity).getId();

		HashMap<String, Object> data = Maps.newHashMap();
		data.put("jobId", jobId);
		data.put("jobName", example.getJobName());

		return data;
	}

	@Override
	public Object republish(int userId, JobExample example) {
		delete(userId, Lists.newArrayList(example.getJobId()));
		return save(userId, example);
	}

	@Override
	public Object delete(int userId, List<ObjectId> idList) {
		User user = userManager.getUser(userId);
		if (0 != user.getCompanyId()) {
			Query<JobVO> q = dsForRW.createQuery(JobVO.class).field("co.id").equal(user.getCompanyId()).filter("_id in", idList);
			Object result = selectId("a_job", q.getQueryObject());

			dsForRW.delete(q);

			idList.forEach(jobId -> {
				// IMPORTANT 企业推送到企业：38、删除职位
				int type = PushText.JOB_DELETE;
				int fromUserId = userId;// 企业用户Id
				int toUserId = userId;
				Object objectId = jobId;
				new PushText(type, fromUserId, toUserId, objectId).send();
			});

			return result;
		}
		return null;
	}

	@Override
	public Object updateOne(int userId, JobExample example) {
		User user = userManager.getUser(userId);
		if (0 == user.getCompanyId())
			return null;

		Query<JobVO> q = dsForRW.createQuery(JobVO.class);
		q.field("_id").equal(example.getJobId());
		q.field("co.id").equal(user.getCompanyId());

		UpdateOperations<JobVO> ops = dsForRW.createUpdateOperations(JobVO.class);

		if (!StringUtil.isEmpty(example.getJobName()))
			ops.set("jobName", example.getJobName());
		if (!StringUtil.isEmpty(example.getDescription()))
			ops.set("description", example.getDescription());

		if (0 != example.getCountryId())
			ops.set("countryId", example.getCountryId());
		if (0 != example.getProvinceId())
			ops.set("provinceId", example.getProvinceId());
		if (0 != example.getCityId())
			ops.set("cityId", example.getCityId());
		if (0 != example.getAreaId())
			ops.set("areaId", example.getAreaId());

		if (0 != example.getWorkExp())
			ops.set("workExp", example.getWorkExp());
		if (0 != example.getDiploma())
			ops.set("dip", example.getDiploma());
		if (0 != example.getSalary())
			ops.set("salary", example.getSalary());
		if (0 != example.getVacancy())
			ops.set("vacancy", example.getVacancy());

		if (0 != example.getRefreshTime())
			ops.set("refreshTime", example.getRefreshTime());
		if (0 != example.getStatus())
			ops.set("status", example.getStatus());

		if (3 == example.getStatus()) {
			ops.set("count.active", 0);
			ops.set("count.apply", 0);
			ops.set("count.browse", 0);
			ops.set("count.collect", 0);
			ops.set("count.read", 0);
		}
		if (0 != example.getFnId())
			ops.set("fnId", example.getFnId());
		// if (null != example.getExamList() &&
		// !example.getExamList().isEmpty()) {
		// 不传则不更新，空数组则置空
		if (null != example.getExamList()) {
			ops.set("profile.examList", example.getExamList());
		}

		ops.set("profile.isVideo", example.getIsVideo());
		ops.set("profile.isOffline", example.getIsOffline());

		return dsForRW.findAndModify(q, ops);
	}

	@Override
	public Object updateMulti(int userId, List<ObjectId> idList, Long refreshTime) {
		Query<JobVO> q = dsForRW.createQuery(JobVO.class).filter("_id in", idList);
		UpdateOperations<JobVO> ops = dsForRW.createUpdateOperations(JobVO.class).set("refreshTime", refreshTime);
		dsForRW.update(q, ops);

		return selectId("a_job", q.getQueryObject());
	}

	@Override
	public Object updateMulti(int userId, List<ObjectId> idList, Integer status) {
		Query<JobVO> q = dsForRW.createQuery(JobVO.class).filter("_id in", idList);
		UpdateOperations<JobVO> ops = dsForRW.createUpdateOperations(JobVO.class).set("status", status);
		if (3 == status) {
			ops.set("count.active", 0);
			ops.set("count.apply", 0);
			ops.set("count.browse", 0);
			ops.set("count.collect", 0);
			ops.set("count.read", 0);
		}
		DBObject keys = new BasicDBObject("_id", 1);

		return findAndUpdate(q, ops, keys, new Callback() {

			@Override
			public void execute(Object obj) {
				BasicDBObject dbObj = (BasicDBObject) obj;

				// IMPORTANT 企业推送到企业：35、36、37：暂停职位、恢复职位、取消职位
				int type = 0;
				if (3 == status) // 取消
					type = PushText.JOB_CANCEL;
				else if (1 == status) // 正常
					type = PushText.JOB_RESUME;
				else if (2 == status) // 暂停
					type = PushText.JOB_PAUSE;

				int fromUserId = userId;// 企业用户Id
				int toUserId = userId;
				Object objectId = dbObj.get("_id").toString();
				new PushText(type, fromUserId, toUserId, objectId).send();
			}
		});
	}

	@Override
	public JobVO get(ObjectId jobId) {
		return dsForRW.createQuery(JobVO.class).field("_id").equal(jobId).get();
	}

	@Override
	public JobVO selectById(ObjectId jobId) {
		Query<JobVO> q = dsForRW.createQuery(JobVO.class).field("_id").equal(jobId);
		UpdateOperations<JobVO> ops = dsForRW.createUpdateOperations(JobVO.class).inc("count.browse", 1);
		JobVO job = dsForRW.findAndModify(q, ops);
		if (null == job)
			throw new ServiceException("职位不存在");
		return job;
	}

	@Override
	public List<JobVO> selectByCompanyId(int userId, int pageIndex, int pageSize) {
		List<Integer> idList = friendsRepository.queryFollowCompanyId(userId);
		return selectByCompanyId(idList, pageIndex, pageSize);
	}

	@Override
	public List<JobVO> selectByCompanyId(List<Integer> idList, int pageIndex, int pageSize) {
		List<JobVO> result = Lists.newArrayList();
		if (null != idList && !idList.isEmpty()) {
			Query<JobVO> q = dsForRW.createQuery(JobVO.class).field("status").equal(1).filter("co.id in", idList);
			result = q.offset(pageIndex * pageSize).limit(pageSize).asList();
		}
		return result;
	}

	@Override
	public List<JobVO> query(JobExample example, int pageIndex, int pageSize) {
		Query<JobVO> q = dsForRW.createQuery(JobVO.class);

		if (0 != example.getStatus()) {
			// 可用职位
			q.field("status").equal(example.getStatus());
		}
		// 按城市筛选
		if (0 != example.getCityId())
			q.field("cityId").equal(example.getCityId());

		// 按公司筛选
		if (0 != example.getCompanyId())
			q.field("co.id").equal(example.getCompanyId());
		// 按公司名字筛选
		if (!StringUtil.isEmpty(example.getCompanyName()))
			q.field("co.name").containsIgnoreCase(example.getCompanyName());
		// 按公司性质筛选
		if (0 != example.getNature())
			q.field("co.nature").equal(example.getNature());
		// 按公司规模筛选
		if (0 != example.getScale())
			q.field("co.scale").equal(example.getScale());
		// 按公司行业筛选
		if (0 != example.getIndustryId())
			q.field("co.ind").equal(example.getIndustryId());
		// 按职能筛选
		if (0 != example.getFnId())
			q.field("fnId").equal(example.getFnId());

		if (!StringUtil.isEmpty(example.getJobName()))
			// q.field("jobName").equal(Pattern.compile(example.getJobName()));
			q.field("jobName").containsIgnoreCase(example.getJobName());

		if (0 != example.getDiploma())
			q.field("diploma").equal(example.getDiploma());
		if (0 != example.getSalary())
			q.field("salary").equal(example.getSalary());
		if (0 != example.getWorkExp())
			q.field("workExp").equal(example.getWorkExp());
		// 最近几天
		if (null != example.getDays() && 0 != example.getDays()) {
			q.filter("refreshTime >", DateUtil.currentTimeSeconds() - example.getDays() * 86400000);
		}
		if (!StringUtil.isEmpty(example.getKeyword())) {
			q.or(q.criteria("jobName").containsIgnoreCase(example.getKeyword()),
					q.criteria("co.name").containsIgnoreCase(example.getKeyword()));
		}

		List<JobVO> jobList = 0 == pageSize ? q.asList() : q.offset(pageIndex * pageSize).limit(pageSize).asList();

		return jobList;
	}

	@Override
	public List<JobVO> selectByUserId(int userId, int status, int pageIndex, int pageSize) {
		User user = userManager.getUser(userId);

		Query<JobVO> q = dsForRW.createQuery(JobVO.class);
		q.field("co.id").equal(user.getCompanyId());
		if (0 != status)
			q.field("status").equal(status);

		List<JobVO> jobList = 0 == pageSize ? q.asList() : q.offset(pageIndex * pageSize).limit(pageSize).asList();

		return jobList;
	}

	public List<JobVO> selectJobByIdList(Object... elements) {
		List<JobVO> results = dsForRW.createQuery(JobVO.class).filter("_id in", elements).asList();
		return results;
	}

	@Override
	public List<JobVO> selectOrderByActive(int pageIndex, int pageSize) {
		Query<JobVO> q = dsForRW.createQuery(JobVO.class).field("status").equal(1).order("-count.active").offset(pageIndex * pageSize)
				.limit(pageSize);
		return q.asList();
	}

	@Override
	public List<JobVO> selectOrderByTime(int pageIndex, int pageSize) {
		// Query<JobVO> q = dsForRW.createQuery(JobVO.class);
		// if (null != jobId)
		// q.field(Mapper.ID_KEY).lessThan(jobId);
		// q.order("-_id").limit(pageSize);

		Query<JobVO> q = dsForRW.createQuery(JobVO.class).field("status").equal(1).order("-refreshTime").offset(pageIndex * pageSize)
				.limit(pageSize);
		return q.asList();
	}

}
