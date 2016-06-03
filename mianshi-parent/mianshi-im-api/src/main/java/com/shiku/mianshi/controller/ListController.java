package com.shiku.mianshi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.JobManager;
import cn.xyz.repository.MsgListRepository;

@RestController
public class ListController {

	@Autowired
	private MsgListRepository msgListRepository;

	@Autowired
	private JobManager jobRepository;

	@RequestMapping(value = "/b/circle/msg/hot")
	public JSONMessage getHostMsgList(@RequestParam(defaultValue = "0") int cityId, @RequestParam(defaultValue = "0") Integer pageIndex) {
		Object data = msgListRepository.getHotList(cityId, pageIndex, 0);
		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/b/circle/msg/latest")
	public JSONMessage getLatestMsgList(@RequestParam(defaultValue = "0") int cityId, @RequestParam(defaultValue = "0") Integer pageIndex) {
		Object data = msgListRepository.getLatestList(cityId, pageIndex, 0);
		return JSONMessage.success(null, data);
	}

}
