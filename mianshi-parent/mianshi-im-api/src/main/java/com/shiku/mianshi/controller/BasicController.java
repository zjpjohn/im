package com.shiku.mianshi.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.AdminManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.service.KSMSServiceImpl;
import cn.xyz.service.KXMPPServiceImpl;

import com.mongodb.BasicDBObject;

@RestController
public class BasicController {

	@Autowired
	private AdminManager adminManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private KSMSServiceImpl pushManager;

	@RequestMapping(value = "/config")
	public JSONMessage getConfig() {
		return JSONMessage.success(null, adminManager.getConfig());
	}

	@RequestMapping(value = "/config/set", method = RequestMethod.GET)
	public ModelAndView setConfig() {
		adminManager.initConfig();
		try {
			KXMPPServiceImpl.getInstance().register("10005", "10005");
			KXMPPServiceImpl.getInstance().register("10000", "10000");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ModelAndView mav = new ModelAndView("config_set");
		mav.addObject("config", adminManager.getConfig());
		return mav;
	}

	@RequestMapping(value = "/config/set", method = RequestMethod.POST)
	public void setConfig(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BasicDBObject dbObj = new BasicDBObject();
		for (String key : request.getParameterMap().keySet()) {
			dbObj.put(key, request.getParameter(key));
		}
		adminManager.setConfig(dbObj);
		response.sendRedirect("/config/set");
	}

	@RequestMapping(value = "/areas")
	public JSONMessage getAreaList(@RequestParam int parentId) {
		return JSONMessage.success(null, adminManager.getAreaList(parentId));
	}

	@RequestMapping(value = "/user/debug")
	public JSONMessage getUser(@RequestParam int userId) {
		return JSONMessage.success(null, userManager.getUser(userId));
	}

	@RequestMapping("/basic/randcode/sendSms")
	public JSONMessage sendSms(@RequestParam String telephone) {
		return pushManager.applyVerify(telephone);
	}

	@RequestMapping(value = "/verify/telephone")
	public JSONMessage virifyTelephone(
			@RequestParam(value = "telephone", required = true) String telephone) {
		return userManager.isRegister(telephone) ? JSONMessage
				.failure("手机号已注册") : JSONMessage.success("手机号未注册");
	}

}
