package cn.xyz.repository.mongo;

import java.util.List;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.jedis.JedisCallback;
import cn.xyz.commons.support.jedis.JedisCallbackVoid;
import cn.xyz.commons.support.jedis.JedisTemplate;
import cn.xyz.commons.support.mongo.MongoOperator;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.example.AddCommentParam;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Comment;
import cn.xyz.mianshi.vo.Msg;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.MsgCommentRepository;
import cn.xyz.repository.MsgRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

@Service
public class MsgCommentRepositoryImpl implements MsgCommentRepository {

	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	@Resource(name = "dsForRW")
	protected Datastore dsForRW;
	@Autowired
	private UserManager userService;
	@Autowired
	private MsgRepository circleService;

	@Override
	public ObjectId add(int userId, AddCommentParam param) {
		User user = userService.getUser(userId);
		Comment entity = new Comment(ObjectId.get(), new ObjectId(
				param.getMessageId()), user.getUserId(), user.getNickname(),
				param.getBody(), param.getToUserId(), param.getToNickname(),
				param.getToBody(), DateUtil.currentTimeSeconds());

		// 缓存评论
		jedisTemplate.execute(new JedisCallbackVoid() {

			@Override
			public void execute(Jedis jedis) {
				String key = String.format("msg:%1$s:comment",
						param.getMessageId());

				Pipeline pipe = jedis.pipelined();
				pipe.lpush(key, entity.toString());// 插入最新评论
				pipe.ltrim(key, 0, 500);// 缓存最新500条评论
				pipe.expire(key, 43200);// 重置过期时间
				pipe.sync();
			}
		});
		// 保存评论
		dsForRW.save(entity);
		// 更新消息：评论数+1、活跃度+1
		circleService.update(new ObjectId(param.getMessageId()),
				Msg.Op.Comment, 1);
		// 推送新评论

		return entity.getCommentId();
	}

	@Override
	public boolean delete(int userId, ObjectId msgId, ObjectId commentId) {
		try {
			// 删除评论
			Comment comment = dsForRW.findAndDelete(dsForRW
					.createQuery(Comment.class).field(MongoOperator.ID)
					.equal(commentId));
			// 删除评论缓存
			jedisTemplate.execute(new JedisCallbackVoid() {

				@Override
				public void execute(Jedis jedis) {
					String key = String.format("msg:%1$s:comment",
							msgId.toString());
					jedis.lrem(key, 0, comment.toString());// 删除评论
				}
			});
			// 更新消息：评论数-1、活跃度-1
			circleService.update(msgId, Msg.Op.Comment, -1);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public List<Comment> find(ObjectId msgId, ObjectId commentId,
			int pageIndex, int pageSize) {
		String key = String.format("msg:%1$s:comment", msgId.toString());
		boolean exists = jedisTemplate.keyExists(key);

		// 评论没有缓存、加载最新N条评论到缓存
		if (!exists) {
			List<Comment> commentList = dsForRW.find(Comment.class)
					.field("msgId").equal(msgId).order("-_id").limit(500)
					.asList();
			jedisTemplate.execute(new JedisCallbackVoid() {

				@Override
				public void execute(Jedis jedis) {
					Pipeline pipe = jedis.pipelined();
					for (Comment comment : commentList) {
						String string = comment.toString();
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
			List<Comment> commentList = dsForRW.find(Comment.class)
					.field("msgId").equal(msgId).order("-_id")
					.offset(pageIndex * pageSize).limit(pageSize).asList();

			return commentList;
		} else {
			try {
				List<Comment> commentList = Lists.newArrayList();
				for (String text : textList) {
					Comment comment = new ObjectMapper().readValue(text,
							Comment.class);
					commentList.add(comment);
				}
				return commentList;
			} catch (Exception e) {
				throw new ServiceException("评论缓存解析失败");
			}
		}
	}
}
