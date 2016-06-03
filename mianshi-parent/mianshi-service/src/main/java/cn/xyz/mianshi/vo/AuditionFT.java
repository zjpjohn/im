package cn.xyz.mianshi.vo;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

/**
 * 初试记录
 * <p>
 * 聚合索引：企业状态、个人状态、企业Id、职位Id、企业用户Id、个人用户Id、公司是否删除、个人是否删除
 * 
 * @author luorc
 *
 */
@Entity(value = "a_aft", noClassnameStored = true)
@Indexes(@Index("status,toStatus,companyId,jobId,userId,toUserId,idc,idu,t"))
public class AuditionFT {
	// (09:41) luorc: // 待交卷：a<3（招聘方） a<=1（应聘方)
	// (09:41) luorc: // 已交卷：a=3 and b=0
	// (09:41) luorc: // 已答复：a=3 and b>0
	// 待交卷：status=1 and toStatus<=2
	// 已交卷：status=1 and toStatus=4
	// 已答复：status!=1
	/**
	 * ToStatus
	 * 
	 * @author luorc
	 *
	 */
	public interface ToStatus {
		public static int DEFAULT = 1;// 已邀请
		public static int AGREE = 2;// 已同意
		public static int REFUSE = 3;// 已拒绝
		public static int FINISH = 4;// 已完成
	}

	public interface Status {
		public static int DEFAULT = 1;// 已邀请
		public static int NOT_PASS = 3;// 初试不通过
		public static int PASS = 2;// 初试通过
		// /** 招聘方邀请初试 */
		// public static int DEFAULT = 1;
		// /** 应聘方同意初试 */
		// public static int AGREE = 3;
		// /** 应聘方完成初试 */
		// public static int FINISH = 4;
		// /** 招聘方已评分 */
		// public static int SCORE = 5;
		// /** 应聘方拒绝初试 */
		// public static int REFUSE = 2;
		// /** 招聘方不通过初试 */
		// public static int NOT_PASS = 7;
		// /** 招聘方通过初试 */
		// public static int PASS = 6;

		// /** 招聘方邀请初试 */
		// public static int DEFAULT = 1;// 待交卷、已邀请
		// /** 应聘方同意初试 */
		// public static int AGREE = 2;// 待交卷、已邀请
		// /** 应聘方完成初试 */
		// public static int FINISH = 3;// 已交卷
		// /** 招聘方已评分 */
		// public static int SCORE = 4;// 已评分
		// /** 应聘方拒绝初试 */
		// public static int REFUSE = 5;// 已答复
		// /** 招聘方不通过初试 */
		// public static int NOT_PASS = 6;// 已答复
		// /** 招聘方通过初试 */
		// public static int PASS = 7;// 已答复

	}

	private @Id ObjectId id;

	private int companyId;
	private String companyName;
	private int userId;// 企业用户

	private ObjectId jobId;
	private String jobName;

	private int toUserId;// 应聘者
	private String toUserName;// 应聘者姓名
	private ObjectId resumeId;
	private String resumeName;

	private List<Object> examList;
	private Object results;

	private long createTime;
	private long endTime;
	private long expiredTime;
	private long startTime;

	private int status;// 企业状态
	private int toStatus;// 个人状态

	private int idc;// 企业是否删除
	private int idu;// 用户是否删除

	private long t;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public List<Object> getExamList() {
		return examList;
	}

	public void setExamList(List<Object> examList) {
		this.examList = examList;
	}

	public Object getResults() {
		return results;
	}

	public void setResults(Object results) {
		this.results = results;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(long expiredTime) {
		this.expiredTime = expiredTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
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
