package cn.xyz.mianshi.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.service.RoomManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Room;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.Room.Member;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;

import com.google.common.collect.Lists;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Service
public class RoomManagerImpl implements RoomManager {

	@Resource(name = "dsForRW")
	private Datastore dsForRW;
	@Resource(name = "dsForTigase")
	private Datastore dsForTigase;
	
	@Autowired
	private UserManager userManager;

	@Override
	public Room add(User user, Room example, List<Integer> idList) {
		Member member = new Member();
		member.setActive(DateUtil.currentTimeSeconds());
		member.setNickname(user.getNickname());
		member.setRole(1);
		member.setSub(1);
		member.setTalkTime(0L);
		member.setUserId(user.getUserId());

		Room entity = new Room();
		entity.setId(ObjectId.get());
		entity.setJid(example.getJid());
		entity.setName(example.getName());// 必须
		entity.setDesc(example.getDesc());// 必须
		entity.setSubject("");
		entity.setCategory(0);
		entity.setTags(Lists.newArrayList());
		entity.setNotice(new Room.Notice());
		entity.setNotices(Lists.newArrayList());
		entity.setUserSize(1);
		entity.setMaxUserSize(50);
		entity.setMembers(Lists.newArrayList(member));
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
		entity.setComunityCode(example.getComunityCode());;// 必须
		
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
		dsForRW.save(entity);

		join(user.getUserId(), entity.getId(), 1);

		// 创建XMPP房间
		// try {
		// xmppManager.addRoom(user, entity.getId().toString(),
		// entity.getName());
		// } catch (Exception e) {
		// dsForRW.delete(dsForRW.createQuery(Room.class).field("_id").equal(entity.getId()));
		//
		// throw new ServiceException("创建房间失败");
		// }

		if (null != idList && !idList.isEmpty()) {
			for (int userId : idList) {
				Member _member = new Member();
				_member.setUserId(userId);
				_member.setRole(3);
				updateMember(user, entity.getId(), _member);
				join(userId, entity.getId(), 2);
			}
		}

		return entity;
	}

