package tigase.shiku;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import tigase.conf.ConfigurationException;
import tigase.osgi.ModulesManagerImpl;
import tigase.server.AbstractMessageReceiver;
import tigase.server.Packet;
import tigase.shiku.db.ShikuMessageArchiveRepository;
import tigase.shiku.model.MessageModel;
import tigase.shiku.model.MucMessageModel;
import tigase.xmpp.BareJID;
import tigase.xmpp.StanzaType;

/**
 * 消息记录归档组件
 * 
 * @author lw@www.camelot.com
 *
 */
public class ShikuMessageArchiveComponent extends AbstractMessageReceiver {
	private static final Logger log = Logger.getLogger(ShikuMessageArchiveComponent.class.getCanonicalName());
	private static final String MSG_ARCHIVE_REPO_CLASS_PROP_KEY = "archive-repo-class";
	private static final String MSG_ARCHIVE_REPO_URI_PROP_KEY = "archive-repo-uri";
	private static final String[] MSG_BODY_PATH = { "message", "body" };

	private ShikuMessageArchiveRepository repo = null;

	public ShikuMessageArchiveComponent() {
		super();
		setName("shiku-message-archive");
	}

	//处理数据包
		public void processPacket(Packet packet) {
			//如果接收方不为空且不等于组件的id时
			if ((packet.getStanzaTo() != null)&& !getComponentId().equals(packet.getStanzaTo())) {
				// 调用保存消息方法
				storeMessage(packet);
				return;
			}
		}

	@Override
	public Map<String, Object> getDefaults(Map<String, Object> params) {
		return super.getDefaults(params);
	}

	//初始化消息归档组件配置
	public void setProperties(Map<String, Object> props)
			throws ConfigurationException {
		try {
			super.setProperties(props);

			if (props.size() == 1) {
				return;
			}

			Map<String, String> repoProps = new HashMap<String, String>(4);
			for (Entry<String, Object> entry : props.entrySet()) {
				if ((entry.getKey() == null) || (entry.getValue() == null))
					continue;
				repoProps.put(entry.getKey(), entry.getValue().toString());
			}

			String repoClsName = (String) props
					.get(MSG_ARCHIVE_REPO_CLASS_PROP_KEY);
			String uri = (String) props.get(MSG_ARCHIVE_REPO_URI_PROP_KEY);

			if (null != uri) {
				if (null != repoClsName) {
					try {
						@SuppressWarnings("unchecked")
						Class<? extends ShikuMessageArchiveRepository> repoCls = (Class<? extends ShikuMessageArchiveRepository>) ModulesManagerImpl
								.getInstance().forName(repoClsName);
						repo = repoCls.newInstance();
						repo.initRepository(uri, repoProps);
					} catch (ClassNotFoundException e) {
						log.log(Level.SEVERE,
								"Could not find class "
										+ repoClsName
										+ " an implementation of ShikuMessageArchive repository",
								e);
						throw new ConfigurationException(
								"Could not find class "
										+ repoClsName
										+ " an implementation of ShikuMessageArchive repository",
								e);
					}
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "消息归档组件初始化失败", e);
			throw new ConfigurationException("消息归档组件初始化失败", e);
		}
	}

	@Override
	public void release() {
		super.release();
	}

	//获取描述
	public String getDiscoDescription() {
		return "ShiKu Message Archiving Support";
	}

	//获取UserId
	private Integer getUserId(BareJID jid) {
		String strUserId = jid.toString();
		int index = strUserId.indexOf("@");
		strUserId = strUserId.substring(0, index);
		
		return Integer.parseInt(strUserId);
	}

	//获取RoomId
	private String getRoomId(BareJID jid) {
		String strUserId = jid.toString();
		int index = strUserId.indexOf("@");
		strUserId = strUserId.substring(0, index);

		return strUserId;
	}

	//保存消息
	private void storeMessage(Packet packet) {
		String ownerStr = packet.getAttributeStaticStr(ShikuMessageArchivePlugin.OWNNER_JID);
		//如果数据包发送者不为空
		if (ownerStr != null) {
			packet.getElement().removeAttribute(ShikuMessageArchivePlugin.OWNNER_JID);
			//获得数据包类型，发送者id
			StanzaType type = packet.getType();
			BareJID sender_jid = BareJID.bareJIDInstanceNS(ownerStr);
			Integer sender = getUserId(sender_jid);
			// 单聊
			if (StanzaType.chat == type) {
				Integer direction = sender_jid.equals(packet.getStanzaFrom()
						.getBareJID()) ? 0 : 1;// 0=发出去的；1=收到的
				BareJID receiver_jid = direction == 0 ? packet.getStanzaTo()
						.getBareJID() : packet.getStanzaFrom().getBareJID();
				Integer receiver = getUserId(receiver_jid);
				Long ts = System.currentTimeMillis();
				String message = packet.getElement().toString();
				Integer messageType = 1;// 1=chat
				String body = packet.getElement().getChildCData(MSG_BODY_PATH);

				MessageModel model = new MessageModel(sender,
						sender_jid.toString(), receiver,
						receiver_jid.toString(), ts, direction, messageType,
						body, message);

				repo.archiveMessage(model);
			}
			// 群聊
			else if (StanzaType.groupchat == type) {
				if (sender_jid.equals(packet.getStanzaFrom().getBareJID())) {
					BareJID room_jid = packet.getStanzaTo().getBareJID();
					String room_id = getRoomId(room_jid);
					String nickname = "";
					String body = packet.getElement().getChildCData(
							MSG_BODY_PATH);
					String message = "";
					Integer public_event = 1;
					Long ts = System.currentTimeMillis();
					Integer event_type = 1;

					MucMessageModel model = new MucMessageModel(room_id,
							room_jid.toString(), sender, sender_jid.toString(),
							nickname, body, message, public_event, ts,
							event_type);

					repo.archiveMessage(model);
				}
			} else {

			}
		} else {
			log.log(Level.INFO, "Owner attribute missing from packet: {0}",packet);
		}
	}
}
