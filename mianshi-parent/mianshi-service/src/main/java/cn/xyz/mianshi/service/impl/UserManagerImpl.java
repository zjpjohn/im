package cn.xyz.mianshi.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import cn.xyz.commons.constants.KKeyConstant;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.jedis.JedisCallback;
import cn.xyz.commons.support.jedis.JedisCallbackVoid;
import cn.xyz.mianshi.example.UserExample;
import cn.xyz.mianshi.example.UserQueryExample;
import cn.xyz.mianshi.service.CompanyManager;
import cn.xyz.mianshi.service.FriendsManager;
import cn.xyz.mianshi.service.IdxManager;
import cn.xyz.mianshi.service.ResumeManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Friends;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.User.UserSettings;
import cn.xyz.repository.MongoRepository;
import cn.xyz.repository.UserRepository;
import cn.xyz.service.KXMPPServiceImpl;

import com.google.common.collect.Maps;
import com.mongodb.DBObject;
import com.shiku.lbs.service.NearbyManager;
import com.shiku.lbs.vo.NearbyUser;

@Service(UserManagerImpl.BEAN_ID)
public class UserManagerImpl extends MongoRepository implements UserManager {
	public static final String BEAN_ID = "UserManagerImpl";

	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private FriendsManager friendsManager;
	@Autowired
	private IdxManager idxManager;
	@Autowired
	private NearbyManager nearbyManager;

	@Autowired
	private ResumeManager resumeManager;
	@Autowired
	private UserRepository userRepository;

	@Override
	public User createUser(String telephone, String password) {
		User user = new User();
		user.setUserId(idxManager.getUserId());
		user.setUserKey(DigestUtils.md5Hex(telephone));
		user.setPassword(DigestUtils.md5Hex(DigestUtils.md5Hex(password)));
		user.setTelephone(telephone);

		userRepository.addUser(user);

		return user;
	}

	@Override
	public void createUser(User user) {
		userRepository.addUser(user);

	}

	@Override
	public User.UserSettings getSettings(int userId) {
		return userRepository.getSettings(userId);
	}

	@Override
	public User getUser(int userId) {
		User user = userRepository.getUser(userId);
		if (null == user)
			throw new ServiceException("用户不存在");
		if (0 != user.getCompanyId())
			user.setCompany(companyManager.get(user.getCompanyId()));
		return user;
	}

	@Override
	public User getUser(int userId, int toUserId) {
		User user = getUser(toUserId);

		Friends friends = friendsManager.getFriends(new Friends(userId,
				toUserId));
		user.setFriends(null == friends ? null : friends);

		// if (userId == toUserId) {
		// List<ResumeV2> resumeList = resumeManager.selectByUserId(toUserId);
		// user.setResumeList(resumeList);
		// }

		return user;
	}
	
	@Override
	public User getUser(String telephone) {
		return userRepository.getUser(telephone);
	}
	
	@Override
	public int getUserId(String accessToken) {
		return 0;
	}

	@Override
	public boolean isRegister(String telephone) {
		return 1 == userRepository.getCount(telephone);
	}

	@Override
	public User login(String telephone, String password) {
		String userKey = DigestUtils.md5Hex(telephone);

		User user = userRepository.getUser(userKey, null);

		if (null == user) {
			throw new ServiceException("帐号不存在");
		} else {
			String _md5 = DigestUtils.md5Hex(password);
			String _md5_md5 = DigestUtils.md5Hex(_md5);

			if (_md5.equals(user.getPassword())
					|| _md5_md5.equals(user.getPassword())) {
				return user;
			} else {
				throw new ServiceException("帐号或密码错误");
			}
		}
	}

	@Override
	public Map<String, Object> login(UserExample example) {
		User user = userRepository.getUser(example.getTelephone(), null);
		if (null == user) {
			throw new ServiceException(1040101, "帐号不存在");
		} else {
			// if
			// (DigestUtils.md5Hex(example.getPassword()).equals(user.getPassword()))
			// {
			String password = example.getPassword();
			String _md5 = DigestUtils.md5Hex(password);
			String _md5_md5 = DigestUtils.md5Hex(_md5);

			if (password.equals(user.getPassword())
					|| _md5.equals(user.getPassword())
					|| _md5_md5.equals(user.getPassword())) {
				// 获取上次登录日志
				Object login = userRepository.getLogin(user.getUserId());
				// 保存登录日志
				userRepository.updateLogin(user.getUserId(), example);
				// f1981e4bd8a0d6d8462016d2fc6276b3
				Map<String, Object> data = userRepository.getAT(
						user.getUserId(), example.getTelephone());
				data.put("userId", user.getUserId());
				data.put("nickname", user.getNickname());
				data.put("companyId", user.getCompanyId());
				data.put("login", login);
				// 保存登录日志

				return data;
			}
			throw new ServiceException(1040102, "帐号密码错误");
		}
	}

