package cn.xyz.mianshi.vo.cv;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 简历模型
 * <p>
 * 聚合索引：状态、性别、出生日期、婚否、求职状态、工作经验、最高学历、就职过的公司、就读过的学校、学习过的专业、
 * 工作地点、到岗时间、职能、行业、工作类型、期望薪水
 * </p>
 * 
 * @author luorc
 *
 */
@Entity("u_resume")
@Indexes(@Index("s,p.s,p.b,p.m,p.j,p.w,p.d,p.co,p.sc,p.ma,i.c,i.d,i.f,i.i,i.w,i.s,userId"))
public class ResumeV2 {
	public static final String DOC_NAME = "u_resume";
	// 简历Id
	private @Id ObjectId resumeId;
	// 简历名称
	private String resumeName;
	// 创建时间
	private Long createTime;
	// 修改时间
	private Long modifyTime;
	// 所属用户
	private Integer userId;

	// 个人信息
	// @JSONField(name = "personal")
	private Personal p;

	// 求职意向
	// @JSONField(name = "intent")
	private Intent i;

	// 教育经历
	// @JSONField(name = "eduList")
	private List<Edu> e;

	// 工作经历
	// @JSONField(name = "workList")
	private List<Work> w;

	// 项目经验
	private List<Project> projectList;

	// 状态：0=删除；1=正常；2=默认简历
	@JSONField(serialize = false)
	private int s = 1;

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

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Long modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Personal getP() {
		return p;
	}

	public void setP(Personal p) {
		this.p = p;
	}

	public Intent getI() {
		return i;
	}

	public void setI(Intent i) {
		this.i = i;
	}

	public List<Edu> getE() {
		return e;
	}

	public void setE(List<Edu> e) {
		this.e = e;
	}

	public List<Work> getW() {
		return w;
	}

	public void setW(List<Work> w) {
		this.w = w;
	}

	public List<Project> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<Project> projectList) {
		this.projectList = projectList;
	}

	public int getS() {
		return s;
	}

	public void setS(int s) {
		this.s = s;
	}

}
