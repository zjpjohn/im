package cn.xyz.repository.mongo;

import java.util.List;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Service;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.vo.CompanyFans;
import cn.xyz.mianshi.vo.CompanyFriends;
import cn.xyz.mianshi.vo.Fans;
import cn.xyz.mianshi.vo.Friends;
import cn.xyz.repository.FriendsRepository;
import cn.xyz.repository.MongoRepository;

import com.google.common.collect.Lists;

@Service
public class FriendsRepositoryImpl extends MongoRepository implements FriendsRepository {

	@Override
	public CompanyFans deleteFans(CompanyFans fans) {
		Query<CompanyFans> q = dsForRW.createQuery(CompanyFans.class);
		q.field("userId").equal(fans.getUserId());
		q.field("companyId").equal(fans.getCompanyId());

		return dsForRW.findAndDelete(q);
	}

	@Override
	public Fans deleteFans(int userId, int toUserId) {
		Query<Fans> q = dsForRW.createQuery(Fans.class).field("userId").equal(userId).field("toUserId").equal(toUserId);

		return dsForRW.findAndDelete(q);
	}

	@Override
	public CompanyFriends deleteFriends(CompanyFriends friends) {
		Query<CompanyFriends> q = dsForRW.createQuery(CompanyFriends.class);
		q.field("userId").equal(friends.getUserId());
		q.field("companyId").equal(friends.getCompanyId());

		return dsForRW.findAndDelete(q);
	}

	@Override
	public Friends deleteFriends(int userId, int toUserId) {
		Query<Friends> q = dsForRW.createQuery(Friends.class).field("userId").equal(userId).field("toUserId").equal(toUserId);

		return dsForRW.findAndDelete(q);
	}

	@Override
	public CompanyFriends getFriends(CompanyFriends friends) {
		Query<CompanyFriends> q = dsForRW.createQuery(CompanyFriends.class);
		q.field("userId").equal(friends.getUserId());
		q.field("companyId").equal(friends.getCompanyId());

		return q.get();
	}

	@Override
	public Friends getFriends(int userId, int toUserId) {
		Query<Friends> q = dsForRW.createQuery(Friends.class);
		q.field("userId").equal(userId);
		q.field("toUserId").equal(toUserId);
		Friends friends = q.get();
		return friends;
	}

	@Override
	public List<Friends> queryBlacklist(int userId) {
		Query<Friends> q = dsForRW.createQuery(Friends.class).field("userId").equal(userId).field("blacklist").equal(1);

		return q.asList();
	}

	@Override
	public List<Fans> queryFans(int userId) {
		Query<Fans> q = dsForRW.createQuery(Fans.class).field("userId").equal(userId);

		return q.asList();
	}

	@Override
	public List<Integer> queryFansId(int userId) {
		Query<Fans> q = dsForRW.createQuery(Fans.class).retrievedFields(true, "toUserId").field("userId").equal(userId);
		q.filter("status !=", 0);

		List<Integer> result = Lists.newArrayList();
		List<Fans> fList = q.asList();

		fList.forEach(fans -> {
			result.add(fans.getToUserId());
		});

		return result;
	}

	@Override
	public List<Friends> queryFollow(int userId) {
		Query<Friends> q = dsForRW.createQuery(Friends.class).field("userId").equal(userId);
		q.filter("status !=", 0);
		// q.or(q.criteria("status").equal(Friends.Status.Attention),
		// q.criteria("status").equal(Friends.Status.Friends));

		return q.asList();

	}

	@Override
	public List<Integer> queryFollowId(int userId) {
		Query<Friends> q = dsForRW.createQuery(Friends.class).retrievedFields(true, "toUserId").field("userId").equal(userId);
		q.filter("status !=", 0);

		List<Integer> result = Lists.newArrayList();
		List<Friends> fList = q.asList();

		fList.forEach(friends -> {
			result.add(friends.getToUserId());
		});

		return result;
	}

	@Override
	public List<Friends> queryFriends(int userId) {
		Query<Friends> q = dsForRW.createQuery(Friends.class);
		q.field("userId").equal(userId);
		q.field("status").equal(Friends.Status.Friends);

		return q.asList();
	}

	@Override
	public Object saveFans(CompanyFans entity) {
		return dsForRW.save(entity).getId();
	}

	@Override
	public Object saveFans(Fans fans) {
		return dsForRW.save(fans).getId();
	}

	@Override
	public Object saveFriends(CompanyFriends entity) {
		return dsForRW.save(entity).getId();
	}

	@Override
	public Object saveFriends(Friends friends) {
		return dsForRW.save(friends);
	}

	@Override
	public Friends updateFriends(Friends friends) {
		Query<Friends> q = dsForRW.createQuery(Friends.class).field("userId").equal(friends.getUserId()).field("toUserId").equal(friends.getToUserId());

		UpdateOperations<Friends> ops = dsForRW.createUpdateOperations(Friends.class);
		ops.set("modifyTime", DateUtil.currentTimeSeconds());
		if (null != friends.getBlacklist())
			ops.set("blacklist", friends.getBlacklist());
		if (null != friends.getStatus())
			ops.set("status", friends.getStatus());
		if (!StringUtil.isEmpty(friends.getRemarkName()))
			ops.set("remarkName", friends.getRemarkName());

		return dsForRW.findAndModify(q, ops);
		// try {
		// // DBObject query = new BasicDBObject(2);
		// // query.put("userId", friends.getUserId());
		// // query.put("toUserId", friends.getToUserId());
		//
		// DBObject set = new BasicDBObject();
		// if (null != friends.getBlacklist())
		// set.put("blacklist", friends.getBlacklist());
		// if (null != friends.getStatus())
		// set.put("status", friends.getStatus());
		// if (!StringUtils.isEmpty(friends.getRemarkName()))
		// set.put("remarkName", friends.getRemarkName());
		// set.put("modifyTime", Dates.currentTimeSeconds());
		//
		// DBObject update = new BasicDBObject("$set", set);
		//
		// dsForRW.getDB().getCollection("u_friends")
		// .findAndModify(query, update);
		//
		// return true;
		// } catch (Exception e) {
		// logger.error("更新好友关系失败", e);
		// }
		//
		// return false;
	}

	@Override
	public List<Integer> queryFollowCompanyId(int userId) {
		Query<CompanyFriends> q = dsForRW.createQuery(CompanyFriends.class).retrievedFields(true, "companyId").field("userId").equal(userId);

		List<Integer> idList = Lists.newArrayList();

		List<CompanyFriends> oList = q.asList();
		oList.forEach(fans -> {
			idList.add(fans.getCompanyId());
		});

		return idList;
	}

}
