package test;

import java.io.File;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public class TestMapDB {

	public static void main(String[] args) {
		DB db = DBMaker.newFileDB(new File("testdb")).closeOnJvmShutdown().encryptionEnable("password").make();
		Map<String, Object> test = db.getHashMap("test");
		// test.put("1", "你大爷");
		for (String key : test.keySet()) {
			System.out.println(key);
		}

		// db.commit();
		db.close();

	}

}
