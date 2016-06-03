package cn.xyz.mianshi.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.utils.MapUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.CompanyManager;
import cn.xyz.mianshi.service.FriendsManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.CompanyFans;
import cn.xyz.mianshi.vo.CompanyFriends;
import cn.xyz.mianshi.vo.CompanyVO;
import cn.xyz.mianshi.vo.Fans;
import cn.xyz.mianshi.vo.Friends;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.FriendsRepository;
import cn.xyz.repository.UserRepository;

import com.google.common.collect.Lists;

@Service
public class FriendsManagerImpl implements FriendsManager {

	private static final String groupCode = "110";

	private static Logger Log = LoggerFactory.getLogger(FriendsManager.class);

	@Autowired
	private FriendsRepository friendsRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserManager userService;
	@Autowired
	private CompanyManager companyService;

	@Override
	public Friends addBlacklist(Integer userId, Integer toUserId) {
		// 是否存在AB关系
		Friends friendsAB = friendsRepository.getFriends(userId, toUserId);

		if (null == friendsAB) {
			Friends friends = new Friends(userId, toUserId,
					Friends.Status.Stranger, Friends.Blacklist.Yes);
			friendsRepository.saveFriends(friends);
		} else {
			// 更新关系
			friendsRepository.updateFriends(new Friends(userId, toUserId, null,
					Friends.Blacklist.Yes));
		}

		return friendsRepository.getFriends(userId, toUserId);
	}

	@Override
	public JSONMessage followUser(Integer userId, Integer toUserId) {
		final String serviceCode = "08";
		JSONMessage jMessage;

		try {
			User user = userService.getUser(userId);
			// 是否存在AB关系
			Friends friendsAB = friendsRepository.getFriends(userId, toUserId);
			// 是否存在BA关系
			Friends friendsBA = friendsRepository.getFriends(toUserId, userId);
			// 获取目标用户设置
			User.UserSettings userSettingsB = userService.getSettings(toUserId);

			// ----------------------------
			// 0 0 0 0 无记录 执行关注逻辑
			// A B 1 0 非正常 执行关注逻辑
			// A B 1 1 拉黑陌生人 执行关注逻辑
			// A B 2 0 关注 重复关注
			// A B 3 0 好友 重复关注
			// A B 2 1 拉黑关注 恢复关系
			// A B 3 1 拉黑好友 恢复关系
			// ----------------------------
			// 无AB关系或陌生人黑名单关系，加关注
			if (null == friendsAB
					|| Friends.Status.Stranger == friendsAB.getStatus()) {
				// 目标用户拒绝关注
				if (0 == userSettingsB.getAllowAtt()) {
					jMessage = new JSONMessage(groupCode, serviceCode, "01",
							"关注失败，目标用户拒绝关注");
				}
				// 目标用户允许关注
				else {
					int statusA = 0;

					// 目标用户加好需验证，执行加关注
					if (1 == userSettingsB.getFriendsVerify()) {
						// ----------------------------
						// 0 0 0 0 无记录 执行单向关注
						// B A 1 0 非正常 执行单向关注
						// B A 1 1 拉黑陌生人 执行单向关注
						// B A 2 0 关注 加好友
						// B A 3 0 好友 加好友
						// B A 2 1 拉黑关注 加好友
						// B A 3 1 拉黑好友 加好友
						// ----------------------------
						// 无BA关系或陌生人黑名单关系，单向关注
						if (null == friendsBA
								|| Friends.Status.Stranger == friendsBA
										.getStatus()) {
							statusA = Friends.Status.Attention;
						} else {
							statusA = Friends.Status.Friends;

							friendsRepository.updateFriends(new Friends(
									toUserId, user.getUserId(),
									Friends.Status.Friends));
						}
					}
					// 目标用户加好友无需验证，执行加好友
					else {
						statusA = Friends.Status.Friends;

						if (null == friendsBA) {
							friendsRepository.saveFans(new Fans(userId,
									toUserId));
							friendsRepository.saveFriends(new Friends(toUserId,
									user.getUserId(), Friends.Status.Friends,
									Friends.Blacklist.No));
						} else
							friendsRepository.updateFriends(new Friends(
									toUserId, user.getUserId(),
									Friends.Status.Friends));
					}

					if (null == friendsAB) {
						friendsRepository.saveFans(new Fans(toUserId, userId));
						friendsRepository.saveFriends(new Friends(userId,
								toUserId, statusA, Friends.Blacklist.No));
					} else {
						friendsRepository.updateFriends(new Friends(userId,
								toUserId, statusA, Friends.Blacklist.No));
					}

					if (statusA == Friends.Status.Attention) {
						jMessage = JSONMessage.success("关注成功，已关注目标用户",
								MapUtil.newMap("type", 1));
					} else {
						jMessage = JSONMessage.success("关注成功，已互为好友",
								MapUtil.newMap("type", 2));
					}

				}
			}
			// 有关注或好友关系，重复关注
			else if (Friends.Blacklist.No == friendsAB.getBlacklist()) {
				if (Friends.Status.Attention == friendsAB.getStatus())
					jMessage = JSONMessage.success("关注失败， 重复关注",
							MapUtil.newMap("type", 3));
				else
					jMessage = JSONMessage.success("关注失败， 已互为好友",
							MapUtil.newMap("type", 4));
			}
			// 有关注黑名单或好友黑名单关系，恢复关系
			else {
				friendsRepository.updateFriends(new Friends(userId, toUserId,
						Friends.Blacklist.No));

				jMessage = null;
			}
		} catch (Exception e) {
			Log.error("关注失败", e);

			jMessage = JSONMessage.failure("关注失败");
		}

		return jMessage;
	}

