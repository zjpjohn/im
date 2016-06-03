package cn.xyz.mianshi.vo.cv;

import org.bson.types.ObjectId;

public class Edu {
	private ObjectId id;
	// 开始时间
	private long begin;
	// 结束时间
	private long end;
	// 学校名称
	private String name;
	// 学历Id
	private int degree;
	// 学历
	private String degreeText;
	// 专业Id
	private int major;
	// 专业
	private String majorText;
	// 专业描述
	private String desc;
	// 是否海外学习
	private int isForeign;

	public Edu() {
		super();
	}

	public Edu(long begin, long end, String name, int degree, String degreeText, int major, String majorText) {
		super();
		this.begin = begin;
		this.end = end;
		this.name = name;
		this.degree = degree;
		this.degreeText = degreeText;
		this.major = major;
		this.majorText = majorText;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public long getBegin() {
		return begin;
	}

	public void setBegin(long begin) {
		this.begin = begin;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public String getDegreeText() {
		return degreeText;
	}

	public void setDegreeText(String degreeText) {
		this.degreeText = degreeText;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public String getMajorText() {
		return majorText;
	}

	public void setMajorText(String majorText) {
		this.majorText = majorText;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getIsForeign() {
		return isForeign;
	}

	public void setIsForeign(int isForeign) {
		this.isForeign = isForeign;
	}

}