package cn.xyz.repository;

import java.util.List;

import cn.xyz.mianshi.vo.CompanyFans;
import cn.xyz.mianshi.vo.CompanyFriends;
import cn.xyz.mianshi.vo.Fans;
import cn.xyz.mianshi.vo.Friends;

public interface FriendsRepository {

	CompanyFans deleteFans(CompanyFans fans);

	Fans deleteFans(int userId, int toUserId);

	CompanyFriends deleteFriends(CompanyFriends friends);

	Friends deleteFriends(int userId, int toUserId);

	CompanyFriends getFriends(CompanyFriends friends);

	Friends getFriends(int userId, int toUserId);

	List<Friends> queryBlacklist(int userId);

	List<Fans> queryFans(int userId);

	List<Integer> queryFansId(int userId);

	List<Friends> queryFollow(int userId);

	List<Integer> queryFollowId(int userId);

	List<Friends> queryFriends(int userId);

	Object saveFans(CompanyFans entity);

	Object saveFans(Fans fans);

	Object saveFriends(CompanyFriends entity);

	Object saveFriends(Friends friends);

	Friends updateFriends(Friends friends);

	List<Integer> queryFollowCompanyId(int userId);

}
