package cn.xyz.commons.autoconfigure;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PreDestroy;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.xyz.mianshi.vo.User;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

@Configuration
public class KMongoAutoConfiguration {
	private Morphia morphiaForTigase;
	private Datastore dsForTigase;

	private Morphia morphia;
	private Datastore ds;

	private MongoClient mongoClient;

	@Autowired
	private KMongoProperties properties;

	@PreDestroy
	public void close() {
		if (null != mongoClient)
			mongoClient.close();
	}

	@Bean(name = "mongoClient", destroyMethod = "close")
	public MongoClient mongo() throws UnknownHostException {
		List<ServerAddress> seeds = Lists.newArrayList(new ServerAddress(
				properties.getHost(), properties.getPort()));

		MongoClientOptions.Builder builder = MongoClientOptions.builder();
		builder.socketKeepAlive(true);
		builder.socketTimeout(2000);
		builder.maxWaitTime(2000);
		builder.heartbeatFrequency(2000);// 心跳频率

		mongoClient = new MongoClient(seeds);
		// this.mongo.setReadPreference(ReadPreference.);

		return mongoClient;

	}

	@Bean(name = "morphiaForTigase")
	public Morphia morphiaForTigase() {
		morphiaForTigase = new Morphia();
		morphiaForTigase.map(cn.xyz.mianshi.vo.Room.class);
		morphiaForTigase.map(cn.xyz.mianshi.vo.Room.Member.class);
		morphiaForTigase.map(cn.xyz.mianshi.vo.Room.Notice.class);

		return morphiaForTigase;
	}

	@Bean(name = "morphia")
	public Morphia morphia() {
		// IMPORTANT SPRING-BOOT JAR 应用在LINUX系统下时，mapPackage无法加载jar文件中的类
		// 详见ReflectionUtils类486、487行
		//
		morphia = new Morphia();
		// morphia.mapPackage("com.shiku.mianshi.vo", false);
		// 手动加载
		if (0 == morphia.getMapper().getMappedClasses().size()) {
			morphia.map(cn.xyz.mianshi.vo.AuditionFT.class);
			morphia.map(cn.xyz.mianshi.vo.AuditionRT.class);
			morphia.map(cn.xyz.mianshi.vo.CheckVO.class);
			morphia.map(cn.xyz.mianshi.vo.Comment.class);

			morphia.map(cn.xyz.mianshi.vo.CompanyFans.class);
			morphia.map(cn.xyz.mianshi.vo.CompanyFriends.class);

			morphia.map(cn.xyz.mianshi.vo.Fans.class);
			morphia.map(cn.xyz.mianshi.vo.Friends.class);
			morphia.map(cn.xyz.mianshi.vo.Gift.class);
			morphia.map(cn.xyz.mianshi.vo.JobApply.class);
			morphia.map(cn.xyz.mianshi.vo.JobVO.class);

			morphia.map(cn.xyz.mianshi.vo.Msg.class);
			morphia.map(cn.xyz.mianshi.vo.NoticeVO.class);
			morphia.map(cn.xyz.mianshi.vo.Praise.class);
			morphia.map(cn.xyz.mianshi.vo.User.class);

			morphia.map(cn.xyz.mianshi.vo.UserCollect.class);
		}

		return morphia;
	}

	@Bean(name = "dsForRW")
	public Datastore dsForRW(MongoClient mongoClient) {
		ds = morphia().createDatastore(mongoClient, properties.getDbName());
		ds.ensureIndexes();
		ds.ensureCaps();
		try {
			BasicDBObject keys = new BasicDBObject();
			keys.put("coordinate", "2d");
			keys.put("nickname", 1);
			keys.put("sex", 1);
			keys.put("birthday", 1);
			keys.put("active", 1);

			DBCollection dbCollection = ds.getCollection(User.class);
			dbCollection.createIndex(keys);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}

	public Datastore dsForRead(MongoClient mongoClient) {
		// TODO MONGODB、只读库实现
		return null;
	}

	public Datastore dsForWrite(MongoClient mongoClient) {
		// TODO MONGODB、只写库实现
		return null;
	}

	@Bean(name = "dsForTigase")
	public Datastore dsForTigase(MongoClient mongoClient) {
		dsForTigase = morphiaForTigase().createDatastore(mongoClient, "tigase");
		dsForTigase.ensureIndexes();
		dsForTigase.ensureCaps();

		return dsForTigase;
	}

}
