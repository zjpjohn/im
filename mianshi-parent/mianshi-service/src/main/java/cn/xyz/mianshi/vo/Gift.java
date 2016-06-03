package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import cn.xyz.commons.utils.JSONUtil;

@Entity(value = "s_gift", noClassnameStored = true)
public class Gift {
	private int count;// 礼物数量
	private @Id ObjectId giftId;// 礼物Id
	private @Indexed int id;// 送礼物记录Id
	private @Indexed ObjectId msgId;// 送礼物所属消息Id
	private String nickname;// 送礼物用户昵称
	private int price;// 礼物价格
	private long time;// 送礼物时间
	private @Indexed int userId;// 送礼物用户Id

	public Gift() {
		super();
	}

	public Gift(ObjectId giftId, ObjectId msgId, int userId, String nickname,
			int id, int price, int count, long time) {
		super();
		this.giftId = giftId;
		this.msgId = msgId;
		this.userId = userId;
		this.nickname = nickname;
		this.id = id;
		this.price = price;
		this.count = count;
		this.time = time;
	}

	public int getCount() {
		return count;
	}

	public ObjectId getGiftId() {
		return giftId;
	}

	public int getId() {
		return id;
	}

	public ObjectId getMsgId() {
		return msgId;
	}

	public String getNickname() {
		return nickname;
	}

	public int getPrice() {
		return price;
	}

	public long getTime() {
		return time;
	}

	public int getUserId() {
		return userId;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setGiftId(ObjectId giftId) {
		this.giftId = giftId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMsgId(ObjectId msgId) {
		this.msgId = msgId;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

}
