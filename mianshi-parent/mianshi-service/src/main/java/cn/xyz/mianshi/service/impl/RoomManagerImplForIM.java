package cn.xyz.mianshi.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.example.UserExample;
import cn.xyz.mianshi.service.RoomManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Room;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.Room.Member;
import cn.xyz.mianshi.vo.Room.Notice;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Service(RoomManager.BEAN_ID)
public class RoomManagerImplForIM implements RoomManager {

	@Resource(name = "dsForTigase")
	private Datastore dsForTigase;
	@Autowired
	private UserManager userManager;
	@Autowired
	private KXMPPServiceImpl xmppManager;

	@Override
	public Room add(User user, Room example, List<Integer> memberUserIdList) {
		Room entity = new Room();
		ObjectId roomId=ObjectId.get();
		entity.setId(roomId);
		entity.setJid(example.getJid());
		entity.setName(example.getName());// 必须
		entity.setDesc(example.getDesc());// 必须
		entity.setSubject("");
		entity.setCategory(0);
		entity.setTags(Lists.newArrayList());
		entity.setNotice(new Room.Notice());
		entity.setNotices(Lists.newArrayList());
		entity.setUserSize(0);
		entity.setMaxUserSize(100);
		entity.setMembers(Lists.newArrayList());
		entity.setCountryId(example.getCountryId());// 必须
		entity.setProvinceId(example.getProvinceId());// 必须
		entity.setCityId(example.getCityId());// 必须
		entity.setAreaId(example.getAreaId());// 必须
		entity.setLongitude(example.getLongitude());// 必须
		entity.setLatitude(example.getLatitude());// 必须
		entity.setUserId(user.getUserId());
		entity.setNickname(user.getNickname());
		entity.setCreateTime(DateUtil.currentTimeSeconds());
		entity.setModifier(user.getUserId());
		entity.setModifyTime(entity.getCreateTime());
		entity.setS(1);
		entity.setComunityCode(example.getComunityCode());
		if(example.getShowType()!=null&&example.getShowType().equals("hide")){
			entity.setShowType("hide");//私有群组
		}else{
			entity.setShowType("show");
		}
		
		
		
		if (null == entity.getName())
			entity.setName("我的群组");
		if (null == entity.getDesc())
			entity.setDesc("");
		if (null == entity.getCountryId())
			entity.setCountryId(0);
		if (null == entity.getProvinceId())
			entity.setProvinceId(0);
		if (null == entity.getCityId())
			entity.setCityId(0);
		if (null == entity.getAreaId())
			entity.setAreaId(0);
		if (null == entity.getLongitude())
			entity.setLongitude(0d);
		if (null == entity.getLatitude())
			entity.setLatitude(0d);

		
		// 保存房间配置
		dsForTigase.save(entity);

		// 创建者
		Member member = new Member();
		member.setActive(DateUtil.currentTimeSeconds());
		member.setCreateTime(member.getActive());
		member.setModifyTime(0L);
		member.setNickname(user.getNickname());
		member.setRole(1);
		member.setRoomId(entity.getId());
		member.setSub(1);
		member.setTalkTime(0L);
		member.setUserId(user.getUserId());
		// 群组成员列表
		List<Member> memberList = Lists.newArrayList(member);
		String addMemberStr="";
		// 群组成员列表不为空
		if (null != memberUserIdList && !memberUserIdList.isEmpty()) {
			Long currentTimeSeconds = DateUtil.currentTimeSeconds();
			for (int userId : memberUserIdList) {
				User _user = userManager.getUser(userId);
				// 成员
				Member _member = new Member();
				_member.setActive(currentTimeSeconds);
				_member.setCreateTime(currentTimeSeconds);
				_member.setModifyTime(0L);
				_member.setNickname(_user.getNickname());
				_member.setRole(3);
				_member.setRoomId(roomId);
				_member.setSub(1);
				_member.setTalkTime(0L);
				_member.setUserId(_user.getUserId());

				memberList.add(_member);
				addMemberStr+=_user.getNickname()+"/";
			}
		}
		// 保存成员列表
		dsForTigase.save(memberList);
		//修改房间成员数量
		updateUserSize(entity.getId(), memberList.size());
		// 新增成员推送通知
		Room room = get(roomId);
		MessageBean messageBean = new MessageBean();
		messageBean.setFromUserId(user.getUserId() + "");
		messageBean.setFromUserName(user.getNickname());
		//messageBean.setType(KXMPPServiceImpl.NEW_MEMBER);
		messageBean.setType(KXMPPServiceImpl.CHANGE_ROOM_NAME);
		messageBean.setObjectId(room.getJid());
		//messageBean.setContent(user.getNickname()+"邀请"+addMemberStr+"加入了群聊");
		messageBean.setContent(room.getName());
		
		try {
			KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId, 0),messageBean.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}