	@Override
	public void delete(User user, ObjectId roomId) {
		// IMPORTANT 1-3、删房间推送
		Room room = get(roomId);
		MessageBean messageBean = new MessageBean();
		messageBean.setType(KXMPPServiceImpl.DELETE_ROOM);
		messageBean.setObjectId(room.getId().toString());
		messageBean.setContent(room.getName());

		try {
			KXMPPServiceImpl.getInstance().send(getMemberIdList(room),
					messageBean.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		dsForRW.delete(dsForRW.createQuery(Room.class).field("_id")
				.equal(roomId));

		// try {
		// xmppManager.deleteRoom(user, roomId.toString());
		// dsForRW.delete(dsForRW.createQuery(Room.class).field("_id").equal(roomId));
		// } catch (Exception e) {
		// throw new ServiceException("删除房间失败");
		// }
	}

	@Override
	public void deleteMember(User user, ObjectId roomId, int userId) {
		DBCollection dbCollection = dsForRW.getCollection(Room.class);

		DBObject q = new BasicDBObject("_id", roomId);
		DBObject o = new BasicDBObject();
		o.put("$pull", new BasicDBObject("members", new BasicDBObject("userId",
				userId)));
		o.put("$inc", new BasicDBObject("userSize", -1));

		dbCollection.update(q, o);
		// IMPORTANT 1-4、删除成员推送
		MessageBean messageBean = new MessageBean();
		messageBean.setType(KXMPPServiceImpl.DELETE_MEMBER);
		messageBean.setObjectId(roomId.toString());
		messageBean.setToUserId(userId + "");
		try {
			KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId),
					messageBean.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Room get(ObjectId roomId) {
		return dsForRW.createQuery(Room.class).field("_id").equal(roomId).get();
	}

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
		return null;
	}

	@Override
	public Object selectHistoryList(int userId, int type,Room roomN, int pageIndex,
			int pageSize) {
		List<Object> idList = Lists.newArrayList();

		if (0 != type) {

			DBCollection coll = dsForRW.getDB().getCollection("s_room_his");
			DBObject project = new BasicDBObject("$project", new BasicDBObject(
					"list", 1));
			DBObject match = new BasicDBObject("$match", new BasicDBObject(
					"_id", userId));
			DBObject unwind = new BasicDBObject("$unwind", "$list");
			DBObject matchChild = new BasicDBObject("$match",
					new BasicDBObject("list.type", type));
			List<DBObject> pipeline = Arrays.asList(project, match, unwind,
					matchChild);

			AggregationOutput output = coll.aggregate(pipeline);
			output.results().forEach(obj -> {
				BasicDBObject dbObj = (BasicDBObject) obj.get("list");
				idList.add(dbObj.get("roomId"));
			});

		} else {
			BasicDBObject o = new BasicDBObject("_id", userId);
			BasicDBObject result = (BasicDBObject) dsForRW.getDB()
					.getCollection("s_room_his").findOne(o);
			if (null == result)
				return null;
			else {
				BasicDBList objList = (BasicDBList) result.get("list");
				for (Object obj : objList) {
					idList.add(((BasicDBObject) obj).get("roomId"));
				}
			}
		}
		if (idList.isEmpty())
			return null;
		Query<Room> q = dsForRW.createQuery(Room.class);
		List<Room> roomList = q.field("_id").in(idList).asList();
		roomList.forEach(room -> {
			for (Member member : room.getMembers()) {
				if (userId == member.getUserId()) {
					room.setMember(member);
				}
			}
			room.setMembers(null);
		});

		return roomList;
	}

	@Override
	public Object getMember(ObjectId roomId, int userId) {
		DBCollection dbCollection = dsForRW.getCollection(Room.class);

		DBObject q = new BasicDBObject();
		q.put("_id", roomId);
		q.put("members.userId", userId);

		if (dbCollection.count(q) != 1)
			throw new ServiceException("成员不存在");

		DBObject members = dbCollection.findOne(q, new BasicDBObject("members",
				1));
		Object member = ((BasicDBList) members.get("members")).get(0);
		return member;
	}

	private List<Integer> getMemberIdList(ObjectId roomId) {
		Room room = get(roomId);
		return getMemberIdList(room);
	}

	private List<Integer> getMemberIdList(Room room) {
		List<Integer> userIdList = Lists.newArrayList();
		room.getMembers().forEach(member -> {
			userIdList.add(member.getUserId());
		});

		return userIdList;
	}

	@Override
	public List<Member> getMemberList(ObjectId roomId) {
		List<Member> memberList = get(roomId).getMembers();
		memberList.sort(new Comparator<Member>() {

			@Override
			public int compare(Member o1, Member o2) {
				return o1.getRole().compareTo(o2.getRole());
			}

		});

		return memberList;
	}

	@Override
	public void join(int userId, ObjectId roomId, int type) {

		DBCollection dbCollection = dsForRW.getDB().getCollection("s_room_his");

		DBObject q = new BasicDBObject("_id", userId);

		BasicDBObject dbObj = new BasicDBObject();
		dbObj.put("roomId", roomId);
		dbObj.put("time", DateUtil.currentTimeSeconds());
		dbObj.put("type", type);

		if (0 == dbCollection.find(q).count()) {
			DBObject o = new BasicDBObject();
			o.put("_id", userId);
			o.put("list", new DBObject[] { dbObj });
			o.put("roomCount", 1);

			dbCollection.insert(o);
		} else {
			DBObject o = new BasicDBObject();
			o.put("$addToSet", new BasicDBObject("list", dbObj));
			o.put("$inc", new BasicDBObject("roomCount", 1));

			dbCollection.update(q, o);
		}

		// BasicDBObject jo = new BasicDBObject();
		// jo.put("_id", userId);
		// jo.put("list", new BasicDBObject("$addToSet", dbObj));
		//
		// dsForRW.getDB().getCollection("s_room_his").save(jo);
	}

	@Override
	public void update(User user, ObjectId roomId, String roomName,
			String notice, String desc) {
		BasicDBObject q = new BasicDBObject("_id", roomId);
		BasicDBObject o = new BasicDBObject();

		if (!StringUtil.isEmpty(roomName)) {
			o.put("$set", new BasicDBObject("name", roomName));
			// IMPORTANT 1-2、改房间名推送
			Room room = get(roomId);
			MessageBean messageBean = new MessageBean();
			messageBean.setType(KXMPPServiceImpl.CHANGE_ROOM_NAME);
			messageBean.setObjectId(roomId.toString());
			messageBean.setContent(roomName);
			try {
				KXMPPServiceImpl.getInstance().send(getMemberIdList(room),
						messageBean.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!StringUtil.isEmpty(desc)) {
			o.put("$set", new BasicDBObject("desc", desc));
		}
		if (!StringUtil.isEmpty(notice)) {
			BasicDBObject dbObj = new BasicDBObject();
			dbObj.put("id", ObjectId.get());
			dbObj.put("text", notice);
			dbObj.put("userId", user.getUserId());
			dbObj.put("nickname", user.getNickname());
			dbObj.put("time", DateUtil.currentTimeSeconds());

			o.put("$set", new BasicDBObject("notice", dbObj));
			o.put("$addToSet", new BasicDBObject("notices", dbObj));

			// IMPORTANT 1-5、改公告推送
			MessageBean messageBean = new MessageBean();
			messageBean.setType(KXMPPServiceImpl.NEW_NOTICE);
			messageBean.setObjectId(roomId.toString());
			messageBean.setContent(notice);
			try {
				KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId),
						messageBean.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		dsForRW.getCollection(Room.class).update(q, o);
	}

	@Override
	public void updateMember(User user, ObjectId roomId, Member member) {
		DBCollection dbCollection = dsForRW.getCollection(Room.class);

		DBObject q = new BasicDBObject();
		q.put("_id", roomId);
		q.put("members.userId", member.getUserId());
		boolean exists = dbCollection.count(q) == 1;

		if (exists) {
			DBObject values = new BasicDBObject();
			if (null != member.getRole())
				values.put("members.$.role", member.getRole());
			if (null != member.getSub())
				values.put("members.$.sub", member.getSub());
			if (null != member.getTalkTime())
				values.put("members.$.talkTime", member.getTalkTime());
			if (null != member.getNickname()
					&& !"".equals(member.getNickname()))
				values.put("members.$.nickname", member.getNickname());
			values.put("members.$.modifyTime", DateUtil.currentTimeSeconds());

			// q.put("members.userId", user.getUserId());
			// q.put("members.role", v)
			// BasicDBObject values = new BasicDBObject();
			// values.put("members.$.sub", sub);
			// values.put("members.$.talkTime", talkTime);
			//

			values.put("modifier", user.getUserId());
			values.put("modifyTime", DateUtil.currentTimeSeconds());

			DBObject o = new BasicDBObject("$set", values);

			dbCollection.update(q, o);

			if (null != member.getNickname()
					&& !"".equals(member.getNickname())) {
				// IMPORTANT 1-1、改昵称推送
				MessageBean messageBean = new MessageBean();
				messageBean.setType(KXMPPServiceImpl.CHANGE_NICK_NAME);
				messageBean.setObjectId(roomId.toString());
				messageBean.setToUserId(member.getUserId() + "");
				messageBean.setToUserName(member.getNickname() + "");
				try {
					KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId),
							messageBean.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (null != member.getTalkTime()) {
				// IMPORTANT 1-6、禁言
				MessageBean messageBean = new MessageBean();
				messageBean.setType(KXMPPServiceImpl.GAG);
				messageBean.setObjectId(roomId.toString());
				messageBean.setToUserId(member.getUserId() + "");
				messageBean.setToUserName(member.getNickname() + "");
				messageBean.setContent(member.getTalkTime());
				try {
					KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId),
							messageBean.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			User toUser = userManager.getUser(member.getUserId());

			DBObject dbObj = new BasicDBObject();
			dbObj.put("active", DateUtil.currentTimeSeconds());
			dbObj.put("nickname", toUser.getNickname());
			dbObj.put("role", member.getRole());
			dbObj.put("sub", 1);
			dbObj.put("talkTime", 0);
			dbObj.put("userId", member.getUserId());
			dbObj.put("createTime", DateUtil.currentTimeSeconds());
			dbObj.put("modifyTime", DateUtil.currentTimeSeconds());

			q = new BasicDBObject("_id", roomId);
			BasicDBObject o = new BasicDBObject();
			o.put("$addToSet", new BasicDBObject("members", dbObj));
			o.put("$inc", new BasicDBObject("userSize", 1));

			dbCollection.update(q, o);

			// join(member.getUserId(), roomId);
		}
	}

	@Override
	public void updateMember(User user, ObjectId roomId, List<Integer> idList) {
		for (int userId : idList) {
			Member _member = new Member();
			_member.setUserId(userId);
			_member.setRole(3);
			updateMember(user, roomId, _member);
			join(userId, roomId, 2);
		}
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
		Query<Room> q = dsForRW.createQuery(Room.class).field("userId").equal(userId).field("comunityCode").equal(comunityCode);
		List<Room> asList = q.asList();
		return asList.size();
	}

}
