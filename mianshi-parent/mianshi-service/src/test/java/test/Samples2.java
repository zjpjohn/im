package test;

import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.vo.AuditionRT;

import com.google.common.collect.Lists;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public class Samples2 {
	static DBCollection coll = Samples1.dsForRW.getDB().getCollection("test");

	public static List<Object> selectId(String name, DBObject q) {
		List<Object> idList = Lists.newArrayList();

		DBCursor cursor = Samples1.dsForRW.getDB().getCollection(name).find(q, new BasicDBObject("_id", 1));
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		return idList;
	}

	public static List<Object> selectId(String name, QueryBuilder qb) {
		return selectId(name, qb.get());
	}

	public static void main(String[] args) {
		DBObject o = new BasicDBObject();
		o.put("_id", new BasicDBObject("$in", Lists.newArrayList(new ObjectId("5476e62aa310d6f87451551a"))));
		o.put("toStatus", new BasicDBObject("$ne", AuditionRT.ToStatus.REFUSE));
		selectId("a_art", o).forEach(obj -> {
			System.out.println(obj);
		});
		;
		// String keyword = "技雄";
		// Query<JobVO> q = Samples1.dsForRW.createQuery(JobVO.class);
		// q.or(q.criteria("jobName").containsIgnoreCase(keyword),
		// q.criteria("co.name").containsIgnoreCase(keyword));
		// List<JobVO> jobList = q.asList();
		// jobList.forEach(job -> {
		// System.out.println(job.getJobName());
		// });
		// System.out.println(getAvgScore(new
		// ObjectId("5476940c4826a4378fe626fe"), 50));

		// score(new ObjectId("5476940c4826a4378fe626fe"), 5, 20);

		// JobVO job =
		// Samples1.dsForRW.createQuery(JobVO.class).field("_id").equal(new
		// ObjectId("547427a3a3106ccc168bc0da")).get();
		// System.out.println(job);

		// List<ObjectId> idList = Lists.newArrayList(new
		// ObjectId("54768ea5a310ac668a0529f2"));
		// List<ObjectId> idList = Lists.newArrayList(new
		// ObjectId("5473f7f3a7c4ea8652fa21c0"));
		//
		// Query<AuditionRT> query =
		// Samples1.dsForRW.createQuery(AuditionRT.class);
		// // query.field("toUserId").equal(userId);
		// query.filter("_id in", idList);
		// System.out.println(query.countAll());
		// UpdateOperations<AuditionRT> ops =
		// Samples1.dsForRW.createUpdateOperations(AuditionRT.class);
		// ops.set("status", AuditionRT.ToStatus.REFUSE);
		// Samples1.dsForRW.update(query, ops);
	}

	public static void initColl() {
		DBObject results = new BasicDBObject();
		results.put("score", 0);
		results.put("scores", new Object[] {});
		DBObject jo = new BasicDBObject();
		jo.put("results", results);

		coll.save(jo);
	}

	public static void score(ObjectId artId, int userId, int score) {
		int avgScore = getAvgScore(artId, score);
		DBObject dbObj = new BasicDBObject();
		dbObj.put("userId", userId);
		dbObj.put("score", score);
		dbObj.put("time", DateUtil.currentTimeSeconds());

		QueryBuilder q = QueryBuilder.start();
		q.and("_id").is(artId);

		DBObject o = new BasicDBObject();
		o.put("$set", new BasicDBObject("results.score", avgScore));
		o.put("$addToSet", new BasicDBObject("results.scores", new BasicDBObject("$each", new Object[] { dbObj })));

		coll.update(q.get(), o);
	}

	static int getAvgScore(ObjectId artId, int score) {
		int total = score;

		DBObject project = new BasicDBObject("$project", new BasicDBObject("results", 1));
		DBObject match = new BasicDBObject("$match", new BasicDBObject("_id", artId));
		DBObject unwind = new BasicDBObject("$unwind", "$results.scores");
		List<DBObject> pipeline = Arrays.asList(project, match, unwind);
		AggregationOutput output = coll.aggregate(pipeline);
		List<DBObject> objList = Lists.newArrayList(output.results());

		for (DBObject obj : objList) {
			DBObject results = (DBObject) obj.get("results");
			BasicDBObject scores = (BasicDBObject) results.get("scores");
			total += scores.getInt("score");
		}

		int avg = total / (objList.size() + 1);

		return avg;

	}

}
