package cn.xyz.mianshi.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.mapper.BasicMapper;
import cn.xyz.mianshi.service.AdminManager;
import cn.xyz.mianshi.vo.AreaVO;
import cn.xyz.mianshi.vo.OptionVO;
import cn.xyz.repository.MongoRepository;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Service()
public class AdminManagerImpl extends MongoRepository implements AdminManager {

	@Autowired
	private BasicMapper basicMapper;
	@Resource(name = "dsForRW")
	private Datastore dsForRW;
	@Resource(name = "dsForTigase")
	private Datastore dsForTigase;

	@Override
	public List<AreaVO> getAreaList(int parentId) {
		return basicMapper.getAreaList(parentId);
	}

	@Override
	public List<OptionVO> getDiplomaOptionList() {
		return basicMapper.getOptionList(1);
	}

	@Override
	public List<AreaVO> getProvinceList() {
		return basicMapper.getAreaList(1);
	}

	@Override
	public List<OptionVO> getSalaryOptionList() {
		return basicMapper.getOptionList(3);
	}

	@Override
	public List<OptionVO> getWrokExpOptionList() {
		return basicMapper.getOptionList(2);
	}

	@Override
	public BasicDBObject getConfig() {
		// return (BasicDBObject) dsForRW
		// .getDB()
		// .getCollection("config")
		// .findOne(
		// new BasicDBObject("_id", new ObjectId(
		// "55b20f2bc6054581a0e3d7e9")));

		for (String name : dsForRW.getDB().getCollectionNames()) {
			if (name.contains("system.indexes"))
				continue;
			System.out.println("RESET COLLECTION：" + name);
			DBCollection dbColl = dsForRW.getDB().getCollection(name);
			dbColl.update(new BasicDBObject(), new BasicDBObject("$set",
					new BasicDBObject("className", "")), false, true);
			dbColl.update(new BasicDBObject(), new BasicDBObject("$unset",
					new BasicDBObject("className", "")), false, true);
		}

		for (String name : dsForTigase.getDB().getCollectionNames()) {
			if (name.contains("system.indexes"))
				continue;
			System.out.println("RESET COLLECTION：" + name);
			DBCollection dbColl = dsForTigase.getDB().getCollection(name);
			dbColl.update(new BasicDBObject(), new BasicDBObject("$set",
					new BasicDBObject("className", "")), false, true);
			dbColl.update(new BasicDBObject(), new BasicDBObject("$unset",
					new BasicDBObject("className", "")), false, true);
		}

		return (BasicDBObject) dsForRW.getDB().getCollection("config")
				.findOne();
	}

	@Override
	public void initConfig() {
		DBObject versionInf = BasicDBObjectBuilder.start("disableVersion", "")
				.add("version", "").add("versionRemark", "").add("message", "")
				.get();
		BasicDBObject dbObj = new BasicDBObject();
		// dbObj.put("_id", new ObjectId("55b20f2bc6054581a0e3d7e9"));
		dbObj.put("XMPPDomain", "www.youjob.co");
		dbObj.put("XMPPHost", "www.youjob.co");
		dbObj.put("android", versionInf);
		dbObj.put("apiUrl", "http://imapi.youjob.co/");
		dbObj.put("downloadAvatarUrl", "http://file.youjob.co/");
		dbObj.put("downloadUrl", "http://file.youjob.co/");
		dbObj.put("ftpHost", "");
		dbObj.put("ftpPassword", "");
		dbObj.put("ftpUsername", "");
		dbObj.put("ios", versionInf);

		dbObj.put("uploadUrl", "http://www.youjob.co/");
		dbObj.put("freeswitch", "202.155.223.225");
		dbObj.put("meetingHost", "202.155.223.225");
		dbObj.put("shareUrl", "");
		dbObj.put("softUrl", "");
		dbObj.put("helpUrl", "http,//www.youjob.co/wap/help");
		dbObj.put("buyUrl", "");
		dbObj.put("videoLen", "");
		dbObj.put("audioLen", "");
		dbObj.put("money",
				BasicDBObjectBuilder.start("isCanChange", 0).add("Login", 0)
						.add("Share", 0).add("Intro", 0).get());

		dbObj.put("resumeBaseUrl", "http,//www.youjob.co/resume/wap");
		dbObj.put("aboutUrl", "");
		dbObj.put("website", "http,//www.youjob.co/");

		dsForRW.getDB().getCollection("config").save(dbObj);
	}

	@Override
	public void setConfig(DBObject dbObj) {
		DBCollection dbColl = dsForRW.getDB().getCollection("config");
		DBObject q = new BasicDBObject();
		// q.put("_id", new ObjectId("55b20f2bc6054581a0e3d7e9"));
		DBObject o = new BasicDBObject();
		o.put("$set", dbObj);

		dbColl.update(q, o);
	}
}
