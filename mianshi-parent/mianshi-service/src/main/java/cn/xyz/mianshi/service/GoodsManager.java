package cn.xyz.mianshi.service;

import java.util.List;

import cn.xyz.mianshi.vo.goods.Goods;
import cn.xyz.mianshi.vo.goods.GoodsVO;

public interface GoodsManager {

	/**
	 * 购买业务产品
	 * 
	 * @param userId
	 * @param goodsId
	 * @param count
	 * @return
	 */
	Object buyBizGoods(int userId, int goodsId, int count);

	/**
	 * 购买题库产品
	 * 
	 * @param userId
	 * @param goodsIdList
	 * @return
	 */
	Object buyExamGoods(int userId, List<Integer> goodsIdList);

	/**
	 * 购买充值产品
	 * 
	 * @param userId
	 * @param rechargeType
	 * @param goodsId
	 * @param count
	 * @return
	 */
	Object buyPayGoods(int userId, int rechargeType, int goodsId, int count);

	GoodsVO getGiftGoods(int goodsId);

	List<Goods> selectBizGoods();

	List<Goods> selectGoodsByCategoryId(int categoryId, int userId);

	List<Goods> selectGoodsByCategoryId(int categoryId);

	List<Goods> selectPayGoods();

	boolean updateOrder(String out_trade_no, String trade_no, String nodify_data);
}
