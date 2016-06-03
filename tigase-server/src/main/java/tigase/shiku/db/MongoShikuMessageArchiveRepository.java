package tigase.shiku.db;

import java.net.UnknownHostException;
import java.util.Map;

import tigase.db.DBInitException;
import tigase.shiku.model.MessageModel;
import tigase.shiku.model.MucMessageModel;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoShikuMessageArchiveRepository implements
		ShikuMessageArchiveRepository {

	// private static final String HASH_ALG = "SHA-256";
	private static final String MUC_MSGS_COLLECTION = "shiku_muc_msgs";
	private static final String MSGS_COLLECTION = "shiku_msgs";

	private MongoClient mongo;
	private DB db;

	// private byte[] generateId(BareJID user) throws TigaseDBException {
	// try {
	// MessageDigest md = MessageDigest.getInstance(HASH_ALG);
	// return md.digest(user.toString().getBytes());
	// } catch (NoSuchAlgorithmException ex) {
	// throw new TigaseDBException("Should not happen!!", ex);
	// }
	// }

	@Override
	public void initRepository(String resource_uri, Map<String, String> params)
			throws DBInitException {
		try {
			MongoClientURI uri = new MongoClientURI(resource_uri);
			mongo = new MongoClient(uri);
			db = mongo.getDB(uri.getDatabase());

			// 初始化群组聊天记录集合
			DBCollection dbCollection = !db
					.collectionExists(MUC_MSGS_COLLECTION) ? db
					.createCollection(MUC_MSGS_COLLECTION, new BasicDBObject())
					: db.getCollection(MUC_MSGS_COLLECTION);
			dbCollection.createIndex(new BasicDBObject("room_jid_id", 1));
			dbCollection.createIndex(new BasicDBObject("room_jid_id", 1)
					.append("ts", 1));

			// 初始化聊天记录集合
			dbCollection = !db.collectionExists(MSGS_COLLECTION) ? db
					.createCollection(MSGS_COLLECTION, new BasicDBObject())
					: db.getCollection(MSGS_COLLECTION);
			dbCollection.createIndex(new BasicDBObject("sender", 1));
			dbCollection.createIndex(new BasicDBObject("receiver", 1));
			dbCollection.createIndex(new BasicDBObject("sender", 1).append(
					"receiver", 1));
			dbCollection.createIndex(new BasicDBObject("sender", 1).append(
					"receiver", 1).append("ts", 1));

		} catch (UnknownHostException ex) {
			throw new DBInitException(
					"Could not connect to MongoDB server using URI = "
							+ resource_uri, ex);
		}
	}

	@Override
	public void archiveMessage(MessageModel model) {
		BasicDBObject dbObj = new BasicDBObject(9);
		dbObj.put("body", model.getBody());
		dbObj.put("direction", model.getDirection());
		dbObj.put("message", model.getMessage());
		dbObj.put("receiver", model.getReceiver());
		dbObj.put("receiver_jid", model.getReceiver_jid());
		dbObj.put("sender", model.getSender());
		dbObj.put("sender_jid", model.getSender_jid());
		dbObj.put("ts", model.getTs());
		dbObj.put("type", model.getType());

		db.getCollection(MSGS_COLLECTION).insert(dbObj);
	}

	@Override
	public void archiveMessage(MucMessageModel model) {
		try {

			BasicDBObject dbObj = new BasicDBObject();
			dbObj.put("body", model.getBody());
			dbObj.put("event_type", model.getEvent_type());
			dbObj.put("message", "");
			dbObj.put("nickname", "");
			dbObj.put("public_event", 0);
			dbObj.put("room_jid_id", model.getRoom_id());
			dbObj.put("room_jid", model.getRoom_jid());
			dbObj.put("sender_jid", model.getSender_jid());
			dbObj.put("sender", model.getSender());
			dbObj.put("ts", model.getTs());

			db.getCollection(MUC_MSGS_COLLECTION).insert(dbObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
