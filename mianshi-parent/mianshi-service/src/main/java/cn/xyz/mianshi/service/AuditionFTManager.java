package cn.xyz.mianshi.service;

import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.example.AuditionFTExample;
import cn.xyz.mianshi.example.AuditionInviteExample;
import cn.xyz.mianshi.vo.JobApply;
import cn.xyz.mianshi.vo.PageVO;

import com.mongodb.DBObject;

public interface AuditionFTManager {

	Object agree(int userId, List<ObjectId> idList);

	Object refuse(int userId, List<ObjectId> idList);

	Object delete(int userId, List<ObjectId> idList);

	Object notpass(int userId, List<ObjectId> idList);

	Object pass(int userId, List<ObjectId> idList, long startTime);

	void answer(int userId, ObjectId aftId, int examId, Object answer, int score);

	Object get(ObjectId id);

	Object invite(int userId, AuditionInviteExample param);

	Object invite(int userId, JobApply jobApply);

	Object invite(int userId, JobApply jobApply, List<Integer> examIdList);

	List<DBObject> query(int userId, AuditionFTExample example);

	PageVO query(int userId, int offset, int limit);

	void score(int userId, ObjectId aftId, int examId, int score);

	void update(int userId, ObjectId aftId, List<Integer> examIdList);

}
