package test;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class ConfigBuild {
	public static final String uri = "mongodb://114.119.6.150:27017";
	public static MongoClient mongoClient;

	static {
		try {
			mongoClient = new MongoClient(new MongoClientURI(uri));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// System.out.println(DigestUtils.md5Hex("123456"));
		// init();
		// DBObject jo = new BasicDBObject();
		// jo.put("isFirstLogin", 1);
		// jo.put("loginTime", DateUtil.currentTimeSeconds());
		// jo.put("apiVersion", "");
		// jo.put("osVersion", "");
		// jo.put("model", "");
		// jo.put("serial", "");
		// jo.put("latitude", 0);
		// jo.put("longitude", 0);
		// jo.put("location", "");
		// jo.put("address", "");
		// DBCollection dbColl =
		// mongoClient.getDB("mianshi").getCollection("user");
		// dbColl.update(new BasicDBObject(), new BasicDBObject("$set", new
		// BasicDBObject("loginLog", jo)), false, true);

		// String s =
		// "[\"5461df5aa3108b8e792e3fbd\",\"5461df5aa3108b8e792e3fbd\"]";
		// List<ObjectId> r2 = new ObjectMapper().readValue(s,
		// TypeFactory.defaultInstance().constructCollectionType(List.class,
		// ObjectId.class));
		// System.out.println(r2);
		//
		// for (ObjectId a : r2) {
		// System.out.println(a);
		// }
		// List<MyBean> result = mapper.readValue(src,
		// TypeFactory.collectionType(ArrayList.class, MyBean.class));
		// DBCollection dbCollection = mongoClient.getDB("imapi").getCollection(
		// "config");
		// DBObject dbObj = dbCollection.findOne(new BasicDBObject("_id", 1));
		// System.out.println(dbObj);
		//
		// up1();
		setClassNameEmpty("imapi");
		setClassNameEmpty("tigase");
	}

	public static void setClassNameEmpty(String dbName) {
		System.out.println(dbName);
		for (String name : mongoClient.getDB(dbName).getCollectionNames()) {
			if (name.contains("system.indexes"))
				continue;
			System.out.println(name);
			DBCollection dbColl = mongoClient.getDB(dbName).getCollection(name);
			dbColl.update(new BasicDBObject(), new BasicDBObject("$set",
					new BasicDBObject("className", "")), false, true);

			dbColl.update(new BasicDBObject(), new BasicDBObject("$unset",
					new BasicDBObject("className", "")), false, true);

		}
	}

	public static void up1() {
		DBCollection dbCollection = mongoClient.getDB("imapi").getCollection(
				"config");

		DBObject dbObj = new BasicDBObject();
		dbObj.put("XMPPDomain", "www.youjob.co日你妈");
		dbObj.put("XMPPHost", "www.youjob.co");
		dbObj.put("apiUrl", "http://imapi.youjob.co/");
		dbObj.put("downloadAvatarUrl", "http://file.youjob.co/");
		dbObj.put("downloadUrl", "http://file.youjob.co/");
		dbObj.put("uploadUrl", "http://www.youjob.co/");

		DBObject q = new BasicDBObject();
		q.put("_id", new ObjectId("55b20f2bc6054581a0e3d7e9"));

		DBObject o = new BasicDBObject();
		o.put("$set", dbObj);
		System.out.println(ObjectId.get());
		dbCollection.update(q, o);
	}

	public static void tt() throws Exception {
		// DBCollection dbCollection =
		// TestMorphia.dsForRW.getDB().getCollection("config");
		// DBObject dbObject = dbCollection.findOne();
		// System.out.println(dbObject);
		//
		MongoClient mongoClient = new MongoClient(new MongoClientURI(uri));
		// mongoClient.getDB("mianshi").getCollection("config").save(dbObject);

		DBCollection dbCollection = mongoClient.getDB("mianshi").getCollection(
				"config");

		// DBObject entity = new BasicDBObject();
		// entity.put("apiUrl", "http://192.168.1.240/api/v1/");
		// entity.put("uploadUrl", "http://192.168.1.240/");
		// entity.put("downloadUrl", "http://192.168.1.240/");
		// entity.put("downloadAvatarUrl", "http://192.168.1.240/");

		DBObject entity = new BasicDBObject();
		entity.put("apiUrl", "http://121.37.30.25:8090/api/v1/");
		entity.put("uploadUrl", "http://121.37.30.25/");
		entity.put("downloadUrl", "http://121.37.30.25/");
		entity.put("downloadAvatarUrl", "http://121.37.30.25/");
		entity.put("freeswitch", "121.37.30.25");

		DBObject q = new BasicDBObject();
		q.put("_id", new ObjectId("53d0d7c945c9af43903a0c66"));

		DBObject o = new BasicDBObject();
		o.put("$set", entity);

		dbCollection.update(q, o);
	}
}