	@Override
	public boolean addFriends(Integer userId, Integer toUserId) {
		if (deleteFriends(userId, toUserId)) {
			friendsRepository.saveFans(new Fans(userId, toUserId));
			friendsRepository.saveFans(new Fans(toUserId, userId));
			friendsRepository.saveFriends(new Friends(userId, toUserId,
					Friends.Status.Friends));
			friendsRepository.saveFriends(new Friends(toUserId, userId,
					Friends.Status.Friends));

			return true;
		} else
			throw new ServiceException("加好友失败");
	}

	@Override
	public Friends deleteBlacklist(Integer userId, Integer toUserId) {
		// 是否存在AB关系
		Friends friendsAB = friendsRepository.getFriends(userId, toUserId);

		if (null == friendsAB) {
			// 无记录
		} else {
			// 陌生人黑名单
			if (Friends.Blacklist.Yes == friendsAB.getBlacklist()
					&& Friends.Status.Stranger == friendsAB.getStatus()) {
				// 物理删除
				friendsRepository.deleteFriends(userId, toUserId);
			} else {
				// 恢复关系
				friendsRepository.updateFriends(new Friends(userId, toUserId,
						null, Friends.Blacklist.No));
			}
			// 是否存在AB关系
			friendsAB = friendsRepository.getFriends(userId, toUserId);
		}

		return friendsAB;
	}

	@Override
	public boolean unfollowUser(Integer userId, Integer toUserId) {
		// 删除用户关注
		friendsRepository.deleteFriends(userId, toUserId);
		// 删除目标用户粉丝
		friendsRepository.deleteFans(toUserId, userId);

		return true;
	}

	@Override
	public boolean deleteFriends(Integer userId, Integer toUserId) {
		friendsRepository.deleteFans(userId, toUserId);
		friendsRepository.deleteFriends(userId, toUserId);
		friendsRepository.deleteFans(toUserId, userId);
		friendsRepository.deleteFriends(toUserId, userId);

		return true;
	}

	@Override
	public Friends getFriends(Friends p) {
		return friendsRepository.getFriends(p.getUserId(), p.getToUserId());
	}

	@Override
	public List<Friends> queryBlacklist(Integer userId) {
		return friendsRepository.queryBlacklist(userId);
	}

	@Override
	public List<Fans> queryFans(Integer userId) {
		List<Fans> result = friendsRepository.queryFans(userId);

		for (Fans friends : result) {
			User user = userService.getUser(friends.getToUserId());
			friends.setToNickname(user.getNickname());
		}

		return result;
	}

	@Override
	public List<Integer> queryFansId(Integer userId) {
		return friendsRepository.queryFansId(userId);
	}

	@Override
	public List<Friends> queryFollow(Integer userId) {
		List<Friends> result = friendsRepository.queryFollow(userId);

		for (Friends friends : result) {
			/*
			 * add by lw start
			 */
			User temp = userRepository.getUser(friends.getToUserId());
			if(temp == null){
				continue;
			}
			/*
			 * add by lw end
			 */
			User user = userService.getUser(friends.getToUserId());
			friends.setCompanyId(user.getCompanyId());
			friends.setToNickname(user.getNickname());
		}

		return result;

	}

	@Override
	public List<Integer> queryFollowId(Integer userId) {
		return friendsRepository.queryFollowId(userId);
	}

	@Override
	public List<Friends> queryFriends(Integer userId) {
		List<Friends> result = friendsRepository.queryFriends(userId);

		result.forEach(friends -> {
			User toUser = userService.getUser(friends.getToUserId());
			friends.setToNickname(toUser.getNickname());
			friends.setCompanyId(toUser.getCompanyId());
		});

		return result;
	}

	@Override
	public Friends updateRemark(int userId, int toUserId, String remarkName) {
		Friends friends = new Friends(userId, toUserId);
		friends.setRemarkName(remarkName);
		return friendsRepository.updateFriends(friends);
	}

	@Override
	public Object followCompany(int userId, int companyId) {
		CompanyFriends friends = friendsRepository
				.getFriends(new CompanyFriends(companyId, userId));

		if (null == friends) {
			CompanyVO company = companyService.get(companyId);

			CompanyFans fans = new CompanyFans();
			fans.setCompanyId(companyId);
			fans.setTime(System.currentTimeMillis() / 1000);
			fans.setUserId(userId);

			friends = new CompanyFriends();
			friends.setCompanyId(companyId);
			friends.setCompanyName(company.getName());
			friends.setTime(System.currentTimeMillis() / 1000);
			friends.setUserId(userId);

			friendsRepository.saveFans(fans);
			return friendsRepository.saveFriends(friends);
		}

		return friends.getId();
	}

	@Override
	public Object unfollowCompany(int userId, int companyId) {
		friendsRepository.deleteFans(new CompanyFans(companyId, userId));
		friendsRepository.deleteFriends(new CompanyFriends(companyId, userId));

		return null;
	}

	@Override
	public List<Integer> getFriendsIdList(int userId) {
		List<Integer> result = Lists.newArrayList();

		try {
			List<Friends> friendsList = friendsRepository.queryFriends(userId);
			friendsList.forEach(friends -> {
				result.add(friends.getToUserId());
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
