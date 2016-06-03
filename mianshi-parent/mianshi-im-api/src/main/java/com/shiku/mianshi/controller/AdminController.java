package com.shiku.mianshi.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.ESBUtil;
import cn.xyz.commons.utils.HttpURLUtil;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.example.UserExample;
import cn.xyz.mianshi.service.FriendsManager;
import cn.xyz.mianshi.service.RoomManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.PageVO;
import cn.xyz.mianshi.vo.Room;
import cn.xyz.mianshi.vo.User;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.shiku.mianshi.KAdminProperties;
import com.shiku.mianshi.crmProperties;
/**
 * 后台管理
 * 
 * Author: chais
 *
 */
@Controller
@RequestMapping("/console")
public class AdminController {
	public static final String LOGIN_USER_KEY = "LOGIN_USER";

	@Resource(name = "dsForTigase")
	private Datastore dsForTigase;
	@Resource(name = "dsForRW")
	private Datastore dsForRW;
	@Autowired
	private UserManager userManager;
	@Autowired
	private FriendsManager friendsManager;
	@Resource(name = RoomManager.BEAN_ID)
	private RoomManager roomManager;
	@Autowired
	private KAdminProperties props;
	@Autowired
	private crmProperties crmProps;
	
	private Map<String, String> adminMap;

	private boolean isAdmin(String username) {
		return adminMap.containsKey(username);
	}

	private boolean login(String username, String password) {
		return adminMap.get(username).equals(password);
	}

	@RequestMapping(value = { "", "/" })
	public String index(HttpServletRequest request, HttpServletResponse response) {
		return "index";
	}

	@RequestMapping(value = "/login", method = { RequestMethod.GET })
	public String login() {
		return "login";
	}

	@RequestMapping(value = "/logout", method = { RequestMethod.GET })
	public void logout(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.getSession().removeAttribute(LOGIN_USER_KEY);
		response.sendRedirect("/console/login");
	}

	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	public String login(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String telephone = request.getParameter("telephone");
		String password = request.getParameter("password");

		if (null == adminMap) {
			adminMap = new HashMap<String, String>();
			String[] users = props.getUsers().split(",");
			for (String t : users) {
				String[] user = t.split(":");
				adminMap.put(user[0], user[1]);
			}
		}
		if (isAdmin(telephone)) {
			if (login(telephone, password)) {
				request.getSession().setAttribute(LOGIN_USER_KEY, telephone);
				response.sendRedirect("/console");
				return null;
			}
			request.setAttribute("tips", "帐号或密码错误！");
			return "login";
		} else {
			try {
				User user = userManager.login(telephone, password);
				request.getSession().setAttribute(LOGIN_USER_KEY, user);
				response.sendRedirect("/console");
				return null;
			} catch (Exception e) {

			}
			request.setAttribute("tips", "帐号或密码错误！");
			return "login";
		}
	}

	@RequestMapping(value = "/chat_logs", method = { RequestMethod.GET })
	public String chat_logs(@RequestParam(defaultValue = "0") long startTime,
			@RequestParam(defaultValue = "0") long endTime,
			@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize,
			HttpServletRequest request) {
		// User user = getUser();

		DBCollection dbCollection = dsForTigase.getDB().getCollection(
				"shiku_msgs");
		BasicDBObject q = new BasicDBObject();
		// q.put("sender", user.getUserId());
		if (0 != startTime)
			q.put("ts", new BasicDBObject("$gte", startTime));
		if (0 != endTime)
			q.put("ts", new BasicDBObject("$lte", endTime));

		long total = dbCollection.count(q);
		java.util.List<DBObject> pageData = Lists.newArrayList();

		DBCursor cursor = dbCollection.find(q).skip(pageIndex * pageSize)
				.limit(pageSize);
		while (cursor.hasNext()) {
			BasicDBObject dbObj = (BasicDBObject) cursor.next();
			if (1 == dbObj.getInt("direction")) {
				int sender = dbObj.getInt("receiver");
				dbObj.put("receiver_nickname", userManager.getUser(sender)
						.getNickname());
			}
			pageData.add(dbObj);
		}
		request.setAttribute("page", new PageVO(pageData, total, pageIndex,
				pageSize));
		return "chat_logs";
	}

