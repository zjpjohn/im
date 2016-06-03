package cn.xyz.mianshi.service;

import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.vo.Room;
import cn.xyz.mianshi.vo.User;

public interface RoomManager {
	public static final String BEAN_ID = "RoomManagerImpl2";

	Room add(User user, Room room, List<Integer> idList);

	void delete(User user, ObjectId roomId);

	void update(User user, ObjectId roomId, String roomName, String notice,
			String desc);

	Room get(ObjectId roomId);

	List<Room> selectList(Room room,int pageIndex, int pageSize);

	Object selectHistoryList(int userId, int type);

	Object selectHistoryList(int userId, int type,Room room, int pageIndex, int pageSize);

	void deleteMember(User user, ObjectId roomId, int userId);

	void updateMember(User user, ObjectId roomId, Room.Member member);

	void updateMember(User user, ObjectId roomId, List<Integer> idList);

	Object getMember(ObjectId roomId, int userId);

	List<Room.Member> getMemberList(ObjectId roomId);

	void join(int userId, ObjectId roomId, int type);

	void leave(int userId, ObjectId roomId);

	int getRoomCount(String comunityCode, int userId);
}
