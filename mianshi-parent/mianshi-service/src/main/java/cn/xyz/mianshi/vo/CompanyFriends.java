package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

/**
 * 用户关注的企业
 * <p>
 * 片键：userId
 * 
 * @author luorc
 *
 */
@Entity(value = "co_friend", noClassnameStored = true)
@Indexes(@Index("companyId,userId"))
public class CompanyFriends {
	private int companyId;
	private String companyName;
	private @Id ObjectId id;
	private long time;
	private int userId;

	public CompanyFriends() {
		super();
	}

	public CompanyFriends(int companyId, int userId) {
		super();
		this.companyId = companyId;
		this.userId = userId;
	}

	public int getCompanyId() {
		return companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public ObjectId getId() {
		return id;
	}

	public long getTime() {
		return time;
	}

	public int getUserId() {
		return userId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
