package cn.xyz.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

@Component
@ConfigurationProperties(prefix = "xmpp")
public class KXMPPServiceImpl implements ApplicationContextAware {
	private static Logger logger = Logger.getLogger(KXMPPServiceImpl.class); 
	
	public static class MessageBean {
		private Object content;
		private String fileName;
		private String fromUserId = "10005";
		private String fromUserName = "10005";
		private Object objectId;
		private long timeSend = System.currentTimeMillis() / 1000;
		private String toUserId;
		private String toUserName;
		private int type;

		public Object getContent() {
			return content;
		}

		public String getFileName() {
			return fileName;
		}

		public String getFromUserId() {
			return fromUserId;
		}

		public String getFromUserName() {
			return fromUserName;
		}

		public Object getObjectId() {
			return objectId;
		}

		public long getTimeSend() {
			return timeSend;
		}

		public String getToUserId() {
			return toUserId;
		}

		public String getToUserName() {
			return toUserName;
		}

		public int getType() {
			return type;
		}

		public void setContent(Object content) {
			this.content = content;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public void setFromUserId(String fromUserId) {
			this.fromUserId = fromUserId;
		}

		public void setFromUserName(String fromUserName) {
			this.fromUserName = fromUserName;
		}

		public void setObjectId(Object objectId) {
			this.objectId = objectId;
		}

		public void setTimeSend(long timeSend) {
			this.timeSend = timeSend;
		}

		public void setToUserId(String toUserId) {
			this.toUserId = toUserId;
		}

		public void setToUserName(String toUserName) {
			this.toUserName = toUserName;
		}

		public void setType(int type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return JSON.toJSONString(this);
		}

	}

	// 修改昵称
	// {
	// "type": 901,
	// "objectId": "房间Id",
	// "fromUserId": 10005,
	// "fromUserName": "10005",
	// "toUserId": 用户Id,
	// "toUserName": "用户昵称",
	// "timeSend": 123
	// }
	public static final int CHANGE_NICK_NAME = 901;

	// 修改房间名
	// {
	// "type": 902,
	// "objectId": "房间Id",
	// "content": "房间名",
	// "fromUserId": 10005,
	// "fromUserName": "10005",
	// "timeSend": 123
	// }
	public static final int CHANGE_ROOM_NAME = 902;

	// 删除成员
	// {
	// "type": 904,
	// "objectId": "房间Id",
	// "fromUserId": 0,
	// "fromUserName": "",
	// "toUserId": 被删除成员Id,
	// "timeSend": 123
	// }
	public static final int DELETE_MEMBER = 904;
	// 删除房间
	// {
	// "type": 903,
	// "objectId": "房间Id",
	// "content": "房间名",
	// "fromUserId": 10005,
	// "fromUserName": "10005",
	// "timeSend": 123
	// }
	public static final int DELETE_ROOM = 903;
	// 禁言
	// {
	// "type": 906,
	// "objectId": "房间Id",
	// "content": "禁言时间",
	// "fromUserId": 10005,
	// "fromUserName": "10005",
	// "toUserId": 被禁言成员Id,
	// "toUserName": "被禁言成员昵称",
	// "timeSend": 123
	// }
	public static final int GAG = 906;
	// 新成员
	// {
	// "type": 907,
	// "objectId": "房间Id",
	// "fromUserId": 邀请人Id,
	// "fromUserName": "邀请人昵称",
	// "toUserId": 新成员Id,
	// "toUserName": "新成员昵称",
	// "timeSend": 123
	// }
	public static final int NEW_MEMBER = 907;
	// 新公告
	// {
	// "type": 905,
	// "objectId": "房间Id",
	// "content": "公告内容",
	// "fromUserId": 10005,
	// "fromUserName": "10005",
	// "timeSend": 123
	// }
	public static final int NEW_NOTICE = 905;

	private static ApplicationContext context;

	public static KXMPPServiceImpl getInstance() {
		return context.getBean(KXMPPServiceImpl.class);
	}

	private XMPPConnection con;
	private ConnectionConfiguration config;
	private String from;
	private String host;
	private int port;
	private String username;
	private String password;

