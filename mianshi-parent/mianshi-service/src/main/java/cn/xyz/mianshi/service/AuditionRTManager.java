package cn.xyz.mianshi.service;

import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.example.AuditionInviteExample;
import cn.xyz.mianshi.example.AuditionRTExample;
import cn.xyz.mianshi.vo.JobApply;
import cn.xyz.mianshi.vo.PageVO;

import com.mongodb.DBObject;

public interface AuditionRTManager {

	void start(int userId, ObjectId artId);

	void reserveTime(int userId, ObjectId artId, long startTime);

	Object agree(int userId, List<ObjectId> idList);

	Object refuse(int userId, List<ObjectId> idList);

	Object notpass(int userId, List<ObjectId> idList);

	Object pass(int userId, List<ObjectId> idList);

	Object delete(int userId, List<ObjectId> idList);

	Object get(ObjectId id);

	Object gets(List<ObjectId> idList);

	Object invite(int userId, AuditionInviteExample example);

	Object invite(int userId, DBObject dbObj, long startTime);

	Object invite(int userId, JobApply jobApply, long startTime);

	Object query(int userId, AuditionRTExample example);

	PageVO query(int userId, int offset, int limit);

	void score(int userId, ObjectId artId, int score);

}
