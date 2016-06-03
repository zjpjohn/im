package test;

import java.lang.reflect.Modifier;
import java.net.UnknownHostException;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.utils.ReflectionUtils;

import cn.xyz.commons.support.spring.converter.MappingFastjsonHttpMessageConverter.ObjectIdSerializer;
import cn.xyz.mianshi.vo.Msg;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.User.ThridPartyAccount;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class TestMorphia {
	private static final SerializeConfig serializeConfig = new SerializeConfig();

	public static final Morphia morphia;
	public static Datastore dsForRW;
	public static final String uri = "mongodb://114.119.6.150:27017";

	static {
		serializeConfig.put(ObjectId.class, new ObjectIdSerializer());
		morphia = new Morphia();
		morphia.mapPackage("cn.xyz.mianshi.vo", true);
		try {
			MongoClient mongoClient = new MongoClient(new MongoClientURI(uri));
			dsForRW = morphia.createDatastore(mongoClient, "imapi");
			dsForRW.ensureIndexes();
			dsForRW.ensureCaps();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	static String ss = "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊";

	public static void main(String... args) throws Exception {
		// Query<Friends> q = dsForRW.createQuery(Friends.class)
		// .retrievedFields(true, "toUserId").field("userId")
		// .equal(10000028);
		// q.filter("status !=", 0);
		//
		// List<Integer> result = Lists.newArrayList();
		// List<Friends> fList = q.asList();

		// List<ObjectId> applyIdList = Lists.newArrayList(new
		// ObjectId("5469d5d8a310b457963c8958"));
		// Query<JobApply> q = dsForRW.createQuery(JobApply.class);
		// q.field("companyId").equal(1);
		// q.field("status").equal(1);
		// q.filter("_id in", applyIdList);
		// // applyIdList中未处理的职位申请记录
		// List<JobApply> jobApplyList = q.asList();
		// System.out.println(jobApplyList);
		// String s =
		// "[\"5461df92a3108b8e792e3fbe\",\"5461df92a3108b8e792e3fbf\",\"5461df5aa3108b8e792e3fbd\"]";
		// s = "[\"5461df92a3108b8e792e3fbf\"]";
		// ObjectId[] idList = new ObjectMapper().readValue(s,
		// ObjectId[].class);
		// DBObject q = new BasicDBObject(Mapper.ID_KEY, new
		// BasicDBObject("$in", idList));
		// // upsert 如果不存在update的记录，是否插入objNew
		// DBObject o = new BasicDBObject("$set", new BasicDBObject("jobName",
		// "111111111111"));
		// WriteResult result = dsForRW.getDB().getCollection("job").update(q,
		// o, false, true);
		// System.out.println(result);
		// List<JobVO> jobList = Lists.newArrayList();
		// for (int i = 0; i < 10000; i++) {
		// JobVO.Company co = new JobVO.Company();
		// co.setId(1);
		// co.setName("测试公司啊啊啊" + i % 3 + "是是啊啊啊");
		// co.setNature(1);
		// co.setScale(1);
		//
		// JobVO job = new JobVO();
		// job.setAreaId(0);
		// job.setCityId(10000);
		// job.setCo(co);
		// // job.setCount(count);
		// job.setCountryId(1000);
		// job.setDescription(ss);
		// job.setDip(15);
		// job.setExpiredTime(System.currentTimeMillis());
		// job.setJobId(ObjectId.get());
		// job.setJobName("测试软件工程" + i + "师啊啊啊啊啊" + i);
		// job.setProvinceId(1234123);
		// job.setPublishTime(System.currentTimeMillis());
		// job.setSalary(234);
		// job.setStatus(1);
		// job.setUserId(10004);
		// job.setVacancy(100);
		// job.setWorkExp(123);
		//
		// jobList.add(job);
		// }
		// long a = System.currentTimeMillis();
		// dsForRW.save(jobList);
		// System.out.println(System.currentTimeMillis() - a);
		// DBObject o = new BasicDBObject("userId", 100004);
		// DBObject fields = new BasicDBObject("personal", "1");
		// fields.put("_id", 0);
		//
		// System.out.println(dsForRW.getDB().getCollection("user_resume").findOne(o,
		// fields));
		// testFind2();
		// DBCollection collection = dsForRW.getCollection(User.class);
		// DBCursor cursor = collection.find();
		// while (cursor.hasNext())
		// System.out.println(cursor.next());
		// cursor.close();
		// testDelete();
		// testAdd();
		// testUnset();
		// testDelete1();
		// testDelete3();
	}

	// filter("yearsOfOperation >", 5)
	// filter("rooms.maxBeds >=", 2)
	// filter("rooms.bathrooms exists", 1)
	// filter("stars in", new Long[]{3, 4}) //3 and 4 stars (midrange?)}
	// filter("quantity mod", new Long[]{4, 0}) // customers ordered in packs of
	// 4)}
	// filter("age >=", age)
	// filter("age =", age)
	// filter("age", age) (if no operator, = is assumed)
	// filter("age !=", age)
	// filter("age in", ageList)
	// filter("customers.loyaltyYears in", yearsList)

	public static void testFind() {
		List<Integer> userIdList = Lists.newArrayList();
		userIdList.add(100841);
		DBObject ref = new BasicDBObject("userId", new BasicDBObject("$in",
				userIdList));
		// if (!StringUtils.isEmpty(_id))
		// ref.put("_id", new BasicDBObject("$lt", new ObjectId(_id)));
		DBObject keys = new BasicDBObject();
		keys.put("_id", 1);
		keys.put("userId", 1);
		keys.put("nickname", 1);

		DBCursor cursor = dsForRW.getDB().getCollection("message")
				.find(ref, keys).sort(new BasicDBObject("_id", -1)).limit(10);
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			// dbObj.put("msgId", dbObj.get("_id").toString());
			// dbObj.removeField("_id");
			// System.out.println(dbObj);
			// list.add(dbObj);
			Msg msg = morphia.fromDBObject(Msg.class, dbObj);
			System.out.println(JSON.toJSONString(msg));
			System.out.println(morphia.toDBObject(msg));
		}
	}

	public static void testFind2() {
		List<Integer> userIdList = Lists.newArrayList();
		userIdList.add(100841);

		Query<Msg> query = dsForRW.createQuery(Msg.class)
				.retrievedFields(true, "userId", "nickname")
				.filter("userId in", userIdList);
		query.filter("_id <", new ObjectId("541021dde4b0f49faafdab59"));
		List<Msg> msgList = query.order("-_id").limit(2).asList();
		for (Msg msg : msgList) {
			// System.out.println(morphia.toDBObject(msg));
			System.out.println(JSON.toJSONString(msg, serializeConfig));
		}
	}

	public static void testAdd() {
		ThridPartyAccount account = new ThridPartyAccount();
		account.setTpName("WEIBO");
		account.setTpAccount("luorongchun@gmail.com");
		account.setTpUserId("5201314");

		Query<User> query = dsForRW.createQuery(User.class)
				.field(Mapper.ID_KEY).equal(100861);
		UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class)
				.add("accounts", account);

		dsForRW.update(query, ops);
	}

	public static void testUnset() {
		Query<User> query = dsForRW.createQuery(User.class)
				.field(Mapper.ID_KEY).equal(100861);
		UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class)
				.unset("accounts");

		dsForRW.update(query, ops);
	}

	public static void testDelete1() {
		DBCollection photoCollection = dsForRW.getDB().getCollection("user");

		DBObject q = new BasicDBObject("_id", 100861);
		DBObject o = new BasicDBObject();
		o.put("$pull", new BasicDBObject("accounts", new BasicDBObject(
				"tpName", "WEIBO")));

		photoCollection.update(q, o);
	}

	// errors,can't delete
	public static void testDelete2() {
		DBCollection photoCollection = dsForRW.getDB().getCollection("user");

		DBObject q = new BasicDBObject("_id", 100861);
		DBObject o = new BasicDBObject();
		o.put("$pull", new BasicDBObject("accounts.tpName", "WEIBO"));

		photoCollection.update(q, o);
	}

	public static void testDelete3() {
		Query<User> query = dsForRW.createQuery(User.class)
				.field(Mapper.ID_KEY).equal(100861);
		UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class)
				.removeAll("accounts", new BasicDBObject("tpName", "WEIBO"));
		dsForRW.update(query, ops);
	}

	public static void testAddToSet() {
		DBCollection collection = dsForRW.getCollection(User.class);
		ThridPartyAccount account = new ThridPartyAccount();
		account.setTpName("TENCENT");
		account.setTpAccount("9443980291111111111111111");
		account.setTpUserId("123456789");

		DBObject q = new BasicDBObject();
		q.put("_id", 2);
		DBObject o = new BasicDBObject();
		o.put("$addToSet",
				new BasicDBObject("accounts", morphia.toDBObject(account)));
		collection.update(q, o);
	}

	public static void testMapper() throws Exception {
		for (final Class<?> clazz : ReflectionUtils
				.getClasses("com.shiku.mianshi.vo")) {

			try {
				final Embedded embeddedAnn = ReflectionUtils
						.getClassEmbeddedAnnotation(clazz);
				final Entity entityAnn = ReflectionUtils
						.getClassEntityAnnotation(clazz);
				final boolean isAbstract = Modifier.isAbstract(clazz
						.getModifiers());
				if ((entityAnn != null || embeddedAnn != null) && !isAbstract) {
					System.out.println(clazz.getName());
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
