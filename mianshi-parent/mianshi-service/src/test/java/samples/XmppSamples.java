package samples;

import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class XmppSamples {
	private static final ConnectionConfiguration config;
	private static final String host = "www.youjob.co";
	private static final int port = 5222;
	static {
		config = new ConnectionConfiguration(host, port);
		config.setSASLAuthenticationEnabled(false);
		config.setDebuggerEnabled(true);
		config.setSecurityMode(SecurityMode.disabled);
	}

	public static XMPPConnection getConnection() throws Exception {
		XMPPConnection connection = new XMPPConnection(config);
		connection.connect();
		return connection;
	}

	public static XMPPConnection getConnection(String username, String password)
			throws Exception {
		XMPPConnection connection = new XMPPConnection(config);
		connection.connect();
		connection.login(username, password);

		return connection;
	}

	public static void register(String username, String password)
			throws Exception {
		XMPPConnection connection = getConnection();
		connection.getAccountManager().createAccount(username, password);
		connection.disconnect();
	}

	public static List<String> getStoreMessage(String roomJID, String username,
			String password) throws Exception {
		DiscussionHistory history = new DiscussionHistory();
		history.setSeconds(0);
		// history.setMaxChars(0);
		XMPPConnection connection = getConnection(username, password);
		connection.addPacketListener(new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				System.out.println(packet.toXML());
			}
		}, null);
		MultiUserChat muc = new MultiUserChat(connection,
				"33c25e82ee6a497b877a3369e4812637@muc.www.youjob.co");
		muc.join("admin123", null, history,
				SmackConfiguration.getPacketReplyTimeout());
		muc.addMessageListener(new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				// System.out.println("MUC：" + packet.toXML());
			}
		});
		return null;
	}

	public static void main(String... args) throws Exception {
		// getStoreMessage(null, "10000028",
		// "b2ca678b4c936f905fb82f2733f5297f");
		XMPPConnection con = getConnection();
		for (int i = 5; i < 2000; i++) {
			con.getAccountManager().createAccount("tsung" + i, "tsung");
			System.out.println(i);
		}
	}

	public static void aaa() throws Exception {
		XMPPConnection connection = getConnection("10000028",
				"b2ca678b4c936f905fb82f2733f5297f");
		MultiUserChat muc = new MultiUserChat(connection, "123456789" + "@muc."
				+ connection.getServiceName());
		muc.create("123456789");

		// 获得聊天室的配置表单
		Form form = muc.getConfigurationForm();
		// 根据原始表单创建一个要提交的新表单。
		Form submitForm = form.createAnswerForm();
		// 向要提交的表单添加默认答复
		Iterator<FormField> fields = form.getFields();
		while (fields.hasNext()) {
			FormField field = fields.next();
			if (!FormField.TYPE_HIDDEN.equals(field.getType())
					&& field.getVariable() != null) {
				// 设置默认值作为答复
				submitForm.setDefaultAnswer(field.getVariable());
			}
		}

		// muc#roomconfig_roomname 房间名称
		// muc#roomconfig_roomdesc 房间描述
		// muc#roomconfig_persistentroom 房间是持久的
		// muc#roomconfig_publicroom 公开的，允许被搜索到
		// muc#roomconfig_moderatedroom 房间是临时的
		// muc#roomconfig_membersonly 房间仅对成员开放
		// muc#roomconfig_passwordprotectedroom 需要密码才能进入的房间
		// muc#roomconfig_roomsecret 设置房间密码
		// muc#roomconfig_anonymity 匿名的房间
		// muc#roomconfig_changesubject 允许占有者更改主题
		// muc#roomconfig_enablelogging 登陆房间对话
		// logging_format
		// muc#maxhistoryfetch

		submitForm.setAnswer("muc#roomconfig_roomname", "123456789");
		submitForm.setAnswer("muc#roomconfig_roomdesc", "123456789");
		submitForm.setAnswer("muc#roomconfig_persistentroom", true);
		submitForm.setAnswer("muc#roomconfig_publicroom", true);
		submitForm.setAnswer("muc#roomconfig_enablelogging", true);
		muc.sendConfigurationForm(submitForm);

		while (true) {

		}
	}

	// private String host = "www.youjob.co";
	//
	// public void addMember(User user, String roomId, Member member) throws
	// Exception {
	// XMPPConnection connection = getConnection(user);
	// MultiUserChat muc = new MultiUserChat(connection, roomId + "@muc." +
	// connection.getServiceName());
	// if (2 == member.getRole().intValue())
	// muc.grantAdmin(member.getUserId() + "@" + connection.getServiceName());
	// else if (3 == member.getRole().intValue())
	// muc.grantMembership(member.getUserId() + "@" +
	// connection.getServiceName());
	// connection.disconnect();
	// }
	//
	// public void deleteMember(User user, String roomId, Member member) throws
	// Exception {
	// XMPPConnection connection = getConnection(user);
	// MultiUserChat muc = new MultiUserChat(connection, roomId + "@muc." +
	// connection.getServiceName());
	// if (2 == member.getRole().intValue())
	// muc.revokeAdmin(member.getUserId() + "@" + connection.getServiceName());
	// else if (3 == member.getRole().intValue())
	// muc.revokeMembership(member.getUserId() + "@" +
	// connection.getServiceName());
	// connection.disconnect();
	// }
	//
	// public void addRoom(User user, String roomId, String roomName) throws
	// Exception {
	// XMPPConnection connection = getConnection(user);
	// MultiUserChat muc = new MultiUserChat(connection, roomId + "@muc." +
	// connection.getServiceName());
	// muc.create(roomName);
	//
	// // 获得聊天室的配置表单
	// Form form = muc.getConfigurationForm();
	// // 根据原始表单创建一个要提交的新表单。
	// Form submitForm = form.createAnswerForm();
	// // 向要提交的表单添加默认答复
	// Iterator<FormField> fields = form.getFields();
	// while (fields.hasNext()) {
	// FormField field = fields.next();
	// if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable()
	// != null) {
	// // 设置默认值作为答复
	// submitForm.setDefaultAnswer(field.getVariable());
	// }
	// }
	//
	// // muc#roomconfig_roomname 房间名称
	// // muc#roomconfig_roomdesc 房间描述
	// // muc#roomconfig_persistentroom 房间是持久的
	// // muc#roomconfig_publicroom 公开的，允许被搜索到
	// // muc#roomconfig_moderatedroom 房间是临时的
	// // muc#roomconfig_membersonly 房间仅对成员开放
	// // muc#roomconfig_passwordprotectedroom 需要密码才能进入的房间
	// // muc#roomconfig_roomsecret 设置房间密码
	// // muc#roomconfig_anonymity 匿名的房间
	// // muc#roomconfig_changesubject 允许占有者更改主题
	// // muc#roomconfig_enablelogging 登陆房间对话
	// // logging_format
	// // muc#maxhistoryfetch
	//
	// submitForm.setAnswer("muc#roomconfig_roomname", roomName);
	// submitForm.setAnswer("muc#roomconfig_roomdesc", roomName);
	// submitForm.setAnswer("muc#roomconfig_persistentroom", true);
	// submitForm.setAnswer("muc#roomconfig_publicroom", true);
	// submitForm.setAnswer("muc#roomconfig_enablelogging", true);
	// muc.sendConfigurationForm(submitForm);
	// connection.disconnect();
	// }
	//
	// public void deleteRoom(User user, String roomId) throws Exception {
	// XMPPConnection connection = getConnection(user);
	// MultiUserChat muc = new MultiUserChat(connection, roomId + "@muc." +
	// connection.getServiceName());
	// muc.destroy(null, null);
	// }
	//
	// public ConnectionConfiguration getConfig() {
	// ConnectionConfiguration config = new ConnectionConfiguration(host, 5222);
	// config.setSASLAuthenticationEnabled(false);
	// config.setDebuggerEnabled(false);
	// config.setSecurityMode(SecurityMode.disabled);
	//
	// return config;
	// }
	//
	// public XMPPConnection getConnection() throws Exception {
	// XMPPConnection connection = new XMPPConnection(getConfig());
	// connection.connect();
	// return connection;
	// }
	//
	// public XMPPConnection getConnection(String username, String password)
	// throws Exception {
	// ConnectionConfiguration config = new ConnectionConfiguration(host, 5222);
	// config.setSASLAuthenticationEnabled(false);
	// config.setDebuggerEnabled(false);
	// config.setSecurityMode(SecurityMode.disabled);
	//
	// XMPPConnection connection = new XMPPConnection(config);
	// connection.connect();
	// connection.login(username, password);
	//
	// return connection;
	// }
	//
	// public XMPPConnection getConnection(User user) throws Exception {
	// return getConnection(user.getUserId().toString(), user.getPassword());
	// }
	//
}
