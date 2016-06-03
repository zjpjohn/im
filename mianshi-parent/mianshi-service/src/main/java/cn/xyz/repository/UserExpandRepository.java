package cn.xyz.repository;

import java.util.List;

import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.User.ThridPartyAccount;

import com.mongodb.BasicDBObject;

public interface UserExpandRepository {

	JSONMessage roomAdd(Integer userId, BasicDBObject room);

	JSONMessage roomDelete(Integer userId, Integer roomId);

	JSONMessage roomList(Integer userId);

	boolean deleteAccount(int userId, String tpName);

	List<ThridPartyAccount> findAccounts(int userId);

	boolean addAccount(int userId, User.ThridPartyAccount account);
}
