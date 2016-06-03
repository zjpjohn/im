package cn.xyz.repository.mongo;

import java.util.List;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.jedis.JedisCallback;
import cn.xyz.commons.support.jedis.JedisCallbackVoid;
import cn.xyz.commons.support.jedis.JedisTemplate;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Msg;
import cn.xyz.mianshi.vo.Praise;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.MsgPraiseRepository;
import cn.xyz.repository.MsgRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

@Service()
public class MsgPraiseRepositoryImpl implements MsgPraiseRepository {
	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	@Resource(name = "dsForRW")
	protected Datastore dsForRW;
	@Autowired
	private UserManager userService;
	@Autowired
	private MsgRepository circleService;

	@Override
	public ObjectId add(int userId, ObjectId msgId) {
		User user = userService.getUser(userId);

		if (!exists(userId, msgId)) {
			Praise entity = new Praise(ObjectId.get(), msgId, user.getUserId(),
					user.getNickname(), DateUtil.currentTimeSeconds());

			// 缓存赞
			jedisTemplate.execute(new JedisCallbackVoid() {

				@Override
				public void execute(Jedis jedis) {
					String key = String.format("msg:%1$s:praise",
							msgId.toString());
					String strings = entity.toString();

					Pipeline pipe = jedis.pipelined();
					pipe.lpush(key, strings);// 插入最新赞
					pipe.expire(key, 43200);// 重置过期时间
					pipe.sync();
				}
			});
			// 持久化赞
			dsForRW.save(entity);
			// 更新消息：赞+1、活跃度+1
			circleService.update(msgId, Msg.Op.Praise, 1);
			// 推送新赞

			return entity.getPraiseId();
		}

		return null;
	}

	@Override
	public boolean delete(int userId, ObjectId msgId) {
		// 删除评论
		Query<Praise> q = dsForRW.createQuery(Praise.class).field("msgId")
				.equal(msgId).field("userId").equal(userId);
		Praise praise = dsForRW.findAndDelete(q);
		// 刷新缓存
		jedisTemplate.execute(new JedisCallbackVoid() {

			@Override
			public void execute(Jedis jedis) {
				String key = String.format("msg:%1$s:praise", msgId.toString());
				String value = praise.toString();
				jedis.lrem(key, 0, value);
			}
		});
		// 更新消息：赞-1、活跃度-1
		circleService.update(msgId, Msg.Op.Praise, -1);

		return true;
	}

	@Override
	public List<Praise> find(ObjectId msgId, ObjectId praiseId, int pageIndex,
			int pageSize) {
		String key = String.format("msg:%1$s:praise", msgId.toString());
		boolean exists = jedisTemplate.keyExists(key);
		// 赞没有缓存、加载所有赞到缓存
		if (!exists) {
			List<Praise> praiseList = dsForRW.find(Praise.class).field("msgId")
					.equal(msgId).order("-_id").asList();

			jedisTemplate.execute(new JedisCallbackVoid() {

				@Override
				public void execute(Jedis jedis) {
					Pipeline pipe = jedis.pipelined();
					for (Praise praise : praiseList) {
						String string = praise.toString();
						pipe.lpush(key, string);
					}
					pipe.expire(key, 43200);// 重置过期时间
					pipe.sync();
				}
			});
		}

		List<String> textList = jedisTemplate
				.execute(new JedisCallback<List<String>>() {

					@Override
					public List<String> execute(Jedis jedis) {
						long start = pageIndex * pageSize;
						long end = pageIndex * pageSize + pageSize - 1;

						return jedis.lrange(key, start, end);
					}

				});

		// 缓存未命中、超出缓存范围
		if (0 == textList.size()) {
			List<Praise> praiseList = dsForRW.find(Praise.class).field("msgId")
					.equal(msgId).order("-_id").offset(pageIndex * pageSize)
					.limit(pageSize).asList();

			return praiseList;
		} else {
			try {
				List<Praise> praiseList = Lists.newArrayList();
				for (String text : textList) {
					// JSON.parseObject(text, Praise.class)
					Praise praise = new ObjectMapper().readValue(text,
							Praise.class);
					praiseList.add(praise);
				}
				return praiseList;
			} catch (Exception e) {
				throw new ServiceException("赞缓存解析失败");
			}
		}
	}

	@Override
	public boolean exists(int userId, ObjectId msgId) {
		Query<Praise> q = dsForRW.createQuery(Praise.class).field("msgId")
				.equal(msgId).field("userId").equal(userId);
		long count = q.countAll();

		return 0 != count;
	}

}
