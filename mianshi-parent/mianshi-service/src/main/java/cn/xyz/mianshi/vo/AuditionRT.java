package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity(value = "a_art", noClassnameStored = true)
@Indexes(@Index("status,toStatus,companyId,jobId,userId,toUserId,idc,idu,t"))
public class AuditionRT {

	// 个人状态
	public interface ToStatus {
		public static int DEFAULT = 1;// 预约时间
		public static int AGREE = 2;// 同意复试
		public static int REFUSE = 3;// 拒绝复试
		public static int FINISH = 4;// 完成复试
	}

	// 企业状态
	public interface Status {
		public static int DEFAULT = 1;// 预约时间
		public static int PASS = 2;// 复试通过
		public static int NOT_PASS = 3;// 复试不通过
	}

	// 最终个人状态
	public interface FinalStatus {
		public static int DEFAULT = 1;// 未处理
		public static int AGREE = 2;// 同意入职
		public static int REFUSE = 3;// 拒绝入职
	}

	private @Id ObjectId id;
	private int status;
	private int toStatus;
	private int fiStatus;
	private int companyId;
	private ObjectId jobId;
	private int userId;
	private int toUserId;
	private int paid;
	private int idc;
	private int idu;
	private long t;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getToStatus() {
		return toStatus;
	}

	public void setToStatus(int toStatus) {
		this.toStatus = toStatus;
	}

	public int getFiStatus() {
		return fiStatus;
	}

	public void setFiStatus(int fiStatus) {
		this.fiStatus = fiStatus;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public ObjectId getJobId() {
		return jobId;
	}

	public void setJobId(ObjectId jobId) {
		this.jobId = jobId;
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

	public int getPaid() {
		return paid;
	}

	public void setPaid(int paid) {
		this.paid = paid;
	}

	public int getIdc() {
		return idc;
	}

	public void setIdc(int idc) {
		this.idc = idc;
	}

	public int getIdu() {
		return idu;
	}

	public void setIdu(int idu) {
		this.idu = idu;
	}

	public long getT() {
		return t;
	}

	public void setT(long t) {
		this.t = t;
	}

}
