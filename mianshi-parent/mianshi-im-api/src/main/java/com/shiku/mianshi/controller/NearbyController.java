package com.shiku.mianshi.controller;

import java.util.Calendar;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.FriendsManager;
import cn.xyz.mianshi.vo.User;

import com.shiku.lbs.vo.NearbyUser;

@RestController
@RequestMapping("/nearby")
public class NearbyController {

	@Resource(name = "dsForRW")
	private Datastore dsForRW;
	@Autowired
	private FriendsManager friendsManager;

	// @Autowired
	// private NearbyManager nearbyManager;
	//
	// @RequestMapping(value = "/user")
	// public JSONMessage getUserList(@ModelAttribute NearbyUser poi) {
	// JSONMessage jMessage;
	// try {
	// Object data = nearbyManager.getIMUserList(poi);
	// jMessage = JSONMessage.success(null, data);
	// } catch (Exception e) {
	// jMessage = JSONMessage.error(e);
	// }
	// return jMessage;
	// }

	@RequestMapping(value = "/user")
	public JSONMessage nearbyUser(@ModelAttribute NearbyUser poi) {
		JSONMessage jMessage = null;
		try {
			// List<Integer> userIdList =
			// friendsManager.getFriendsIdList(ReqUtil
			// .getUserId());
			Query<User> q = dsForRW.createQuery(User.class);
			// if (null != userIdList && !userIdList.isEmpty())
			// q.filter("userId nin", userIdList);
			q.field("loc").near(poi.getLongitude(), poi.getLatitude(), 1000);
			if (!StringUtil.isEmpty(poi.getNickname())) {
				q.field("nickname").contains(poi.getNickname());
			}
			if (null != poi.getSex()) {
				q.field("sex").equal(poi.getSex());
			}
			if (null != poi.getActive()) {
				q.field("active").greaterThanOrEq(
						DateUtil.currentTimeSeconds() - poi.getActive());
				q.field("active").lessThanOrEq(DateUtil.currentTimeSeconds());
			}
			if (null != poi.getMinAge() && null != poi.getMaxAge()) {
				int year = Calendar.getInstance().get(Calendar.YEAR);
				long start = DateUtil.toSeconds((year - poi.getMaxAge())
						+ "-01-01");
				long end = DateUtil.toSeconds((year - poi.getMinAge())
						+ "-12-31");

				q.field("birthday").greaterThanOrEq(start);
				q.field("birthday").lessThanOrEq(end);
			}
			Object data = q.offset(poi.getPageIndex() * (poi.getPageSize()))
					.limit(poi.getPageSize()).asList();
			jMessage = JSONMessage.success(null, data);
		} catch (Exception e) {
			e.printStackTrace();
			jMessage = JSONMessage.error(e);
		}

		return jMessage;
	}
}
