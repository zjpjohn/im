package cn.xyz.mapper;

import java.util.List;

import cn.xyz.mianshi.vo.goods.Consume;
import cn.xyz.mianshi.vo.goods.Goods;
import cn.xyz.mianshi.vo.goods.Order;

public interface GoodsMapper {

	Goods getGoods(int goodsId);

	Order getOrder(long orderSeq);

	int saveConsume(Consume consume);

	int saveOrder(Order order);

	List<Goods> selectGoodsByCategoryId(int categoryId);

	List<Goods> selectBizGoods();

	int updateOrder(Order order);

}
