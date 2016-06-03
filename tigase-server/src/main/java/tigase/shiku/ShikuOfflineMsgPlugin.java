package tigase.shiku;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import tigase.db.NonAuthUserRepository;
import tigase.db.TigaseDBException;
import tigase.server.Message;
import tigase.server.Packet;
import tigase.server.monitor.MonitorRuntime;
import tigase.shiku.utils.HttpUtil;
import tigase.xmpp.StanzaType;
import tigase.xmpp.XMPPException;
import tigase.xmpp.XMPPProcessor;
import tigase.xmpp.XMPPProcessorIfc;
import tigase.xmpp.XMPPResourceConnection;
import tigase.xmpp.impl.C2SDeliveryErrorProcessor;

/**
 * 离线推送通知、离线消息推送插件
 * 
 * @author lw@www.camelot.com
 */
public class ShikuOfflineMsgPlugin extends XMPPProcessor implements XMPPProcessorIfc {

	private static final String ID = "shiku-offline-msg";
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
	private String pushUrl;

	@Override
	public String id() {
		return ID;
	}
	
	//初始化插件配置
	public void init(Map<String, Object> settings) throws TigaseDBException {
		super.init(settings);
		pushUrl = (String) settings.get("pushUrl");
		System.out.println("zk Offline Msg PushUrl = " + pushUrl);
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
						// 消息接收方是登录用户
						if (session.isUserId(packet.getStanzaTo().getBareJID())){
							return;
						}
						// 接收方是否在线
						boolean isOnline = MonitorRuntime.getMonitorRuntime().isJidOnline(packet.getStanzaTo());
						// 是否进行离线通知
						boolean isNotify = !isOnline;

						// 用户离线、执行离线推送逻辑
						if (isNotify) {
							// TODO 此处请自行实现目标用户离线的消息处理逻辑
							// 单聊消息
							if (StanzaType.chat == type) {
							}
							// 群聊消息
							else if (StanzaType.groupchat == type) {
							}
							// 其他
							else {
							}

							// 请求离线通知处理接口
							HttpUtil.Request req = new HttpUtil.Request();
							req.setSpec(pushUrl);
							// req.getData().put("userId", packet.getStanzaTo());
							req.getData().put("from", packet.getStanzaFrom());
							req.getData().put("to", packet.getStanzaTo());
							req.getData().put("body",packet.getElement().getChildCData(Message.MESSAGE_BODY_PATH));

							String result = HttpUtil.asString(req);
							System.out.println(result);
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
