package cn.xyz.mianshi.service;

import java.util.List;
import java.util.Map;

import cn.xyz.mianshi.example.UserExample;
import cn.xyz.mianshi.example.UserQueryExample;
import cn.xyz.mianshi.vo.User;

import com.mongodb.DBObject;

public interface UserManager {

	User createUser(String telephone, String password);

	void createUser(User user);

	User.UserSettings getSettings(int userId);

	User getUser(int userId);

	User getUser(int userId, int toUserId);

	User getUser(String telephone);
	
	int getUserId(String accessToken);

	boolean isRegister(String telephone);

	User login(String telephone, String password);

	Map<String, Object> login(UserExample example);

	Map<String, Object> loginAuto(String access_token, int userId, String serial);

	void logout(String userKey, String accessToken);

	List<DBObject> query(UserQueryExample param);

	Map<String, Object> register(UserExample example);

	Map<String, Object> registerIMUser(UserExample example);

	void resetPassword(String telephone, String password);

	void updatePassword(int userId, String oldPassword, String newPassword);

	boolean updateSettings(User.UserSettings userSettings);

	User updateUser(int userId, UserExample example);

	List<DBObject> findUser(int pageIndex, int pageSize);

	List<Integer> getAllUserId();

	void removeUser(UserExample example);

	User removeComunityCode(Integer userId, UserExample example);

}
