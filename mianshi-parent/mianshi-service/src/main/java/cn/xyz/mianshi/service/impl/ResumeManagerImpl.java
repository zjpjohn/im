package cn.xyz.mianshi.service.impl;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.mianshi.example.UserExample;
import cn.xyz.mianshi.service.ResumeManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.cv.Edu;
import cn.xyz.mianshi.vo.cv.Personal;
import cn.xyz.mianshi.vo.cv.Project;
import cn.xyz.mianshi.vo.cv.ResumeV2;
import cn.xyz.mianshi.vo.cv.Work;
import cn.xyz.repository.MongoRepository;
import cn.xyz.repository.UserRepository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Service
public class ResumeManagerImpl extends MongoRepository implements ResumeManager {
	@Autowired
	private UserManager userManager;
	@Autowired
	private UserRepository userRepository;

	@Override
	public Object createCV(int userId, UserExample example) {
		Personal p = new Personal();
		// p.setAddress(address);
		p.setAreaId(example.getAreaId());
		p.setB(example.getBirthday());
		p.setCityId(example.getCityId());
		// p.setCo(co);
		p.setCountryId(example.getCountryId());
		p.setD(example.getD());
		p.setEmail(example.getEmail());
		// p.setEvaluate(evaluate);
		// p.setHomeCityId(homeCityId);
		// p.setIdNumber(idNumber);
		// p.setJ(j);
		// p.setLocation(location);
		// p.setM(m);
		// p.setMa(ma);
		p.setName(example.getNickname());
		p.setProvinceId(example.getProvinceId());
		p.setS(example.getSex());
		// p.setSalary(salary);
		// p.setSc(sc);
		p.setTelephone(example.getTelephone());
		p.setW(example.getW());

		ResumeV2 resumeV2 = new ResumeV2();
		resumeV2.setCreateTime(System.currentTimeMillis() / 1000);
		// resumeV2.setE(e);
		// resumeV2.setI(i);
		resumeV2.setModifyTime(resumeV2.getCreateTime());
		resumeV2.setP(p);
		// resumeV2.setResumeId(resumeId);
		resumeV2.setResumeName("简历" + resumeV2.getCreateTime());
		// resumeV2.setS(s);
		resumeV2.setUserId(userId);
		// resumeV2.setW(w);

		resumeV2.setE(Lists.newArrayList());
		resumeV2.setW(Lists.newArrayList());
		resumeV2.setProjectList(Lists.newArrayList());

		Object resumeId = save(userId, resumeV2);

		Map<String, Object> maps = Maps.newHashMap();
		maps.put("resumeId", resumeId);
		maps.put("resumeName", "简历" + resumeV2.getCreateTime());

		return maps;
	}

	@Override
	public Object delete(int userId, List<ObjectId> idList) {
		Query<ResumeV2> q = dsForRW.createQuery(ResumeV2.class).field("userId")
				.equal(userId).filter("_id in", idList);
		Object results = findAndDelete("u_resume", q.getQueryObject());
		return results;
	}

	@Override
	public Object deleteChild(int userId, ObjectId resumeId, String nodeName,
			ObjectId childId) {
		DBObject q = new BasicDBObject();
		q.put("userId", userId);
		q.put("_id", resumeId);

		DBObject o = new BasicDBObject();
		o.put("$pull", new BasicDBObject(nodeName, new BasicDBObject("id",
				childId)));

		dsForRW.getDB().getCollection(ResumeV2.DOC_NAME).update(q, o);

		return childId;
	}

	@Override
	public Object get(int userId, ObjectId resumtId, String nodeName) {
		// 优先使用简历Id获取简历
		return null == resumtId ? get(resumtId, nodeName) : get(userId,
				nodeName);
	}

	@Override
	public Object get(int userId) {
		Query<ResumeV2> q = dsForRW.createQuery(ResumeV2.class).field("userId")
				.equal(userId);
		ResumeV2 resume = q.get();
		if (null == resume)
			throw new ServiceException("简历不存在");

		return resume;
	}

	@Override
	public Object get(int userId, String nodeName) {
		BasicDBObject o = new BasicDBObject("userId", userId);
		DBObject fields = new BasicDBObject(nodeName, 1);
		DBObject dbObj = dsForRW.getDB().getCollection("u_resume")
				.findOne(o, fields);
		return null == dbObj ? null : dbObj.get(nodeName);
	}

	@Override
	public Object get(ObjectId resumtId) {
		Query<ResumeV2> q = dsForRW.createQuery(ResumeV2.class).field("_id")
				.equal(resumtId);
		ResumeV2 resume = q.get();
		if (null == resume)
			throw new ServiceException("简历不存在");

		return resume;
	}

	@Override
	public Object get(ObjectId resumtId, String nodeName) {
		BasicDBObject o = new BasicDBObject("_id", resumtId);
		DBObject fields = new BasicDBObject(nodeName, 1);
		DBObject dbObj = dsForRW.getDB().getCollection("u_resume")
				.findOne(o, fields);
		if (null == dbObj)
			throw new ServiceException("简历不存在");
		return dbObj.get(nodeName);
	}

