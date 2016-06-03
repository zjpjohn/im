package com.shiku.mianshi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.FriendsManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Fans;
import cn.xyz.mianshi.vo.Friends;

@RestController
@RequestMapping("/friends")
public class FriendsController {

	@Autowired
	private FriendsManager friendsManager;
	@RequestMapping("/attention/add")
	public JSONMessage addAtt(@RequestParam Integer toUserId) {
		return friendsManager.followUser(ReqUtil.getUserId(), toUserId);
	}

	@RequestMapping("/blacklist/add")
	public JSONMessage addBlacklist(@RequestParam Integer toUserId) {
		friendsManager.addBlacklist(ReqUtil.getUserId(), toUserId);
		return JSONMessage.success(null);
	}

	@RequestMapping("/add")
	public JSONMessage addFriends(
			@RequestParam(value = "toUserId") Integer toUserId) {
		friendsManager.addFriends(ReqUtil.getUserId(), toUserId);

		return JSONMessage.success("加好友成功");
	}

	@RequestMapping("/blacklist/delete")
	public JSONMessage deleteBlacklist(@RequestParam Integer toUserId) {
		Object data = friendsManager.deleteBlacklist(ReqUtil.getUserId(),
				toUserId);
		return JSONMessage.success("取消拉黑成功", data);
	}

	@RequestMapping("/attention/delete")
	public JSONMessage deleteFollow(
			@RequestParam(value = "toUserId") Integer toUserId) {
		friendsManager.unfollowUser(ReqUtil.getUserId(), toUserId);
		return JSONMessage.success("取消关注成功");
	}

	@RequestMapping("/delete")
	public JSONMessage deleteFriends(@RequestParam Integer toUserId) {
		friendsManager.deleteFriends(ReqUtil.getUserId(), toUserId);
		return JSONMessage.success("删除好友成功");
	}

	@RequestMapping("/remark")
	public JSONMessage friendsRemark(@RequestParam int toUserId,
			@RequestParam String remarkName) {
		friendsManager.updateRemark(ReqUtil.getUserId(), toUserId,
				null == remarkName ? "" : remarkName);

		return JSONMessage.success(null);
	}

	@RequestMapping("/blacklist")
	public JSONMessage queryBlacklist() {
		List<Friends> data = friendsManager.queryBlacklist(ReqUtil.getUserId());

		return JSONMessage.success(null, data);
	}

	@RequestMapping("/fans/list")
	public JSONMessage queryFans(@RequestParam(defaultValue = "") Integer userId) {
		userId = (null == userId ? ReqUtil.getUserId() : userId);
		List<Fans> data = friendsManager.queryFans(userId);

		return JSONMessage.success(null, data);
	}

	@RequestMapping("/attention/list")
	public JSONMessage queryFollow(
			@RequestParam(defaultValue = "") Integer userId) {
		userId = (null == userId ? ReqUtil.getUserId() : userId);
		List<Friends> data = friendsManager.queryFollow(userId);

		return JSONMessage.success(null, data);
	}

	@RequestMapping("/list")
	public JSONMessage queryFriends(@RequestParam Integer userId) {
		userId = (null == userId ? ReqUtil.getUserId() : userId);
		List<Friends> data = friendsManager.queryFriends(userId);

		return JSONMessage.success(null, data);
	}

}
