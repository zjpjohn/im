package tigase.shiku.model;

public class MucMessageModel {
	private String body;
	private Integer event_type = 1;
	private String message;
	private String nickname;
	private Integer public_event = 1;
	private String room_id;
	private String room_jid;

	private Integer sender;
	private String sender_jid;
	private Long ts;

	public MucMessageModel() {
		super();
	}

	public MucMessageModel(String room_id, String room_jid, Integer sender,
			String sender_jid, String nickname, String body, String message,
			Integer public_event, Long ts, Integer event_type) {
		super();
		this.room_id = room_id;
		this.room_jid = room_jid;
		this.sender = sender;
		this.sender_jid = sender_jid;
		this.nickname = nickname;
		this.body = body;
		this.message = message;
		this.public_event = public_event;
		this.ts = ts;
		this.event_type = event_type;
	}

	public String getBody() {
		return body;
	}

	public Integer getEvent_type() {
		return event_type;
	}

	public String getMessage() {
		return message;
	}

	public String getNickname() {
		return nickname;
	}

	public Integer getPublic_event() {
		return public_event;
	}

	public String getRoom_id() {
		return room_id;
	}

	public String getRoom_jid() {
		return room_jid;
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

	public void setBody(String body) {
		this.body = body;
	}

	public void setEvent_type(Integer event_type) {
		this.event_type = event_type;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setPublic_event(Integer public_event) {
		this.public_event = public_event;
	}

	public void setRoom_id(String room_id) {
		this.room_id = room_id;
	}

	public void setRoom_jid(String room_jid) {
		this.room_jid = room_jid;
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

}