	// 引入smack包用于注册用户到Tigase（同步用户到Tigase）
	// <dependency>
	// <groupId>org.igniterealtime.smack</groupId>
	// <artifactId>smack</artifactId>
	// <version>3.2.1</version>
	// </dependency>
	// <dependency>
	// <groupId>org.igniterealtime.smack</groupId>
	// <artifactId>smackx</artifactId>
	// <version>3.2.1</version>
	// </dependency>
	public void register() {
		try {
			ConnectionConfiguration config = new ConnectionConfiguration(host,
					5222);
			config.setSASLAuthenticationEnabled(false);
			config.setDebuggerEnabled(false);
			config.setSecurityMode(SecurityMode.disabled);

			XMPPConnection con = new XMPPConnection(config);
			con.connect();
			// username：帐号、password：密码
			con.getAccountManager().createAccount(username, password);
			con.disconnect();

		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public XMPPConnection getConnection() throws Exception {
		if (null == config) {
			config = new ConnectionConfiguration(host, port);
			config.setSASLAuthenticationEnabled(false);
			config.setDebuggerEnabled(false);
			config.setSecurityMode(SecurityMode.disabled);
		}
		if (null == con || !con.isConnected()) {

			con = new XMPPConnection(config);
			con.connect();
			con.login(username, password);
			con.addConnectionListener(new ConnectionListener() {

				@Override
				public void connectionClosed() {
					con = null;
				}

				@Override
				public void connectionClosedOnError(Exception e) {
					con = null;
				}

				@Override
				public void reconnectingIn(int seconds) {
					con = null;
				}

				@Override
				public void reconnectionFailed(Exception e) {
					con = null;
				}

				@Override
				public void reconnectionSuccessful() {
					con = null;
				}
			});
			from = "10005@" + con.getServiceName();
		}
		return con;
	}

	public void register(String username, String password) throws Exception {
		ConnectionConfiguration config = new ConnectionConfiguration(host, 5222);
		config.setSASLAuthenticationEnabled(false);
		config.setDebuggerEnabled(false);
		config.setSecurityMode(SecurityMode.disabled);
		XMPPConnection con = new XMPPConnection(config);
		con.connect();
		con.getAccountManager().createAccount(username, password);
		con.disconnect();
		System.out.println("注册到Tigase：" + host + "," + username + ","
				+ password);
	}

	public void send(List<Integer> userIdList, String body) throws Exception {
		XMPPConnection _con = getConnection();
		for (int userId : userIdList) {
			Message message = new Message();
			message.setFrom(from);
			message.setTo(userId + "@" + _con.getServiceName());
			message.setBody(body);
			message.setType(Type.chat);
			try {
				_con.sendPacket(message);
				System.out.println("推送成功：" + message.toXML());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("推送失败：" + message.toXML());
			}
		}

	}

	/**
	 * 批量发送接口
	 * 
	 * @author chais
	 *
	 */
	public void send(String username, String password, List<Integer> userIdList, String body)
			throws Exception {
		XMPPConnection _con = getConnection(username, password);
		for (int userId : userIdList) {
			Message message = new Message();
			message.setFrom(username + "@" +  _con.getServiceName());
			message.setTo(userId + "@" + _con.getServiceName());
			message.setBody(body);
			message.setType(Type.chat);
			try {
				_con.sendPacket(message);
				logger.debug("推送成功：" + message.toXML());
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("推送失败：" + message.toXML());
			}
		}
	}
	
	public void send(int userId, String body) throws Exception {
		XMPPConnection _con = getConnection();
		Message message = new Message();
		message.setFrom(from);
		message.setTo(userId + "@" + _con.getServiceName());
		message.setBody(body);
		message.setType(Type.chat);
		try {
			_con.sendPacket(message);
			System.out.println("推送成功：" + message.toXML());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("推送失败：" + message.toXML());
		}
	}

	private XMPPConnection conSys;
	private String fromSys;

	public void send(String username, String password, int userId, String body)
			throws Exception {
		XMPPConnection _con = getConnection(username, password);
		Message message = new Message();
		message.setFrom(fromSys);
		message.setTo(userId + "@" + _con.getServiceName());
		message.setBody(body);
		message.setType(Type.chat);
		try {
			_con.sendPacket(message);
			System.out.println("系统推送成功：" + message.toXML());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("系统推送失败：" + message.toXML());
		}
	}

	public XMPPConnection getConnection(String username, String password)
			throws Exception {
		if (null == config) {
			config = new ConnectionConfiguration(host, port);
			config.setSASLAuthenticationEnabled(false);
			config.setDebuggerEnabled(false);
			config.setSecurityMode(SecurityMode.disabled);
		}
		if (null == conSys || !conSys.isConnected()) {
			conSys = new XMPPConnection(config);
			conSys.connect();
			conSys.login(username, password);
			conSys.addConnectionListener(new ConnectionListener() {

				@Override
				public void connectionClosed() {
					conSys = null;
				}

				@Override
				public void connectionClosedOnError(Exception e) {
					conSys = null;
				}

				@Override
				public void reconnectingIn(int seconds) {
					conSys = null;
				}

				@Override
				public void reconnectionFailed(Exception e) {
					conSys = null;
				}

				@Override
				public void reconnectionSuccessful() {
					conSys = null;
				}
			});
			fromSys = username + "@" + conSys.getServiceName();
		}
		return conSys;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		context = applicationContext;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
