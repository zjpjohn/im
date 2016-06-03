package tigase.shiku;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import tigase.db.NonAuthUserRepository;
import tigase.db.TigaseDBException;
import tigase.server.Message;
import tigase.server.Packet;
import tigase.util.DNSResolver;
import tigase.xmpp.JID;
import tigase.xmpp.StanzaType;
import tigase.xmpp.XMPPException;
import tigase.xmpp.XMPPProcessor;
import tigase.xmpp.XMPPProcessorIfc;
import tigase.xmpp.XMPPResourceConnection;
import tigase.xmpp.impl.C2SDeliveryErrorProcessor;

/**
 * 消息归档插件
 * 
 * @author lw@www.camelot.com
 */
public class ShikuMessageArchivePlugin extends XMPPProcessor implements XMPPProcessorIfc {
	private static final String ID = "shiku-message-archive-plugin";

	private static final Logger log = Logger.getLogger(ShikuAutoReplyPlugin.class.getName());

	public static final String OWNNER_JID = "ownner";

	private static final String MESSAGE = "message";
	private static final String[][] ELEMENT_PATHS = { { MESSAGE } };

	private static final String[] XMLNSS = { Packet.CLIENT_XMLNS };

	private static final Set<StanzaType> TYPES;
	static {
		HashSet<StanzaType> tmpTYPES = new HashSet<StanzaType>();
		tmpTYPES.add(null);
		tmpTYPES.addAll(EnumSet.of(StanzaType.groupchat, StanzaType.chat));
		TYPES = Collections.unmodifiableSet(tmpTYPES);
	}

	private JID shiku_ma_jid = null;//公共jid（接收jid使用）
	
	@Override
	public String id() {
		return ID;
	}
	//初始化插件配置
	public void init(Map<String, Object> settings) throws TigaseDBException {
		super.init(settings);

		String componentJidStr = (String) settings.get("component-jid");

		if (componentJidStr != null) {
			shiku_ma_jid = JID.jidInstanceNS(componentJidStr);
		} else {
			String defHost = DNSResolver.getDefaultHostname();
			shiku_ma_jid = JID.jidInstanceNS("message-archive", defHost, null);
		}
		log.log(Level.CONFIG,
				"Loaded shiku message archiving component jid option: {0} = {1}",
				new Object[] { "component-jid", shiku_ma_jid });
		System.out.println("Shiku MA LOADED = " + shiku_ma_jid.toString());
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
						|| ((type != null) && (type != StanzaType.chat)
								&& (type != StanzaType.groupchat) && (type != StanzaType.normal))) {
					return;
				}
				//如果数据包中的message元素中body不为空
				if (packet.getElemCDataStaticStr(Message.MESSAGE_BODY_PATH) != null) {
					//数据包复制副本
					Packet result = packet.copyElementOnly();
					result.setPacketTo(shiku_ma_jid);
					result.getElement().addAttribute(OWNNER_JID,session.getBareJID().toString());
					//写入流出队列
					results.offer(result);
				}
			}
		} catch (Exception e) {

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
