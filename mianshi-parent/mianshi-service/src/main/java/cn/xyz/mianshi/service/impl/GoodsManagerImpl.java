package cn.xyz.mianshi.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mapper.CompanyMapper;
import cn.xyz.mapper.ExamMapper;
import cn.xyz.mapper.GoodsMapper;
import cn.xyz.mianshi.service.CompanyManager;
import cn.xyz.mianshi.service.GoodsManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.CompanyVO;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.goods.Consume;
import cn.xyz.mianshi.vo.goods.Goods;
import cn.xyz.mianshi.vo.goods.GoodsVO;
import cn.xyz.mianshi.vo.goods.Order;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class GoodsManagerImpl implements GoodsManager {

	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private CompanyMapper companyMapper;
	@Resource(name = "dsForRW")
	private Datastore dsForRW;
	@Autowired
	private ExamMapper examMapper;
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private UserManager userManager;

	@Transactional
	@Override
	public Object buyBizGoods(int userId, int goodsId, int count) {
		User user = userManager.getUser(userId);
		CompanyVO company = companyManager.get(user.getCompanyId());
		Goods goods = goodsMapper.getGoods(goodsId);
		int vcount = goods.getPrice() * count;// 购买所需虚拟币
		int balance = company.getBalance() - vcount;// 本次购买完成余额
		// 余额不足
		if (company.getBalance() < vcount) {
			throw new ServiceException("账户余额不足");
		}

		// 虚拟币消费记录
		Consume consume = new Consume();
		consume.setConsumeTypeId(1);
		consume.setCompanyId(user.getCompanyId());
		consume.setUserId(userId);
		consume.setToUserId(0);
		consume.setVcount(vcount);
		consume.setBalance(balance);
		consume.setStatus(1);
		Map<String, Object> parameter = Maps.newHashMap();
		parameter.put("companyId", company.getCompanyId());
		parameter.put("vcount", 0 - vcount);// 消耗虚拟币

		if (2 == goods.getCategoryId()) {
			long payEndTime = company.getPayEndTime() < DateUtil.currentTimeSeconds() ? DateUtil.currentTimeSeconds() : company
					.getPayEndTime();
			parameter.put("payEndTime", payEndTime + (goods.getValue() * count * 86400));
		} else if (3 == goods.getCategoryId()) {
			int v = goods.getValue() * count;// 视频面试服务次数
			parameter.put("v", v);
		} else {
			throw new ServiceException("不支持购买的业务产品");
		}

		goodsMapper.saveConsume(consume);
		companyMapper.updateOthers(parameter);

		return consume;
	}

	@Override
	public Object buyExamGoods(int userId, List<Integer> goodsIdList) {
		User user = userManager.getUser(userId);
		if (null == goodsIdList || goodsIdList.isEmpty())
			throw new ServiceException("");

		CompanyVO company = companyManager.get(user.getCompanyId());
		int vcount = 0;// 购买所需虚拟币
		int balance = 0;// 本次购买完成余额
		List<Integer> examIdList = Lists.newArrayList();
		for (int goodsId : goodsIdList) {
			Goods goods = goodsMapper.getGoods(goodsId);
			vcount += goods.getPrice();
			examIdList.add(goods.getValue());
		}
		balance = company.getBalance() - vcount;
		// 余额不足
		if (company.getBalance() < vcount) {
			throw new ServiceException("账户余额不足");
		}

		for (int examId : examIdList) {
			Map<String, Object> parameter = Maps.newHashMap();
			parameter.put("companyId", company.getCompanyId());
			parameter.put("examId", examId);
			examMapper.savePaidExam(parameter);
		}

		// 虚拟币消费记录
		Consume consume = new Consume();
		consume.setConsumeTypeId(1);
		consume.setCompanyId(user.getCompanyId());
		consume.setUserId(userId);
		consume.setToUserId(0);
		consume.setVcount(vcount);
		consume.setBalance(balance);
		consume.setStatus(1);

		Map<String, Object> parameter = Maps.newHashMap();
		parameter.put("companyId", company.getCompanyId());
		parameter.put("vcount", 0 - vcount);// 消耗虚拟币

		goodsMapper.saveConsume(consume);
		companyMapper.updateOthers(parameter);

		return consume;
	}

	@Override
	public Object buyPayGoods(int userId, int rechargeType, int goodsId, int count) {
		Goods goods = goodsMapper.getGoods(goodsId);

		Order order = new Order();
		order.setCount(count);
		order.setGoodsId(goodsId);
		order.setGoodsName(goods.getName());
		order.setPrice(goods.getPrice());
		order.setTotal(order.getPrice() * order.getCount());
		if (1 == rechargeType) {// 个人充值
			order.setCompanyId(0);
			order.setUserId(userId);
		} else {// 企业充值
			User user = userManager.getUser(userId);
			order.setCompanyId(user.getCompanyId());
			order.setUserId(0);
		}

		if (goodsMapper.saveOrder(order) > 0)
			return order;
		return null;
	}

	@Override
	public GoodsVO getGiftGoods(int goodsId) {
		return null;
	}

	private boolean hasChild(List<Integer> parent, int child) {
		boolean hasChild = false;
		for (int node : parent) {
			if (node == child) {
				hasChild = true;
				break;
			}
		}
		return hasChild;
	}

	@Override
	public List<Goods> selectBizGoods() {
		return goodsMapper.selectBizGoods();
	}

	@Override
	public List<Goods> selectGoodsByCategoryId(int categoryId) {
		return goodsMapper.selectGoodsByCategoryId(categoryId);
	}

	@Override
	public List<Goods> selectGoodsByCategoryId(int categoryId, int userId) {
		User user = userManager.getUser(userId);
		List<Goods> goodsList = goodsMapper.selectGoodsByCategoryId(categoryId);
		if (0 != user.getCompanyId()) {
			Map<String, Object> parameter = Maps.newHashMap();
			parameter.put("companyId", user.getCompanyId());
			List<Integer> parent = examMapper.selectPaidExamId(parameter);
			for (Goods goods : goodsList) {
				if (hasChild(parent, goods.getValue())) {
					goods.setStatus(2);// 设为已购买
				}
			}
		}
		return goodsList;
	}

	@Override
	public List<Goods> selectPayGoods() {
		return goodsMapper.selectGoodsByCategoryId(Goods.CATEGORY.PAY_GOODS);
	}

	@Override
	public boolean updateOrder(String out_trade_no, String trade_no, String nodify_data) {
		boolean isSuccess = false;

		Order order = goodsMapper.getOrder(Long.parseLong(out_trade_no));
		if (null != order) {
			if (1 != order.getStatus() && 2 != order.getStatus()) {
				order.setNotifyData(nodify_data);
				order.setPayWay("ALIPAY");
				order.setStatus(1);

				// 更新数据成功，执行充值操作
				if (goodsMapper.updateOrder(order) > 0) {
					Goods goods = goodsMapper.getGoods(order.getGoodsId());
					if (0 != order.getCompanyId()) {// 给公司账户充值
						Map<String, Object> parameter = Maps.newHashMap();
						parameter.put("vcount", goods.getValue());
						parameter.put("companyId", order.getCompanyId());
						isSuccess = companyMapper.updateOthers(parameter) > 0;
					} else if (0 != order.getUserId()) {// 给个人账户充值
						Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(order.getUserId());
						UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class).inc("money", goods.getValue())
								.inc("moneyTotal", goods.getValue());
						isSuccess = 1 == dsForRW.update(q, ops).getUpdatedCount();
					}
				}
			} else {
				isSuccess = true;
			}
		}

		return isSuccess;
	}
}
