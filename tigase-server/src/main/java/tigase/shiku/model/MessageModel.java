package tigase.shiku.model;

public class MessageModel {
	private String body;
	private Integer direction;// 0=发出的；1=收到的
	private String message;
	private Integer receiver;
	private String receiver_jid;
	private Integer sender;
	private String sender_jid;
	private Long ts;
	private Integer type;

	public MessageModel() {
		super();
	}

	public MessageModel(Integer sender, String sender_jid, Integer receiver,
			String receiver_jid, Long ts, Integer direction, Integer type,
			String body, String message) {
		super();
		this.sender = sender;
		this.sender_jid = sender_jid;
		this.receiver = receiver;
		this.receiver_jid = receiver_jid;
		this.ts = ts;
		this.direction = direction;
		this.type = type;
		this.body = body;
		this.message = message;
	}

	public String getBody() {
		return body;
	}

	public Integer getDirection() {
		return direction;
	}

	public String getMessage() {
		return message;
	}

	public Integer getReceiver() {
		return receiver;
	}

	public String getReceiver_jid() {
		return receiver_jid;
	}

	public Integer getSender() {
		return sender;
	}

	public String getSender_jid() {
		return sender_jid;
	}

	public Long getTs() {
		return ts;
	}

	public Integer getType() {
		return type;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setReceiver(Integer receiver) {
		this.receiver = receiver;
	}

	public void setReceiver_jid(String receiver_jid) {
		this.receiver_jid = receiver_jid;
	}

	public void setSender(Integer sender) {
		this.sender = sender;
	}

	public void setSender_jid(String sender_jid) {
		this.sender_jid = sender_jid;
	}

	public void setTs(Long ts) {
		this.ts = ts;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
