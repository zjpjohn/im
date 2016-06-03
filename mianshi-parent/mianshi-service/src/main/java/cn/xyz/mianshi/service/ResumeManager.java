package cn.xyz.mianshi.service;

import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.example.UserExample;
import cn.xyz.mianshi.vo.cv.ResumeV2;

import com.mongodb.DBObject;

public interface ResumeManager {

	Object createCV(int userId, UserExample example);

	Object delete(int userId, List<ObjectId> idList);

	Object deleteChild(int userId, ObjectId resumeId, String nodeName,
			ObjectId childId);

	Object get(int userId, ObjectId resumtId, String nodeName);

	Object get(int userId, String nodeName);

	Object get(int userId);

	Object get(ObjectId resumtId);

	Object get(ObjectId resumtId, String nodeName);

	Object save(int userId, ResumeV2 resume);

	Object saveChild(int userId, ObjectId resumeId, String nodeName,
			DBObject dbObj);

	ResumeV2 selectById(int userId, ObjectId id);

	Object selectById(int userId, ObjectId id, String nodeName);

	List<ResumeV2> selectByUserId(int userId);

	Object selectSimpleByUserId(int userId);

	Object updateChild(int userId, ObjectId resumeId, String nodeName,
			DBObject dbObj);

	Object updateNode(int userId, ObjectId id, String nodeName, Object obj);

}
