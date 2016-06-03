package test;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class DBUtil {
	public static final String uri = "mongodb://192.168.97.252:27017";

	public static DB getDB() {
		try {
			MongoClient mongo = new MongoClient(new MongoClientURI(uri));
			return mongo.getDB("imapi");
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String... args) {

		BasicDBObject dbObj = new BasicDBObject();
		dbObj.put("nickname", 1);
		dbObj.put("birthday", 1);
		dbObj.put("sex", 1);
		dbObj.put("active", 1);
		dbObj.put("coordinate", "2dsphere");

		getDB().getCollection("samples").createIndex(dbObj);
	}

}
