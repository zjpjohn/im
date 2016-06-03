package com.shiku.mianshi.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@RestController
@RequestMapping("/tigase")
public class TigaseController extends AbstractController {

	@Resource(name = "dsForTigase")
	private Datastore dsForTigase;

	@Autowired
	private KXMPPServiceImpl xmppService;
	
	private static Logger logger = Logger.getLogger(TigaseController.class); 
	
	@RequestMapping(value = "/push")
	public JSONMessage push(@RequestParam String text, @RequestParam String body) {
		List<Integer> userIdList = JSON.parseArray(text, Integer.class);
		try {
			KXMPPServiceImpl.getInstance().send(userIdList, body);
			return JSONMessage.success();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONMessage.failure("推送失败");
		// {userId:%1$s,toUserIdList:%2$s,body:'%3$s'}
	}
	
	/**
	 * 批量发送接口
	 * 
	 * @author chais
	 *
	 */
	/*@RequestMapping(value = "/push")
	public JSONMessage push(@RequestParam String sender,@RequestParam String senderpwd,@RequestParam String text, @RequestParam String body) {
		List<Integer> userIdList = JSON.parseArray(text, Integer.class);
		try {
			if(sender!=null&&senderpwd!=null){
				KXMPPServiceImpl.getInstance().send(sender,senderpwd,userIdList, body);
				logger.debug("---------------发送成功-----------------");
			}else{
				KXMPPServiceImpl.getInstance().send(userIdList, body);
				logger.debug("---------------发送成功-----------------");
			}
			return JSONMessage.success();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONMessage.failure("推送失败");
		// {userId:%1$s,toUserIdList:%2$s,body:'%3$s'}
	}*/
	
	@RequestMapping("/shiku_msgs")
	public JSONMessage getMsgs(@RequestParam int receiver,
			@RequestParam(defaultValue = "0") long startTime,
			@RequestParam(defaultValue = "0") long endTime,
			@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize) {
		int sender = ReqUtil.getUserId();
		DBCollection dbCollection = dsForTigase.getDB().getCollection(
				"shiku_msgs");
		BasicDBObject q = new BasicDBObject();
		q.put("sender", sender);
		q.put("receiver", receiver);
		if (0 != startTime)
			q.put("ts", new BasicDBObject("$gte", startTime));
		if (0 != endTime)
			q.put("ts", new BasicDBObject("$lte", endTime));

		java.util.List<DBObject> list = Lists.newArrayList();

		DBCursor cursor = dbCollection.find(q)
				.sort(new BasicDBObject("_id", -1)).skip(pageIndex * pageSize)
				.limit(pageSize);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}

		return JSONMessage.success("", list);
	}

	@RequestMapping("/shiku_muc_msgs")
	public JSONMessage getMucMsgs(@RequestParam String roomId,
			@RequestParam(defaultValue = "0") long startTime,
			@RequestParam(defaultValue = "0") long endTime,
			@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize) {

		DBCollection dbCollection = dsForTigase.getDB().getCollection(
				"shiku_muc_msgs");
		BasicDBObject q = new BasicDBObject();
		q.put("room_jid_id", roomId);
		if (0 != startTime)
			q.put("ts", new BasicDBObject("$gte", startTime));
		if (0 != endTime)
			q.put("ts", new BasicDBObject("$lte", endTime));

		java.util.List<DBObject> list = Lists.newArrayList();

		DBCursor cursor = dbCollection.find(q)
				.sort(new BasicDBObject("_id", -1)).skip(pageIndex * pageSize)
				.limit(pageSize);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}

		return JSONMessage.success("", list);
	}

}
