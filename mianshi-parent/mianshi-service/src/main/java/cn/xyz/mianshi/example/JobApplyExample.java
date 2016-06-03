package cn.xyz.mianshi.example;

import org.bson.types.ObjectId;

public class JobApplyExample {
	private ObjectId applyId;
	private ObjectId jobId;
	private Integer pageSize = 20;
	private Integer read;
	private Integer status;
	private Integer tagId;

	public ObjectId getApplyId() {
		return applyId;
	}

	public ObjectId getJobId() {
		return jobId;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Integer getRead() {
		return read;
	}

	public Integer getStatus() {
		return status;
	}

	public Integer getTagId() {
		return tagId;
	}

	public void setApplyId(ObjectId applyId) {
		this.applyId = applyId;
	}

	public void setJobId(ObjectId jobId) {
		this.jobId = jobId;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public void setRead(Integer read) {
		this.read = read;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}

}
