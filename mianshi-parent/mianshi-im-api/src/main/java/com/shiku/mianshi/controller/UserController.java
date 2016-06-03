package com.shiku.mianshi.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import cn.xyz.commons.constants.KConstants.ResultCode;
import cn.xyz.commons.support.jedis.JedisCallbackVoid;
import cn.xyz.commons.support.jedis.JedisTemplate;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.example.BindExample;
import cn.xyz.mianshi.example.UserExample;
import cn.xyz.mianshi.example.UserQueryExample;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.UserRepository;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController extends AbstractController {

	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	@Autowired
	private UserManager userManager;
	@Autowired
	private UserRepository userRepository;

	@RequestMapping(value = "/bind")
	public JSONMessage bind(@ModelAttribute BindExample example) {
		int userId = ReqUtil.getUserId();
		if ("duapp".equals(example.getPlatformId())) {
			jedisTemplate.execute(new JedisCallbackVoid() {

				@Override
				public void execute(Jedis jedis) {
					String key = String.format("duapp:channelId:%1$s",
							example.getChannelId());
					String value = jedis.get(key);

					Pipeline pipeline = jedis.pipelined();

					// 通道已被绑定
					if (null != value)
						// 删除用户绑定的通道
						pipeline.del(String.format("duapp:userId:%1$s", value));
					pipeline.set(key, String.valueOf(userId));
					pipeline.set(String.format("duapp:userId:%1$s", userId),
							example.getChannelId());
					pipeline.sync();
				}
			});
			return JSONMessage.success();
		}
		throw new RuntimeException();
	}

	@RequestMapping("/recommend")
	public JSONMessage getRecommendList(@RequestParam String text) {
		List<String> list = JSON.parseArray(text, String.class);
		List<User> data = userRepository.findByTelephone(list);

		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/get")
	public JSONMessage getUser(@RequestParam(defaultValue = "0") int userId) {
		int loginedUserId = ReqUtil.getUserId();
		int toUserId = 0 == userId ? loginedUserId : userId;
		Object data = userManager.getUser(loginedUserId, toUserId);
		return JSONMessage.success(null, data);
	}
	
	@RequestMapping(value = "/getbytel")
	public JSONMessage getUserByTel(@RequestParam String tel) {
		int loginedUserId = ReqUtil.getUserId();
		User user = (User)userManager.getUser(tel);
		if(user==null){
			return JSONMessage.failure("用户信息不存在");
		}else{
			int toUserId = user.getUserId();
			Object data = userManager.getUser(loginedUserId, toUserId);
			return JSONMessage.success(null, data);
		}
	}

	@RequestMapping(value = "/login")
	public JSONMessage login(@ModelAttribute UserExample example) {
		Object data = userManager.login(example);
		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/login/auto")
	public JSONMessage loginAuto(@RequestParam String access_token,
			@RequestParam int userId, @RequestParam String serial) {
		Object data = userManager.loginAuto(access_token, userId, serial);
		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/logout")
	public JSONMessage logout(@RequestParam String access_token,
			@RequestParam String telephone) {
		userManager.logout(telephone, access_token);
		return JSONMessage.success(null);
	}

	@RequestMapping(value = "/query")
	public JSONMessage queryUser(@ModelAttribute UserQueryExample param) {
		Object data = userManager.query(param);
		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/register")
	public JSONMessage register(@Valid UserExample example) {
		Object data = userManager.registerIMUser(example);
		return JSONMessage.success(null, data);
	}

	@RequestMapping("/update")
	public JSONMessage updateUser(@ModelAttribute UserExample param) {
		User data = userManager.updateUser(ReqUtil.getUserId(), param);
		return JSONMessage.success(null, data);
	}
	
	@RequestMapping("/updatebytel")
	public JSONMessage updateUserByTel(@ModelAttribute UserExample param) {
		User user = (User)userManager.getUser(param.getTelephone());
		if(user==null){
			return JSONMessage.failure("用户信息不存在");
		}else {
			User data = userManager.updateUser(user.getUserId(), param);
			return JSONMessage.success(null, data);
		}
	}
	
	@RequestMapping(value = "/remove")
	public JSONMessage removeUser(@ModelAttribute UserExample example) {
		userManager.removeUser(example);
		return JSONMessage.success(null, null);
	}
	
	@RequestMapping("/removecomunitycode")
	public JSONMessage removeComunityCode(@ModelAttribute UserExample param) {
		User user = (User)userManager.getUser(param.getTelephone());
		if(user==null){
			return JSONMessage.failure("用户信息不存在");
		}else {
			User data = userManager.removeComunityCode(user.getUserId(), param);
			return JSONMessage.success(null, null);
		}
	}
}
