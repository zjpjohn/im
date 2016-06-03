package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

/**
 * 企业的粉丝
 * <p>
 * 片键：companyId
 * 
 * @author luorc
 *
 */
@Entity(value = "co_fans", noClassnameStored = true)
@Indexes(@Index("companyId,userId"))
public class CompanyFans {
	private int companyId;
	private @Id ObjectId id;
	private long time;
	private int userId;

	public CompanyFans() {
		super();
	}

	public CompanyFans(int companyId, int userId) {
		super();
		this.companyId = companyId;
		this.userId = userId;
	}

	public int getCompanyId() {
		return companyId;
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
