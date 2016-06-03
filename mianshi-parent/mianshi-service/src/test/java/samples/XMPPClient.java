package samples;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class XMPPClient {
	private static XMPPClient instance;

	private ConnectionConfiguration config;
	private String host = "www.youjob.co";
	private int port = 5222;

	private XMPPClient() {
		config = new ConnectionConfiguration(host, port);
		config.setSASLAuthenticationEnabled(false);
		config.setDebuggerEnabled(false);
		config.setSecurityMode(SecurityMode.disabled);
	}

	public static XMPPClient getInstance() {
		if (null == instance)
			instance = new XMPPClient();
		return instance;
	}

	public void init(String username, String password) throws Exception {
		XMPPConnection connection = new XMPPConnection(config);
		connection.connect();
		connection.login(username, password);
		connection.addConnectionListener(new ConnectionListener() {

			@Override
			public void reconnectionSuccessful() {
				System.out.println("连接成功");
			}

			@Override
			public void reconnectionFailed(Exception e) {
				System.out.println("重连失败");
			}

			@Override
			public void reconnectingIn(int seconds) {
				System.out.println("重连中：" + seconds);
			}

			@Override
			public void connectionClosedOnError(Exception e) {
				System.out.println("连接关闭：" + e.getMessage());
			}

			@Override
			public void connectionClosed() {
				System.out.println("连接关闭");
			}
		});
		connection.addPacketListener(new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				if (packet instanceof Presence) {
					System.out.println("CL-Presence\t" + packet.toXML());
					Presence presence = (Presence) packet;
					switch (presence.getStatus()) {
					case "307":

						break;

					default:
						break;
					}
				} else if (packet instanceof IQ) {
					System.out.println("CL-IQ\t" + "\t" + packet.toXML());
				} else if (packet instanceof Message) {
					System.out.println("CL-Message\t" + packet.toXML());
				} else {
					System.out.println("CL-Others\t" + packet.toXML());
				}

			}
		}, null);

		MultiUserChat muc = new MultiUserChat(connection, "aabbcdd@muc.www.youjob.co");
		muc.join("罗融春1");
		muc.sendMessage("哈喽");
		muc.sendMessage("哈喽");
		muc.sendMessage("哈喽");
		muc.addMessageListener(new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				if (packet instanceof Presence) {
					System.out.println("MUC-Presence\t" + packet.toXML());
				} else if (packet instanceof IQ) {
					System.out.println("MUC-IQ\t" + "\t" + packet.toXML());
				} else if (packet instanceof Message) {
					System.out.println("MUC-Message\t" + packet.toXML());
				} else {
					System.out.println("MUC-Others\t" + packet.toXML());
				}
			}
		});
		while (true) {

		}

		// Chat chat = con.getChatManager().createChat("luorc@www.youjob.co",
		// null);
		// Thread thread = new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// while (true) {
		// try {
		// chat.sendMessage((System.currentTimeMillis() / 1000) + "");
		// Thread.sleep(360000);
		// System.out.println((System.currentTimeMillis() / 1000) + "已发送，休眠2秒");
		// } catch (XMPPException e) {
		// System.out.println("XMPP异常：" + e.getMessage());
		// } catch (InterruptedException e) {
		// System.out.println("线程异常：" + e.getMessage());
		// }
		// }
		// }
		// });
		// thread.start();

	}

	public static void main(String[] args) throws Exception {
		XMPPClient.getInstance().init("taowc", "123456");
	}
}
