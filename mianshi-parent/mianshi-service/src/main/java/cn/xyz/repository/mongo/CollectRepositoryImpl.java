package cn.xyz.repository.mongo;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Service;

import cn.xyz.mianshi.vo.UserCollect;
import cn.xyz.repository.CollectRepository;
import cn.xyz.repository.MongoRepository;

@Service
public class CollectRepositoryImpl extends MongoRepository implements CollectRepository {

	@Override
	public Object add(UserCollect entity) {
		return dsForRW.save(entity).getId();
	}

	@Override
	public Object delete(ObjectId collectId) {
		Query<UserCollect> q = dsForRW.createQuery(UserCollect.class).field(Mapper.ID_KEY).equal(collectId);
		return dsForRW.findAndDelete(q);
	}

	@Override
	public List<UserCollect> find(int userId, int type, ObjectId collectId, int pageSize) {
		Query<UserCollect> q = dsForRW.createQuery(UserCollect.class).field("userId").equal(userId).field("type").equal(type);
		if (null != collectId)
			q.field("collectId").lessThan(collectId);

		return q.order("-_id").limit(pageSize).asList();
	}

}
