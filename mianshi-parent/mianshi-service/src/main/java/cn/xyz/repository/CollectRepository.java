package cn.xyz.repository;

import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.vo.UserCollect;

public interface CollectRepository {

	Object add(UserCollect entity);

	Object delete(ObjectId collectId);

	List<UserCollect> find(int userId, int type, ObjectId collectId, int pageSize);

}