	@RequestMapping(value = "/chat_logs_all", method = { RequestMethod.GET })
	public String chat_logs_all(
			@RequestParam(defaultValue = "0") long startTime,
			@RequestParam(defaultValue = "0") long endTime,
			@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "50") int pageSize,
			HttpServletRequest request) {
		// User user = getUser();

		DBCollection dbCollection = dsForTigase.getDB().getCollection(
				"shiku_msgs");
		BasicDBObject q = new BasicDBObject();
		// q.put("sender", user.getUserId());
		q.put("sender", new BasicDBObject("$ne", 10005));
		q.put("receiver", new BasicDBObject("$ne", 10005));
		if (0 != startTime)
			q.put("ts", new BasicDBObject("$gte", startTime));
		if (0 != endTime)
			q.put("ts", new BasicDBObject("$lte", endTime));

		long total = dbCollection.count(q);
		java.util.List<DBObject> pageData = Lists.newArrayList();

		DBCursor cursor = dbCollection.find(q)
				.sort(new BasicDBObject("_id", -1)).skip(pageIndex * pageSize)
				.limit(pageSize);
		while (cursor.hasNext()) {
			BasicDBObject dbObj = (BasicDBObject) cursor.next();
			int sender = dbObj.getInt("sender");
			int receiver = dbObj.getInt("receiver");
			try {
				dbObj.put("sender_nickname", userManager.getUser(sender)
						.getNickname());
			} catch (Exception e) {
				dbObj.put("sender_nickname", "未知");
			}
			try {
				dbObj.put("receiver_nickname", userManager.getUser(receiver)
						.getNickname());
			} catch (Exception e) {
				dbObj.put("receiver_nickname", "未知");
			}
			pageData.add(dbObj);
		}
		request.setAttribute("page", new PageVO(pageData, total, pageIndex,
				pageSize));
		return "chat_logs_all";
	}

	@RequestMapping(value = "/groupchat_logs", method = { RequestMethod.GET })
	public ModelAndView groupchat_logs(
			@RequestParam(defaultValue = "") String room_jid_id,
			@RequestParam(defaultValue = "0") long startTime,
			@RequestParam(defaultValue = "0") long endTime,
			@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize,
			HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("groupchat_logs");
		Object historyList = roomManager.selectHistoryList(getUser()
				.getUserId(), 0,null, pageIndex, pageSize);
		if (!StringUtil.isEmpty(room_jid_id)) {
			DBCollection dbCollection = dsForTigase.getDB().getCollection(
					"shiku_muc_msgs");

			BasicDBObject q = new BasicDBObject();
			// q.put("room_jid_id", room_jid_id);
			if (0 != startTime)
				q.put("ts", new BasicDBObject("$gte", startTime));
			if (0 != endTime)
				q.put("ts", new BasicDBObject("$lte", endTime));
			long total = dbCollection.count(q);
			java.util.List<DBObject> pageData = Lists.newArrayList();

			DBCursor cursor = dbCollection.find(q).skip(pageIndex * pageSize)
					.limit(pageSize);
			while (cursor.hasNext()) {
				pageData.add(cursor.next());
			}
			mav.addObject("page", new PageVO(pageData, total, pageIndex,
					pageSize));
		}
		mav.addObject("historyList", historyList);
		return mav;
	}

	@RequestMapping(value = "/groupchat_logs_all", method = { RequestMethod.GET })
	public ModelAndView groupchat_logs_all(
			@RequestParam(defaultValue = "0") long startTime,
			@RequestParam(defaultValue = "0") long endTime,
			@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "50") int pageSize,
			HttpServletRequest request) {

		DBCollection dbCollection = dsForTigase.getDB().getCollection(
				"shiku_muc_msgs");

		BasicDBObject q = new BasicDBObject();
		// q.put("room_jid_id", room_jid_id);
		if (0 != startTime)
			q.put("ts", new BasicDBObject("$gte", startTime));
		if (0 != endTime)
			q.put("ts", new BasicDBObject("$lte", endTime));
		long total = dbCollection.count(q);
		java.util.List<DBObject> pageData = Lists.newArrayList();
		DBCursor cursor = dbCollection.find(q)
				.sort(new BasicDBObject("ts", -1)).skip(pageIndex * pageSize)
				.limit(pageSize);
		while (cursor.hasNext()) {
			pageData.add(cursor.next());
		}

		ModelAndView mav = new ModelAndView("groupchat_logs_all");
		mav.addObject("page", new PageVO(pageData, total, pageIndex, pageSize));
		return mav;
	}

	@RequestMapping(value = "/userList", method = { RequestMethod.GET })
	public ModelAndView userList(
			@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "50") int pageSize) {
		ModelAndView mav = new ModelAndView("userList");

		DBCollection dbCollection = dsForRW.getCollection(User.class);
		long total = dbCollection.count();
		List<DBObject> pageData = Lists.newArrayList();
		DBCursor cursor = dbCollection.find()
				.sort(new BasicDBObject("_id", -1)).skip(pageIndex * pageSize)
				.limit(pageSize);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			obj.put("userId", obj.get("_id"));
			obj.removeField("_id");

			pageData.add(obj);
		}
		PageVO page = new PageVO(pageData, total, pageIndex, pageSize);
		mav.addObject("page", page);

		return mav;
	}

	@RequestMapping(value = "/deleteUser", method = { RequestMethod.GET })
	public void deleteUser(@RequestParam int userId,
			@RequestParam int pageIndex, HttpServletResponse response) {
		try {
			DBCollection dbCollection = dsForRW.getCollection(User.class);
			dbCollection.remove(new BasicDBObject("_id", userId));

			response.sendRedirect("/console/userList?pageIndex=" + pageIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/deleteRoom", method = { RequestMethod.GET })
	public void deleteRoom(@RequestParam String roomId,
			@RequestParam int pageIndex, HttpServletResponse response) {
		try {
			DBCollection dbCollection = dsForTigase.getCollection(Room.class);
			dbCollection.remove(new BasicDBObject("_id", new ObjectId(roomId)));

			response.sendRedirect("/console/roomList?pageIndex=" + pageIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/roomList", method = { RequestMethod.GET })
	public ModelAndView roomList(
			@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "50") int pageSize) {
		ModelAndView mav = new ModelAndView("roomList");

		DBCollection dbCollection = dsForTigase.getCollection(Room.class);
		long total = dbCollection.count();
		List<DBObject> pageData = Lists.newArrayList();
		DBCursor cursor = dbCollection.find()
				.sort(new BasicDBObject("_id", -1)).skip(pageIndex * pageSize)
				.limit(pageSize);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			pageData.add(obj);
		}
		mav.addObject("page", new PageVO(pageData, total, pageIndex, pageSize));

		return mav;
	}

	@RequestMapping(value = "/pushToAll")
	public void pushToAll(HttpServletResponse response,
			@RequestParam int fromUserId, @RequestParam String body) {
		try {
			body = new String(body.getBytes("ISO-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		MessageBean mb = new MessageBean();
		mb.setContent(body);
		mb.setFromUserId("10000");
		mb.setFromUserName("系统消息");
		mb.setType(1);
		mb.setTimeSend(DateUtil.currentTimeSeconds());

		DBCursor cursor = dsForRW.getDB().getCollection("user")
				.find(null, new BasicDBObject("_id", 1))
				.sort(new BasicDBObject("_id", -1));
		while (cursor.hasNext()) {
			BasicDBObject dbObj = (BasicDBObject) cursor.next();
			int userId = dbObj.getInt("_id");
			try {
				if (10005 == fromUserId)
					KXMPPServiceImpl.getInstance().send(userId,
							JSON.toJSONString(mb));
				else if (10000 == fromUserId)
					KXMPPServiceImpl.getInstance().send("10000", "10000",
							userId, JSON.toJSONString(mb));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.write("<script type='text/javascript'>alert('\u6279\u91CF\u53D1\u9001\u6D88\u606F\u5DF2\u5B8C\u6210\uFF01');window.location.href='/pages/qf.jsp';</script>");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 跳转系统消息页面
	 * Author: chais
	 */
	@RequestMapping(value = "/xtxx", method = { RequestMethod.GET })
	public String xtxx(){
		return "xtxx";
	}
	
	/**
	 * 跳转管理页面
	 * Author: chais
	 */
	@RequestMapping(value = "/manage", method = { RequestMethod.GET })
	public String manage(){
		return "manage";
	}
	
	/**
	 * 根据选中的小区发布系统消息
	 * Author: chais
	 */
	@RequestMapping(value = "/pushToArea")
	public void pushToArea(HttpServletResponse response,
			@RequestParam String communitycodes, @RequestParam String body) {
		try {
			body = new String(body.getBytes("ISO-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		MessageBean mb = new MessageBean();
		mb.setContent(body);
		mb.setFromUserId("10000");
		mb.setFromUserName("系统消息");
		mb.setType(1);
		mb.setTimeSend(DateUtil.currentTimeSeconds());

		Set<Integer> set = new TreeSet<Integer>();
		communitycodes=communitycodes.substring(0,communitycodes.length()-1);
		String[] communitycodeArray=communitycodes.split(",");
		for (int i = 0; i < communitycodeArray.length; i++) {
			//模糊查询符号小区编码的用户
			DBCollection dbCollection = dsForRW.getDB().getCollection("user");
			BasicDBObject q = new BasicDBObject();
			String params = communitycodeArray[i];
			Pattern pattern = Pattern.compile(params, Pattern.CASE_INSENSITIVE);
			q.put("comunityCode", pattern);
			DBCursor cursor = dbCollection.find(q).sort(new BasicDBObject("_id", -1));
			//获得所有用户id并去重（解决一个用户多个小区造成的重复发送问题）
			while (cursor.hasNext()) {
				BasicDBObject dbObj = (BasicDBObject) cursor.next();
				int userId = dbObj.getInt("_id");
				set.add(userId);
			}
		}
		//发送消息
		for (int userIds:set) {
			try {
				KXMPPServiceImpl.getInstance().send("10000", "10000",userIds, JSON.toJSONString(mb));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.write("<script type='text/javascript'>alert('\u6279\u91CF\u53D1\u9001\u6D88\u606F\u5DF2\u5B8C\u6210\uFF01');window.location.href='/console/xtxx';</script>");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public User getUser() {
		Object obj = RequestContextHolder.getRequestAttributes().getAttribute(
				LOGIN_USER_KEY, RequestAttributes.SCOPE_SESSION);
		return null == obj ? null : (User) obj;
	}
	
	/**
	 * 修改所有用户的小区编码集
	 * Author: chais
	 */
	@RequestMapping(value = "/updateUserCommunitycodeStrs")
	public void updateUserCommunitycodeStrs(HttpServletResponse response,
			@RequestParam String uccType, @RequestParam String tel) {
		if(uccType.equals("all")){
			DBCollection dbCollection = dsForRW.getDB().getCollection("user");
			DBCursor cursor = dbCollection.find().sort(new BasicDBObject("_id", -1));
			while (cursor.hasNext()) {
				BasicDBObject dbObj = (BasicDBObject) cursor.next();
				int userId = dbObj.getInt("_id");
				String telephone = dbObj.getString("telephone");
				String userCommunitycodeStrs = getUserCommunitycodeStrs(telephone);
				if(!userCommunitycodeStrs.equals("")){
					Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
					UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
					ops.set("comunityCode", userCommunitycodeStrs);
					dsForRW.update(q, ops);
				}
			}
		}else if(uccType.equals("one")&&tel!=null){
			String userCommunitycodeStrs = getUserCommunitycodeStrs(tel);
			if(!userCommunitycodeStrs.equals("")){
				Query<User> q = dsForRW.createQuery(User.class).field("telephone").equal(tel);
				UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
				ops.set("comunityCode", userCommunitycodeStrs);
				dsForRW.update(q, ops);
			}
		}
		
		try {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.write("<script type='text/javascript'>alert('\u4FEE\u6539\u6210\u529F');window.location.href='/console/manage';</script>");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据手机号获得用户入住的小区集
	 * Author: chais
	 */
	public String getUserCommunitycodeStrs(String telephone) {
        Map<String,String> hashmap = new HashMap<String,String>();
        hashmap.put("telephone", telephone);
        String crm_url="http://"+crmProps.getHost()+":"+crmProps.getPort()+"/c3-crm-serv/member/esb/checkMember/retrieveComunityCodesByTel";
        // 发送报文,返回结果报文
        String result = HttpURLUtil.doPOST(crm_url, hashmap);
        JSONObject jsonObj = JSONObject.parseObject(result);
        String resultsc = jsonObj.getString("result");
        String ComunityCodes = "";
        if(resultsc.equals("1")){
        	ComunityCodes = jsonObj.getString("ComunityCodes");
        }
       return ComunityCodes;
	}
	
	/**
	 * 修改所有群聊房间的最大成员数限制
	 * Author: chais
	 */
	@RequestMapping(value = "/updateRoomUserSize")
	public void updateRoomUserSize(HttpServletResponse response,
			@RequestParam String rusType, @RequestParam String roomName,
			@RequestParam String utel,@RequestParam String maxUserSize) {
		if(rusType.equals("all")){
			DBCollection dbCollection = dsForTigase.getCollection(Room.class);
			BasicDBObject dbq = new BasicDBObject();
			//dbq.put("maxUserSize", new BasicDBObject("$lt", 100));
			DBCursor cursor = dbCollection.find(dbq).sort(new BasicDBObject("_id", -1));
			while (cursor.hasNext()) {
				BasicDBObject dbObj = (BasicDBObject) cursor.next();
				String roomId = dbObj.getString("jid");
	
				Query<Room> qu = dsForTigase.createQuery(Room.class).field("jid").equal(roomId);
				UpdateOperations<Room> cops = dsForTigase.createUpdateOperations(Room.class);
				cops.set("maxUserSize", maxUserSize);
				dsForTigase.update(qu, cops);
			}
		}else if(rusType.equals("one")){
			Query<User> q = dsForRW.createQuery(User.class).field("telephone").equal(utel);
			int userId=q.get().getUserId();
			Query<Room> qu = dsForTigase.createQuery(Room.class).field("name").equal(roomName).field("userId").equal(userId);
			UpdateOperations<Room> cops = dsForTigase.createUpdateOperations(Room.class);
			cops.set("maxUserSize", maxUserSize);
			dsForTigase.update(qu, cops);
		}
		
		try {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.write("<script type='text/javascript'>alert('\u4FEE\u6539\u6210\u529F');window.location.href='/console/manage';</script>");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