	@Override
	public Object save(int userId, ResumeV2 resume) {
		if (null != resume.getE())
			for (Edu edu : resume.getE())
				edu.setId(ObjectId.get());
		if (null != resume.getW())
			for (Work work : resume.getW())
				work.setId(ObjectId.get());
		if (null != resume.getProjectList())
			for (Project project : resume.getProjectList())
				project.setId(ObjectId.get());
		return dsForRW.save(resume).getId();
	}

	@Override
	public Object saveChild(int userId, ObjectId resumeId, String nodeName,
			DBObject dbObj) {
		dbObj.put("id", ObjectId.get());

		DBObject q = new BasicDBObject();
		q.put("userId", userId);
		q.put("_id", resumeId);

		DBObject o = new BasicDBObject();
		o.put("$addToSet", new BasicDBObject(nodeName, new BasicDBObject(
				"$each", new Object[] { dbObj })));

		dsForRW.getDB().getCollection(ResumeV2.DOC_NAME).update(q, o);

		return dbObj.get("id").toString();
	}

	@Override
	public ResumeV2 selectById(int userId, ObjectId id) {
		Query<ResumeV2> query;
		if (null == id)
			query = dsForRW.createQuery(ResumeV2.class).field("s").equal(1)
					.field("userId").equal(userId);
		else
			query = dsForRW.createQuery(ResumeV2.class).field("_id").equal(id);
		return query.get();
	}

	@Override
	public Object selectById(int userId, ObjectId id, String nodeName) {
		DBObject o = new BasicDBObject("_id", id);
		DBObject fields = new BasicDBObject(nodeName, "1");
		fields.put("_id", 0);

		DBObject result = dsForRW.getDB().getCollection("u_resume")
				.findOne(o, fields);

		return null == result ? null : result.get(nodeName);
	}

	@Override
	public List<ResumeV2> selectByUserId(int userId) {
		Query<ResumeV2> query = dsForRW.createQuery(ResumeV2.class)
				.field("userId").equal(userId);
		return query.asList();
	}

	@Override
	public Object selectSimpleByUserId(int userId) {
		DBObject o = new BasicDBObject("userId", userId);
		DBObject fields = new BasicDBObject(4);
		fields.put("_id", 1);
		fields.put("resumeName", 1);
		fields.put("createTime", 1);
		fields.put("modifyTime", 1);

		List<DBObject> results = Lists.newArrayList();

		DBCursor cursor = dsForRW.getDB().getCollection("u_resume")
				.find(o, fields);
		while (cursor.hasNext()) {
			DBObject result = cursor.next();
			result.put("resumeId", result.get("_id").toString());
			result.removeField("_id");

			results.add(result);
		}
		cursor.close();

		return results;
	}

	@Override
	public Object updateChild(int userId, ObjectId resumeId, String nodeName,
			DBObject dbObj) {
		if (dbObj.containsField("id")) {
			DBObject q = new BasicDBObject();
			q.put("userId", userId);
			q.put("_id", resumeId);
			q.put(nodeName + ".id", new ObjectId(dbObj.get("id").toString()));

			DBObject set = new BasicDBObject();
			dbObj.keySet().forEach(key -> {
				if (!"id".equals(key))
					set.put(nodeName + ".$." + key, dbObj.get(key));
			});
			DBObject o = new BasicDBObject("$set", set);

			dsForRW.getDB().getCollection(ResumeV2.DOC_NAME).update(q, o);

			return dbObj.get("id").toString();
		} else
			return saveChild(userId, resumeId, nodeName, dbObj);
	}

	@Override
	public Object updateNode(int userId, ObjectId id, String nodeName,
			Object obj) {
		if ("p".equals(nodeName)) {
			BasicDBObject dbObj = (BasicDBObject) obj;
			User user = new User();
			user.setUserId(userId);
			user.setCountryId(dbObj.getInt("countryId"));
			user.setProvinceId(dbObj.getInt("provinceId"));
			user.setCityId(dbObj.getInt("cityId"));
			user.setAreaId(dbObj.getInt("areaId"));
			user.setName(dbObj.getString("name"));
			user.setNickname(dbObj.getString("name"));
			user.setSex(dbObj.getInt("s"));
			user.setBirthday(dbObj.getLong("b"));

			userRepository.updateUser(user);
		}

		if ("e".equals(nodeName) || "w".equals(nodeName)
				|| "projectList".equals(nodeName)) {
			BasicDBList dbObjList = (BasicDBList) obj;
			dbObjList.forEach(dbObj -> {
				((DBObject) dbObj).put("id", ObjectId.get());
			});
		}

		DBObject q = new BasicDBObject("_id", id);
		DBObject o = new BasicDBObject("$set", new BasicDBObject(nodeName, obj));
		dsForRW.getDB().getCollection("u_resume").findAndModify(q, o);

		return id;
	}

}
