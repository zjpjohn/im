package com.shiku.mianshi.controller;

import java.util.List;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.RoomManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Room;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.Room.Member;

import com.alibaba.fastjson.JSON;

@RestController
@RequestMapping("/room")
public class RoomController {

	@Resource(name = RoomManager.BEAN_ID)
	private RoomManager roomManager;
	@Autowired
	private UserManager userManager;

	/**
	 * 新增房间
	 * 
	 * @param room
	 * @param text
	 * @return
	 */
	@RequestMapping("/add")
	public JSONMessage add(@ModelAttribute Room room,
			@RequestParam(defaultValue = "") String text) {
		if(room.getComunityCode()==null||room.getComunityCode().equals("")){
			return JSONMessage.failure("您尚未入住或小区编码同步失败，无法创建房间");
		}
		int roomCount = roomManager.getRoomCount(room.getComunityCode(),ReqUtil.getUserId());
		if(roomCount>=4){
			return JSONMessage.failure("已经达到最大创建群聊房间数量，无法创建");
		}
		List<Integer> idList = StringUtil.isEmpty(text) ? null : JSON
				.parseArray(text, Integer.class);
		Object data = roomManager.add(userManager.getUser(ReqUtil.getUserId()),
				room, idList);
		return JSONMessage.success(null, data);
	}

	/**
	 * 删除房间
	 * 
	 * @param roomId
	 * @return
	 */
	@RequestMapping("/delete")
	public JSONMessage delete(@RequestParam String roomId) {
		roomManager.delete(userManager.getUser(ReqUtil.getUserId()),
				new ObjectId(roomId));
		return JSONMessage.success();
	}

	/**
	 * 更新房间
	 * 
	 * @param roomId
	 * @param roomName
	 * @param notice
	 * @param desc
	 * @return
	 */
	@RequestMapping("/update")
	public JSONMessage update(@RequestParam String roomId,
			@RequestParam(defaultValue = "") String roomName,
			@RequestParam(defaultValue = "") String notice,
			@RequestParam(defaultValue = "") String desc) {
		// if (StringUtil.isEmpty(roomName) && StringUtil.isEmpty(notice)) {
		//
		// } else {
		// User user = userManager.getUser(ReqUtil.getUserId());
		// roomManager.update(user, new ObjectId(roomId), roomName, notice,
		// desc);
		// }
		User user = userManager.getUser(ReqUtil.getUserId());
		roomManager.update(user, new ObjectId(roomId), roomName, notice, desc);

		return JSONMessage.success();
	}

	/**
	 * 根据房间Id获取房间
	 * 
	 * @param roomId
	 * @return
	 */
	@RequestMapping("/get")
	public JSONMessage get(@RequestParam String roomId) {
		Object data = roomManager.get(new ObjectId(roomId));
		return JSONMessage.success(null, data);
	}

	/**
	 * 获取房间列表（按创建时间排序）
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/list")
	public JSONMessage list(@ModelAttribute Room room,@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize) {
		if(room.getComunityCode()==null||room.getComunityCode().equals("")){
			return JSONMessage.failure("您尚未入住或小区编码同步失败，不能查看房间");
		}
		Object data = roomManager.selectList(room,pageIndex, pageSize);
		return JSONMessage.success(null, data);
	}

	@RequestMapping("/member/update")
	public JSONMessage updateMember(@RequestParam String roomId,
			@ModelAttribute Member member,
			@RequestParam(defaultValue = "") String text) {
		List<Integer> idList = StringUtil.isEmpty(text) ? null : JSON
				.parseArray(text, Integer.class);
		User user = userManager.getUser(ReqUtil.getUserId());
		if (null == idList)
			roomManager.updateMember(user, new ObjectId(roomId), member);
		else
			roomManager.updateMember(user, new ObjectId(roomId), idList);

		return JSONMessage.success();
	}

	@RequestMapping("/member/delete")
	public JSONMessage deleteMember(@RequestParam String roomId,
			@RequestParam int userId) {
		User user = userManager.getUser(ReqUtil.getUserId());
		roomManager.deleteMember(user, new ObjectId(roomId), userId);
		return JSONMessage.success();
	}

	@RequestMapping("/member/get")
	public JSONMessage getMember(@RequestParam String roomId,
			@RequestParam int userId) {
		Object data = roomManager.getMember(new ObjectId(roomId), userId);
		return JSONMessage.success(null, data);
	}

	@RequestMapping("/member/list")
	public JSONMessage getMemberList(@RequestParam String roomId) {
		Object data = roomManager.getMemberList(new ObjectId(roomId));
		return JSONMessage.success(null, data);
	}

	@RequestMapping("/join")
	public JSONMessage join(@RequestParam String roomId,
			@RequestParam(defaultValue = "2") int type) {
		roomManager.join(ReqUtil.getUserId(), new ObjectId(roomId), type);
		return JSONMessage.success();
	}

	@RequestMapping("/leave")
	public JSONMessage leave(@RequestParam String roomId) {
		roomManager.leave(ReqUtil.getUserId(), new ObjectId(roomId));
		return JSONMessage.success();
	}

	@RequestMapping("/list/his")
	public JSONMessage historyList(@ModelAttribute Room room,@RequestParam(defaultValue = "0") int type,
			@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize) {
		// Object data = roomManager.selectHistoryList(ReqUtil.getUserId(),
		// type);
		Object data = roomManager.selectHistoryList(ReqUtil.getUserId(),type,room,
				pageIndex, pageSize);
		return JSONMessage.success(null, data);
	}

}
