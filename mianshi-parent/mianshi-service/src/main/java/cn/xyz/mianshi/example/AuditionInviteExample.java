package cn.xyz.mianshi.example;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.vo.cv.ResumeV2;

public class AuditionInviteExample {
	private ObjectId jobId;// 职位Id
	private String name;
	private ResumeV2 resume;
	private String telephone;// 求职用户手机号
	private int userId;// 求职用户

	public ObjectId getJobId() {
		return jobId;
	}

	public String getName() {
		return name;
	}

	public ResumeV2 getResume() {
		return resume;
	}

	public String getTelephone() {
		return telephone;
	}

	public int getUserId() {
		return userId;
	}

	public void setJobId(ObjectId jobId) {
		this.jobId = jobId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setResume(ResumeV2 resume) {
		this.resume = resume;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
