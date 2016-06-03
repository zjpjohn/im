package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 职位申请记录
 * 
 * @author luorc
 *
 */
@Entity(value = "a_job_apply", noClassnameStored = true)
@Indexes(@Index("status,read,userId,toUserId,jobId,companyId,idc,idu"))
public class JobApply {

	/**
	 * 职位申请状态
	 * 
	 * @author luorc
	 *
	 */
	public interface Status {
		public static final int APPLY = 1;
		public static final int AGREE = 3;
		public static final int REFUSE = 2;
	}

	// 申请Id
	private @Id ObjectId applyId;

	// 公司Id
	private int companyId;
	// 公司名称
	private String companyName;

	// 职位Id
	private ObjectId jobId;
	// 职位名称
	private String jobName;

	// 企业用户Id
	private int userId;
	// 企业用户姓名
	private String userName;

	// 应聘者用户Id
	private int toUserId;
	// 应聘者姓名
	private String toUserName;

	// 使用的简历
	private ObjectId resumeId;
	// 使用的简历名称
	private String resumeName;

	// 申请日期
	private long time;

	// 是否已读：0=未读；1=已读
	private int read;

	// 标记Id
	private int tagId;

	// 状态：1=已申请；2=已拒绝；3=已同意
	private int status;

	// 用户是否删除
	@JSONField(serialize = false)
	private int idu;
	// 企业是否删除
	@JSONField(serialize = false)
	private int idc;

	private Object personal;

	public ObjectId getApplyId() {
		return applyId;
	}

	public void setApplyId(ObjectId applyId) {
		this.applyId = applyId;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public ObjectId getJobId() {
		return jobId;
	}

	public void setJobId(ObjectId jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getToUserId() {
		return toUserId;
	}

	public void setToUserId(int toUserId) {
		this.toUserId = toUserId;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public ObjectId getResumeId() {
		return resumeId;
	}

	public void setResumeId(ObjectId resumeId) {
		this.resumeId = resumeId;
	}

	public String getResumeName() {
		return resumeName;
	}

	public void setResumeName(String resumeName) {
		this.resumeName = resumeName;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getIdu() {
		return idu;
	}

	public void setIdu(int idu) {
		this.idu = idu;
	}

	public int getIdc() {
		return idc;
	}

	public void setIdc(int idc) {
		this.idc = idc;
	}

	public Object getPersonal() {
		return personal;
	}

	public void setPersonal(Object personal) {
		this.personal = personal;
	}

}
