package cn.xyz.mianshi.service;

import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.example.JobApplyExample;
import cn.xyz.mianshi.vo.JobApply;

public interface JobApplyManager {

	Object agree(int userId, List<ObjectId> jobApplyIdList, long startTime);

	Object delete(int userId, List<ObjectId> jobApplyIdList);

	JobApply selectById(ObjectId jobApplyId);

	void read(int userId, List<ObjectId> jobApplyIdList, int read);

	Object refuse(int userId, List<ObjectId> jobApplyIdList);

	Object save(int userId, ObjectId resumeId, List<ObjectId> jobIdList);

	List<JobApply> selectByExample(int userId, int status, ObjectId jobApplyId, int pageSize);

	List<JobApply> selectByExample(int userId, JobApplyExample example);

	void tag(ObjectId jobApplyId, int tagId);

}
