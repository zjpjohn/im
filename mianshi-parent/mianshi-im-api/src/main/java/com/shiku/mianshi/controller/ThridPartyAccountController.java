package com.shiku.mianshi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.UserExpandRepository;
import cn.xyz.repository.UserRepository;

@RestController
@RequestMapping("/user")
public class ThridPartyAccountController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserExpandRepository userExpandRepository;

	@RequestMapping(value = "/accounts")
	public JSONMessage accounts(@RequestParam(defaultValue = "0") int userId) {
		Object data = userExpandRepository.findAccounts(0 == userId ? ReqUtil.getUserId() : userId);

		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/accounts/add")
	public JSONMessage add(@ModelAttribute User.ThridPartyAccount account) {
		JSONMessage jMessage;

		if (StringUtil.isEmpty(account.getTpName()) || StringUtil.isEmpty(account.getTpAccount())
				|| StringUtil.isEmpty(account.getTpUserId())) {
			jMessage = KConstants.Result.ParamsAuthFail;
		} else {
			int userId = ReqUtil.getUserId();
			userExpandRepository.addAccount(userId, account);

			jMessage = JSONMessage.success(null);
		}

		return jMessage;
	}
	
	@RequestMapping(value = "/accounts/delete")
	public JSONMessage delete(@RequestParam String tpName) {
		int userId = ReqUtil.getUserId();
		userExpandRepository.deleteAccount(userId, tpName);

		return JSONMessage.success(null);
	}

}