	@Override
	public void delete(User user, ObjectId roomId) {
		// IMPORTANT 1-3、删房间推送-已改
		Room room = get(roomId);
		MessageBean messageBean = new MessageBean();
		messageBean.setFromUserId(user.getUserId() + "");
		messageBean.setFromUserName(user.getNickname());
		messageBean.setType(KXMPPServiceImpl.DELETE_ROOM);
		// messageBean.setObjectId(room.getId().toString());
		messageBean.setObjectId(room.getJid());
		messageBean.setContent(room.getName());

		try {
			KXMPPServiceImpl.getInstance().send(
					getMemberIdList(room, user.getUserId()),
					messageBean.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//删除房间的公告
		dsForTigase.delete(dsForTigase.createQuery(Room.Notice.class).field("roomId").equal(roomId));
		//删除房间内成员
		dsForTigase.delete(dsForTigase.createQuery(Room.Member.class).field("roomId").equal(roomId));
		//删除房间
		dsForTigase.delete(dsForTigase.createQuery(Room.class).field("_id").equal(roomId));
	}

	@Override
	public void update(User user, ObjectId roomId, String roomName,
			String notice, String desc) {
		BasicDBObject q = new BasicDBObject("_id", roomId);
		BasicDBObject o = new BasicDBObject();
		BasicDBObject values = new BasicDBObject();
		Room room = get(roomId);

		if (!StringUtil.isEmpty(roomName)) {
			// o.put("$set", new BasicDBObject("name", roomName));
			values.put("name", roomName);

			// IMPORTANT 1-2、改房间名推送-已改
			MessageBean messageBean = new MessageBean();
			messageBean.setFromUserId(user.getUserId() + "");
			messageBean.setFromUserName(user.getNickname());
			messageBean.setType(KXMPPServiceImpl.CHANGE_ROOM_NAME);
			// messageBean.setObjectId(roomId.toString());
			messageBean.setObjectId(room.getJid());
			messageBean.setContent(roomName);
			try {
				KXMPPServiceImpl.getInstance().send(getMemberIdList(room, 0),
						messageBean.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!StringUtil.isEmpty(desc)) {
			// o.put("$set", new BasicDBObject("desc", desc));
			values.put("desc", desc);
		}
		if (!StringUtil.isEmpty(notice)) {
			BasicDBObject dbObj = new BasicDBObject();
			dbObj.put("roomId", roomId);
			dbObj.put("text", notice);
			dbObj.put("userId", user.getUserId());
			dbObj.put("nickname", user.getNickname());
			dbObj.put("time", DateUtil.currentTimeSeconds());

			// 更新最新公告
			// o.put("$set", new BasicDBObject("notice", dbObj));
			values.put("notice", dbObj);

			// 新增历史公告记录
			dsForTigase.getCollection(Room.Notice.class).save(dbObj);

			// IMPORTANT 1-5、改公告推送-已改
			MessageBean messageBean = new MessageBean();
			messageBean.setFromUserId(user.getUserId() + "");
			messageBean.setFromUserName(user.getNickname());
			messageBean.setType(KXMPPServiceImpl.NEW_NOTICE);
			// messageBean.setObjectId(roomId.toString());
			messageBean.setObjectId(room.getJid());
			messageBean.setContent(notice);
			try {
				KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId, 0),
						messageBean.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		o.put("$set", values);
		dsForTigase.getCollection(Room.class).update(q, o);
	}

	@Override
	public Room get(ObjectId roomId) {
		Room room = dsForTigase.createQuery(Room.class).field("_id")
				.equal(roomId).get();

		if (null != room) {
			// Member member;
			List<Member> members = dsForTigase.createQuery(Room.Member.class)
					.field("roomId").equal(roomId).asList();
			List<Notice> notices = dsForTigase.createQuery(Room.Notice.class)
					.field("roomId").equal(roomId).asList();

			// room.setMember(member);
			room.setMembers(members);
			room.setNotices(notices);
		}

		return room;
	}

	@Override
	public List<Room> selectList(Room room,int pageIndex, int pageSize) {
		Query<Room> q =dsForTigase.createQuery(Room.class)
				.field("comunityCode").equal(room.getComunityCode())
				.filter("showType !=", "hide");
		if(room.getName()!=null){//按群组名称查询
			q.field("name").contains(room.getName());
		}
		List<Room> roomList=q.offset(pageIndex * pageSize).limit(pageSize).order("-userSize").asList();
		return roomList;
	}

	@Override
	public Object selectHistoryList(int userId, int type) {
		List<Object> historyIdList = Lists.newArrayList();

		Query<Room.Member> q = dsForTigase.createQuery(Room.Member.class)
				.field("userId").equal(userId);
		if (1 == type) {// 自己的房间
			q.filter("role =", 1);
		} else if (2 == type) {// 加入的房间
			q.filter("role !=", 1);
		}
		DBCursor cursor = dsForTigase.getCollection(Room.Member.class).find(
				q.getQueryObject(), new BasicDBObject("roomId", 1));
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			historyIdList.add(dbObj.get("roomId"));
		}

		if (historyIdList.isEmpty())
			return null;

		List<Room> historyList = dsForTigase.createQuery(Room.class)
				.field("_id").in(historyIdList).order("-_id").asList();
		historyList.forEach(room -> {
			Member member = dsForTigase.createQuery(Room.Member.class)
					.field("roomId").equal(room.getId()).field("userId")
					.equal(userId).get();
			room.setMember(member);
		});

		return historyList;
	}

	@Override
	public Object selectHistoryList(int userId, int type,Room roomN, int pageIndex,
			int pageSize) {
		List<Object> historyIdList = Lists.newArrayList();

		
		Query<Room.Member> q = dsForTigase.createQuery(Room.Member.class)
				.field("userId").equal(userId);
		if(roomN.getName()!=null){//是否按群组名称查询
			q.field("name").contains(roomN.getName());
		}
		if (1 == type) {// 自己的房间
			q.filter("role =", 1);
		} else if (2 == type) {// 加入的房间
			q.filter("role !=", 1);
		}
		
		DBCursor cursor = dsForTigase.getCollection(Room.Member.class).find(
				q.getQueryObject(), new BasicDBObject("roomId", 1));
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			historyIdList.add(dbObj.get("roomId"));
		}

		if (historyIdList.isEmpty())
			return null;

		List<Room> historyList = dsForTigase.createQuery(Room.class)
				.field("_id").in(historyIdList).order("-_id")
				.offset(pageIndex * pageSize).limit(pageSize).asList();
		historyList.forEach(room -> {
			Member member = dsForTigase.createQuery(Room.Member.class)
					.field("roomId").equal(room.getId()).field("userId")
					.equal(userId).get();
			room.setMember(member);
		});

		return historyList;
	}

	@Override
	public void deleteMember(User user, ObjectId roomId, int userId) {
		Room room = get(roomId);
		User toUser = userManager.getUser(userId);

		// IMPORTANT 1-4、删除成员推送-已改
		MessageBean messageBean = new MessageBean();
		messageBean.setFromUserId(user.getUserId() + "");
		messageBean.setFromUserName(user.getNickname());
		messageBean.setType(KXMPPServiceImpl.DELETE_MEMBER);
		// messageBean.setObjectId(roomId.toString());
		messageBean.setObjectId(room.getJid());
		messageBean.setToUserId(userId + "");
		messageBean.setToUserName(toUser.getNickname());
		try {
			KXMPPServiceImpl.getInstance().send(
					getMemberIdList(roomId, user.getUserId()),
					messageBean.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		Query<Room.Member> q = dsForTigase.createQuery(Room.Member.class)
				.field("roomId").equal(roomId).field("userId").equal(userId);
		dsForTigase.delete(q);

		updateUserSize(roomId, -1);
	}

	@Override
	public void updateMember(User user, ObjectId roomId,
			List<Integer> userIdList) {
		for (int userId : userIdList) {
			Member _member = new Member();
			_member.setUserId(userId);
			_member.setRole(3);
			updateMember(user, roomId, _member);
		}
	}

	@Override
	public void updateMember(User user, ObjectId roomId, Member member) {
		DBCollection dbCollection = dsForTigase
				.getCollection(Room.Member.class);
		DBObject q = new BasicDBObject().append("roomId", roomId).append(
				"userId", member.getUserId());
		Room room = get(roomId);
		User toUser = userManager.getUser(member.getUserId());

		if (1 == dbCollection.count(q)) {
			BasicDBObject values = new BasicDBObject();
			if (null != member.getRole())
				values.append("role", member.getRole());
			if (null != member.getSub())
				values.append("sub", member.getSub());
			if (null != member.getTalkTime())
				values.append("talkTime", member.getTalkTime());
			if (!StringUtil.isEmpty(member.getNickname()))
				values.append("nickname", member.getNickname());
			values.append("modifyTime", DateUtil.currentTimeSeconds());

			// 更新成员信息
			dbCollection.update(q, new BasicDBObject("$set", values));

			if (!StringUtil.isEmpty(member.getNickname())
					&& !toUser.getNickname().equals(member.getNickname())) {
				// IMPORTANT 1-1、改昵称推送-已改
				MessageBean messageBean = new MessageBean();
				messageBean.setType(KXMPPServiceImpl.CHANGE_NICK_NAME);
				// messageBean.setObjectId(roomId.toString());
				messageBean.setObjectId(room.getJid());
				messageBean.setFromUserId(user.getUserId() + "");
				messageBean.setFromUserName(user.getNickname());
				messageBean.setToUserId(toUser.getUserId() + "");
				messageBean.setToUserName(toUser.getNickname());
				messageBean.setContent(member.getNickname());
				try {
					KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId, 0),
							messageBean.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (null != member.getTalkTime()) {
				// IMPORTANT 1-6、禁言
				MessageBean messageBean = new MessageBean();
				messageBean.setType(KXMPPServiceImpl.GAG);
				// messageBean.setObjectId(roomId.toString());
				messageBean.setObjectId(room.getJid());
				messageBean.setFromUserId(user.getUserId() + "");
				messageBean.setFromUserName(user.getNickname());
				messageBean.setToUserId(toUser.getUserId() + "");
				messageBean.setToUserName(toUser.getNickname());
				messageBean.setContent(member.getTalkTime() + "");
				try {
					KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId, 0),
							messageBean.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			User _user = userManager.getUser(member.getUserId());
			Member _member = new Member();
			_member.setActive(DateUtil.currentTimeSeconds());
			_member.setCreateTime(_member.getActive());
			_member.setModifyTime(0L);
			_member.setNickname(_user.getNickname());
			_member.setRole(member.getRole());
			_member.setRoomId(roomId);
			_member.setSub(1);
			_member.setTalkTime(0L);
			_member.setUserId(_user.getUserId());

			dsForTigase.save(_member);

			updateUserSize(roomId, 1);

			// IMPORTANT 1-7、新增成员
			MessageBean messageBean = new MessageBean();
			messageBean.setType(KXMPPServiceImpl.NEW_MEMBER);
			// messageBean.setObjectId(roomId.toString());
			messageBean.setObjectId(room.getJid());
			messageBean.setFromUserId(user.getUserId() + "");
			messageBean.setFromUserName(user.getNickname());
			messageBean.setToUserId(toUser.getUserId() + "");
			messageBean.setToUserName(toUser.getNickname());
			messageBean.setContent(room.getName());
			messageBean.setFileName(room.getId().toString());
			try {
				KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId, 0),
						messageBean.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Object getMember(ObjectId roomId, int userId) {
		return dsForTigase.createQuery(Room.Member.class).field("roomId")
				.equal(roomId).field("userId").equal(userId).get();
	}

	@Override
	public List<Member> getMemberList(ObjectId roomId) {
		return dsForTigase.createQuery(Room.Member.class).field("roomId")
				.equal(roomId).asList();
	}

	@Override
	public void join(int userId, ObjectId roomId, int type) {
		Member member = new Member();
		member.setUserId(userId);
		member.setRole(1 == type ? 1 : 3);
		updateMember(userManager.getUser(userId), roomId, member);
	}

	private void updateUserSize(ObjectId roomId, int userSize) {
		DBObject q = new BasicDBObject("_id", roomId);
		DBObject o = new BasicDBObject("$inc", new BasicDBObject("userSize",
				userSize));
		dsForTigase.getCollection(Room.class).update(q, o);
	}

	/**
	 * 获取房间成员列表
	 * 
	 * @param roomId
	 * @param userId
	 * @return
	 */
	private List<Integer> getMemberIdList(ObjectId roomId, int userId) {
		List<Integer> userIdList = Lists.newArrayList();

		Query<Room.Member> q = dsForTigase.createQuery(Room.Member.class)
				.field("roomId").equal(roomId);
		DBCursor cursor = dsForTigase.getCollection(Room.Member.class).find(
				q.getQueryObject(), new BasicDBObject("userId", 1));

		while (cursor.hasNext()) {
			BasicDBObject dbObj = (BasicDBObject) cursor.next();
			int _userId = dbObj.getInt("userId");
			if (_userId != userId)
				userIdList.add(_userId);
		}

		return userIdList;
	}

	private List<Integer> getMemberIdList(Room room, int userId) {
		return getMemberIdList(room.getId(), userId);
	}

	@Override
	public void leave(int userId, ObjectId roomId) {

	}

	/**
	 * 获取用户创建的房间数量
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public int getRoomCount(String comunityCode, int userId) {
		Query<Room> q = dsForTigase.createQuery(Room.class).field("userId").equal(userId).field("comunityCode").equal(comunityCode);
		List<Room> asList = q.asList();
		return asList.size();
	}
}
