package cn.xyz.repository;

import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.example.AddMsgParam;
import cn.xyz.mianshi.example.MessageExample;
import cn.xyz.mianshi.vo.Msg;

public interface MsgRepository {

	ObjectId add(int userId, AddMsgParam param);

	boolean delete(Integer userId, ObjectId msgId);

	List<Msg> findByIdList(int userId, String ids);

	/**
	 * 获取用户最新商务圈消息
	 * 
	 * @param userId
	 * @param toUserId
	 * @param msgId
	 * @param pageSize
	 * @return
	 */
	List<Msg> findByUser(Integer userId, Integer toUserId, ObjectId msgId, Integer pageSize);

	/**
	 * 获取用户最新商务圈消息
	 * 
	 * @param userId
	 * @param msgId
	 * @param pageSize
	 * @return
	 */
	List<Msg> findByUser(Integer userId, ObjectId msgId, Integer pageSize);

	/**
	 * 获取当前登录用户及其所关注用户的最新商务圈消息
	 * 
	 * @param userId
	 * @param toUserId
	 * @param msgId
	 * @param pageSize
	 * @return
	 */
	List<Msg> findByUserList(Integer userId, Integer toUserId, ObjectId msgId, Integer pageSize);

	/**
	 * 获取用户最新商务圈消息Id
	 * 
	 * @param userId
	 * @param toUserId
	 * @param msgId
	 * @param pageSize
	 * @return
	 */
	List<Msg> findIdByUser(int userId, int toUserId, ObjectId msgId, int pageSize);

	/**
	 * 获取当前登录用户及其所关注用户的最新商务圈消息Id
	 * 
	 * @param userId
	 * @param toUserId
	 * @param msgId
	 * @param pageSize
	 * @return
	 */
	List<Msg> findIdByUserList(int userId, int toUserId, ObjectId msgId, int pageSize);

	List<Msg> findByExample(int userId, MessageExample example);

	boolean forwarding(Integer userId, AddMsgParam param);

	Msg get(int userId, ObjectId msgId);

	List<Msg> getSquareMsgList(int userId, ObjectId msgId, Integer pageSize);

	void update(ObjectId msgId, Msg.Op op, int activeValue);
}
