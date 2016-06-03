package cn.xyz.mianshi.vo.goods;

import java.util.Date;

public class Consume {
	// consume_id
	// consume_type_id
	// consume_time
	// company_id
	// user_id
	// to_user_id
	// vcount
	// balance
	// status

	private int consumeId;
	private int consumeTypeId;
	private Date consumeTime;
	private int companyId;
	private int userId;
	private int toUserId;
	private int vcount;
	private int balance;
	private int status;

	public int getConsumeId() {
		return consumeId;
	}

	public void setConsumeId(int consumeId) {
		this.consumeId = consumeId;
	}

	public int getConsumeTypeId() {
		return consumeTypeId;
	}

	public void setConsumeTypeId(int consumeTypeId) {
		this.consumeTypeId = consumeTypeId;
	}

	public Date getConsumeTime() {
		return consumeTime;
	}

	public void setConsumeTime(Date consumeTime) {
		this.consumeTime = consumeTime;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getToUserId() {
		return toUserId;
	}

	public void setToUserId(int toUserId) {
		this.toUserId = toUserId;
	}

	public int getVcount() {
		return vcount;
	}

	public void setVcount(int vcount) {
		this.vcount = vcount;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
