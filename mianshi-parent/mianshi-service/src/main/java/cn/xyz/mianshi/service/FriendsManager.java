package cn.xyz.mianshi.service;

import java.util.List;

import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.vo.Fans;
import cn.xyz.mianshi.vo.Friends;

public interface FriendsManager {

	Friends addBlacklist(Integer userId, Integer toUserId);

	boolean addFriends(Integer userId, Integer toUserId);

	Friends deleteBlacklist(Integer userId, Integer toUserId);

	boolean deleteFriends(Integer userId, Integer toUserId);

	Object followCompany(int userId, int companyId);

	JSONMessage followUser(Integer userId, Integer toUserId);

	Friends getFriends(Friends friends);

	List<Friends> queryBlacklist(Integer userId);

	List<Fans> queryFans(Integer userId);

	List<Integer> queryFansId(Integer userId);

	List<Friends> queryFollow(Integer userId);

	List<Integer> queryFollowId(Integer userId);

	List<Friends> queryFriends(Integer userId);

	List<Integer> getFriendsIdList(int userId);

	Object unfollowCompany(int userId, int companyId);

	boolean unfollowUser(Integer userId, Integer toUserId);

	Friends updateRemark(int userId, int toUserId, String remarkName);

}
