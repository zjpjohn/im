package tigase.shiku;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import tigase.db.NonAuthUserRepository;
import tigase.server.Message;
import tigase.server.Packet;
import tigase.server.monitor.MonitorRuntime;
import tigase.xml.Element;
import tigase.xmpp.StanzaType;
import tigase.xmpp.XMPPException;
import tigase.xmpp.XMPPProcessor;
import tigase.xmpp.XMPPProcessorIfc;
import tigase.xmpp.XMPPResourceConnection;
import tigase.xmpp.impl.C2SDeliveryErrorProcessor;

/**
 * 回执信息插件
 * 1、单聊回执
 * 2、群聊回执
 * 
 * @author lw@www.camelot.com
 */
public class ShikuAutoReplyPlugin extends XMPPProcessor implements XMPPProcessorIfc {

	private static final String ID = "shiku-auto-reply";
	private static final String[] XMLNSS = { Packet.CLIENT_XMLNS };
	private static final Set<StanzaType> TYPES;
	static {
		HashSet<StanzaType> tmpTYPES = new HashSet<StanzaType>();
		tmpTYPES.add(null);
		tmpTYPES.addAll(EnumSet.of(StanzaType.groupchat, StanzaType.chat));
		TYPES = Collections.unmodifiableSet(tmpTYPES);
	}
	private static final String MESSAGE = "message";
	private static final String[][] ELEMENT_PATHS = { { MESSAGE } };

	@Override
	public String id() {
		return ID;
	}

	//处理数据包
	public void process(Packet packet, XMPPResourceConnection session,
			NonAuthUserRepository repo, Queue<Packet> results,
			Map<String, Object> settings) throws XMPPException {
		if (session == null) {
			return;
		}
		try {
			if (Message.ELEM_NAME == packet.getElemName()) {
				//如果是发送错误则直接返回
				if (C2SDeliveryErrorProcessor.isDeliveryError(packet)){
					return;
				}
				//获取数据包类型	
				StanzaType type = packet.getType();
				//如果数据包中的message元素中body为空、数据包类型不为空但类型不是chat、groupchat和normal的直接返回
				if ((packet.getElement().findChildStaticStr(Message.MESSAGE_BODY_PATH) == null)
						|| ((type != null) && (type != StanzaType.chat)&& (type != StanzaType.groupchat) 
								&& (type != StanzaType.normal))) {
					return;
				}
				//如果数据包中的message元素中body不为空
				if (packet.getElemCDataStaticStr(Message.MESSAGE_BODY_PATH) != null) {
					// 回执接收方是登录用户时
					if (session.isUserId(packet.getStanzaTo().getBareJID())){
						return;
					}
					boolean isReply = false;
					if (StanzaType.chat == type) {
						// 接收方是否在线
						boolean isOnline = MonitorRuntime.getMonitorRuntime().isJidOnline(packet.getStanzaTo());
						isReply = !isOnline;
					} else if (StanzaType.groupchat == type) {
						isReply = true;
					}

					if (isReply) {
						String id = packet.getStanzaId();
						if (null == id || "".equals(id)){
							return;
						}
						Element received = new Element("received");
						received.setXMLNS("urn:xmpp:receipts");
						received.addAttribute("id", id);
						received.addAttribute("status", "1");

						Packet receipt = Packet.packetInstance("message",packet.getStanzaTo().toString(),
								packet.getStanzaFrom().toString(),StanzaType.normal);
						receipt.getElement().addChild(received);

						// 将回执写入流出队列
						results.offer(receipt);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String[][] supElementNamePaths() {
		return ELEMENT_PATHS;
	}

	@Override
	public String[] supNamespaces() {
		return XMLNSS;
	}

	@Override
	public Set<StanzaType> supTypes() {
		return TYPES;
	}

}

// public class ShikuAutoReplyPlugin extends XMPPProcessor implements
// XMPPPreprocessorIfc {
// private static final Logger log = Logger
// .getLogger(ShikuAutoReplyPlugin.class.getName());
//
// private static final String ID = "shiku-auto-reply";
// private static final String[] XMLNSS = { Packet.CLIENT_XMLNS };
// private static final Set<StanzaType> TYPES;
// static {
// HashSet<StanzaType> tmpTYPES = new HashSet<StanzaType>();
// tmpTYPES.add(null);
// tmpTYPES.addAll(EnumSet.of(StanzaType.groupchat, StanzaType.chat));
// TYPES = Collections.unmodifiableSet(tmpTYPES);
// }
// private static final String MESSAGE = "message";
// private static final String[][] ELEMENT_PATHS = { { MESSAGE } };
//
// @Override
// public String id() {
// return ID;
// }
//
// @Override
// public boolean preProcess(Packet packet, XMPPResourceConnection session,
// NonAuthUserRepository repo, Queue<Packet> results,
// Map<String, Object> settings) {
// if (log.isLoggable(Level.FINEST)) {
// log.log(Level.FINEST,
// "SKYJPlugin Processing packet: {0}, for session: {1}",
// new Object[] { packet, session });
// }
// if (null != session) {
// StanzaType type = packet.getType();
// if (type == null)
// type = StanzaType.normal;
// // 单聊消息
// if (type == StanzaType.chat) {
// // 发送方
// BareJID from = (packet.getStanzaFrom() != null) ? packet
// .getStanzaFrom().getBareJID() : null;
// // 接收方
// BareJID to = (packet.getStanzaTo() != null) ? packet
// .getStanzaTo().getBareJID() : null;
// try {
// // 发送方是当前用户、接收方不是当前用户
// if (session.isUserId(from) && !session.isUserId(to)) {
// // 接收方是否在线
// boolean isOnline = MonitorRuntime.getMonitorRuntime()
// .isJidOnline(packet.getStanzaTo());
// // 接收方离线，服务端以接收方身份反馈回执给发送方
// if (!isOnline) {
// Element received = new Element("received");
// received.setXMLNS("urn:xmpp:receipts");
// received.addAttribute("id", packet.getStanzaId());
// received.addAttribute("status", "1");
//
// Packet receipt = Packet.packetInstance("message",
// packet.getStanzaTo().toString(), packet
// .getStanzaFrom().toString(),
// StanzaType.normal);
// receipt.getElement().addChild(received);
//
// // 将回执写入流出队列
// results.offer(receipt);
// }
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
// } else if (type == StanzaType.groupchat) {
// // 发送方
// BareJID from = (packet.getStanzaFrom() != null) ? packet
// .getStanzaFrom().getBareJID() : null;
// // 接收方
// BareJID to = (packet.getStanzaTo() != null) ? packet
// .getStanzaTo().getBareJID() : null;
// try {
// // 发送方是当前用户、接收方不是当前用户
// if (session.isUserId(from) && !session.isUserId(to)) {
// Element received = new Element("received");
// received.setXMLNS("urn:xmpp:receipts");
// received.addAttribute("id", packet.getStanzaId());
// received.addAttribute("status", "1");
//
// Packet receipt = Packet.packetInstance("message",
// packet.getStanzaTo().toString(), packet
// .getStanzaFrom().toString(),
// StanzaType.normal);
// receipt.getElement().addChild(received);
//
// // 将回执写入流出队列
// results.offer(receipt);
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
// }
//
// return false;
// }
//
// @Override
// public String[][] supElementNamePaths() {
// return ELEMENT_PATHS;
//
// }
//
// @Override
// public String[] supNamespaces() {
// return XMLNSS;
// }
//
// @Override
// public Set<StanzaType> supTypes() {
// return TYPES;
// }
//
// }