	@Override
	public Map<String, Object> loginAuto(String access_token, int userId,
			String serial) {
		try {
			User.LoginLog loginLog = userRepository.getLogin(userId);
			boolean exists = jedisTemplate
					.execute(new JedisCallback<Boolean>() {

						@Override
						public Boolean execute(Jedis jedis) {
							String atKey = KKeyConstant.userIdKey(access_token);
							return jedis.exists(atKey);
						}

					});
			// 1=没有设备号、2=设备号一致、3=设备号不一致
			int serialStatus = null == loginLog ? 1 : (serial.equals(loginLog
					.getSerial()) ? 2 : 3);
			// 1=令牌存在、0=令牌不存在
			int tokenExists = exists ? 1 : 0;

			User user = getUser(userId);

			Map<String, Object> result = Maps.newHashMap();
			result.put("serialStatus", serialStatus);
			result.put("tokenExists", tokenExists);
			result.put("userId", userId);
			result.put("nickname", user.getNickname());
			result.put("name", user.getName());
			result.put("login", loginLog);

			return result;
		} catch (NullPointerException e) {
			throw new ServiceException("帐号不存在");
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void logout(String userKey, String accessToken) {
		jedisTemplate.execute(new JedisCallbackVoid() {

			@Override
			public void execute(Jedis jedis) {
				String atKey = KKeyConstant.atKey(userKey);
				String userIdKey = KKeyConstant.userIdKey(accessToken);

				jedis.del(atKey, userIdKey);
			}
		});
	}

	@Override
	public List<DBObject> query(UserQueryExample param) {
		return userRepository.queryUser(param);
	}

	@Override
	public Map<String, Object> register(UserExample example) {
		if (isRegister(example.getTelephone()))
			throw new ServiceException("手机号已被注册");

		// 生成userId
		Integer userId = idxManager.getUserId();
		// 新增用户
		Map<String, Object> data = userRepository.addUser(userId, example);

		if (null != data) {
			try {
				KXMPPServiceImpl.getInstance().register(userId.toString(),
						example.getPassword());
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				NearbyUser userPoi = new NearbyUser();
				// userPoi.setAddress(address);
				userPoi.setBirthday(example.getBirthday());
				userPoi.setDescription(example.getDescription());
				// userPoi.setDistance(distance);
				userPoi.setLatitude(example.getLatitude());
				userPoi.setLongitude(example.getLongitude());
				userPoi.setNickname(example.getNickname());
				// userPoi.setPoiId(poiId);
				userPoi.setSex(example.getSex());
				// userPoi.setTags(tags);
				// userPoi.setTitle(title);
				userPoi.setUserId(userId);
				nearbyManager.saveUser(userPoi);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (null == example.getCompanyId() || 0 == example.getCompanyId()) {
				try {
					Object cv = resumeManager.createCV(userId, example);
					data.put("cv", cv);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return data;
		}
		throw new ServiceException("用户注册失败");
	}

	@Override
	public Map<String, Object> registerIMUser(UserExample example) {
		if (isRegister(example.getTelephone()))
			throw new ServiceException("手机号已被注册");

		// 生成userId
		Integer userId = idxManager.getUserId();
		// 新增用户
		Map<String, Object> data = userRepository.addUser(userId, example);

		if (null != data) {
			try {
				KXMPPServiceImpl.getInstance().register(userId.toString(),
						example.getPassword());
			} catch (Exception e) {
				e.printStackTrace();
			}
			/*
			try {
				friendsManager.followUser(userId, 10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
			return data;
		}
		throw new ServiceException("用户注册失败");
	}

	@Override
	public void resetPassword(String telephone, String password) {
		userRepository.updatePassword(telephone, password);
	}

	@Override
	public void updatePassword(int userId, String oldPassword,
			String newPassword) {
		User user = getUser(userId);
		String _md5 = DigestUtils.md5Hex(oldPassword);
		String _md5_md5 = DigestUtils.md5Hex(_md5);

		if (oldPassword.equals(user.getPassword())
				|| _md5.equals(user.getPassword())
				|| _md5_md5.equals(user.getPassword())) {
			userRepository.updatePassowrd(userId, newPassword);
		} else
			throw new ServiceException("旧密码错误");
	}

	@Override
	public boolean updateSettings(UserSettings userSettings) {
		return false;
	}

	@Override
	public User updateUser(int userId, UserExample param) {
		return userRepository.updateUser(userId, param);
	}

	@Override
	public List<DBObject> findUser(int pageIndex, int pageSize) {
		return userRepository.findUser(pageIndex, pageSize);
	}

	@Override
	public List<Integer> getAllUserId() {
		return null;
	}

	@Override
	public void removeUser(UserExample example) {
		userRepository.removeUser(example.getTelephone());
	}

	@Override
	public User removeComunityCode(Integer userId, UserExample example) {
		return userRepository.removeComunityCode(userId, example);
	}
}
