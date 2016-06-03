package cn.xyz.service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.jedis.JedisCallbackVoid;
import cn.xyz.commons.support.jedis.JedisTemplate;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.HttpUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;

import com.alibaba.fastjson.JSON;

@Component
@ConfigurationProperties(prefix = "open.189")
public class KSMSServiceImpl implements ApplicationContextAware {

	public static class Result {
		private String access_token;
		private Integer expires_in;
		private String idertifier;
		private String res_code;
		private String res_message;

		public String getAccess_token() {
			return access_token;
		}

		public Integer getExpires_in() {
			return expires_in;
		}

		public String getIdertifier() {
			return idertifier;
		}

		public String getRes_code() {
			return res_code;
		}

		public String getRes_message() {
			return res_message;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public void setExpires_in(Integer expires_in) {
			this.expires_in = expires_in;
		}

		public void setIdertifier(String idertifier) {
			this.idertifier = idertifier;
		}

		public void setRes_code(String res_code) {
			this.res_code = res_code;
		}

		public void setRes_message(String res_message) {
			this.res_message = res_message;
		}
	}

	private String app_id = "216461910000035461";
	private String app_secret = "6ee8640fa58fb452c69e265f455cabac";
	private String app_template_id_invite = "91001772";
	private String app_template_id_random = "91001047";
	@Autowired
	private JedisPool jedisPool;
	@Autowired
	private JedisTemplate jedisTemplate;

	private static ApplicationContext context;

	public static KSMSServiceImpl getInstance() {
		return context.getBean(KSMSServiceImpl.class);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		context = applicationContext;
	}

	public boolean isAvailable(String telephone, String randcode) {
		String key = MessageFormat.format("randcode:{0}", telephone);
		String _randcode = jedisTemplate.get(key);
		return randcode.equals(_randcode);
	}

	public JSONMessage applyVerify(String telephone) {
		String key = MessageFormat.format("randcode:{0}", telephone);
		Long ttl = jedisTemplate.ttl(key);
		if (ttl > 0) {
			throw new ServiceException("请不要频繁请求短信验证码，等待" + ttl + "秒后再次请求");
		}
		JSONMessage jMessage;
		try {
			String param1 = StringUtil.randomCode();
			String param2 = "2分钟";

			Map<String, String> params = new HashMap<String, String>();
			params.put("param1", param1);
			params.put("param2", param2);

			Result result = sendSms(app_template_id_random, telephone, params);

			if ("0".equals(result.getRes_code())) {
				Map<String, String> data = new HashMap<String, String>(1);
				data.put("randcode", param1);
				jMessage = JSONMessage.success(null, data);
				jedisTemplate.set(key, param1);
				jedisTemplate.expire(key, 120);
			} else {
				jMessage = JSONMessage.failure(result.getRes_message());
			}
		} catch (Exception e) {
			jMessage = KConstants.Result.InternalException;
		}

		return jMessage;
	}

	public String getApp_id() {
		return app_id;
	}

	public String getApp_secret() {
		return app_secret;
	}

	public String getApp_template_id_invite() {
		return app_template_id_invite;
	}

	public String getApp_template_id_random() {
		return app_template_id_random;
	}

	public String getToken() throws Exception {
		String token = jedisTemplate.get("open.189.access_token");
		if (StringUtil.isNullOrEmpty(token)) {
			Result result = getTokenObj();
			if ("0".equals(result.getRes_code())) {
				jedisTemplate.execute(new JedisCallbackVoid() {

					@Override
					public void execute(Jedis jedis) {
						Pipeline pipe = jedis.pipelined();
						pipe.set("open.189.access_token",
								result.getAccess_token());
						pipe.expire("open.189.access_token",
								result.getExpires_in());
						pipe.sync();
					}

				});
				token = result.getAccess_token();
			}
		}
		return token;
	}

	public Result getTokenObj() throws Exception {
		HttpUtil.Request request = new HttpUtil.Request();
		request.setSpec("https://oauth.api.189.cn/emp/oauth2/v3/access_token");
		request.setMethod(HttpUtil.RequestMethod.POST);
		request.getData().put("grant_type", "client_credentials");
		request.getData().put("app_id", app_id);
		request.getData().put("app_secret", app_secret);
		Result result = HttpUtil.asBean(request, Result.class);
		return result;
	}

	public JSONMessage sendInvite(String telephone, String companyName,
			String username, String password) {
		JSONMessage jMessage;

		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("param1", companyName);
			params.put("param2", username);
			params.put("param3", password);
			Result result = sendSms(app_template_id_invite, telephone, params);

			if ("0".equals(result.getRes_code())) {
				jMessage = JSONMessage.success();
			} else {
				jMessage = JSONMessage.failure(result.getRes_message());
			}
		} catch (Exception e) {
			jMessage = KConstants.Result.InternalException;
		}

		return jMessage;
	}

	private Result sendSms(String template_id, String telephone,
			Map<String, String> params) throws Exception {
		HttpUtil.Request request = new HttpUtil.Request();
		request.setSpec("http://api.189.cn/v2/emp/templateSms/sendSms");
		request.setMethod(HttpUtil.RequestMethod.POST);
		request.getData().put("app_id", app_id);
		request.getData().put("access_token", getToken());
		request.getData().put("acceptor_tel", telephone);
		request.getData().put("template_id", template_id);
		request.getData().put("template_param", JSON.toJSONString(params));
		request.getData().put("timestamp", DateUtil.getFullString());

		return HttpUtil.asBean(request, Result.class);
	}

}
