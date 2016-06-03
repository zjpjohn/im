package tigase.server.xmppsession;

import java.net.UnknownHostException;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class UserDao {

    private static UserDao instance = new UserDao();

    public static UserDao getInstance() {
        return instance;
    }

    private DBCollection dbCollection;

    private UserDao() {
        try {
            MongoClient mongoClient = new MongoClient("172.16.100.26", 27019);
            this.dbCollection = mongoClient.getDB("imapi").getCollection("user");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveOfflineTime(String jidString) {
        // int userId = Integer.parseInt(jidString.substring(0, jidString.indexOf("@")));
        // DBObject q = new BasicDBObject("_id", userId);
        // DBObject o = new BasicDBObject("$set", new BasicDBObject("loginLog.offlineTime", System.currentTimeMillis() /
        // 1000));
        // dbCollection.update(q, o);
    }

}
