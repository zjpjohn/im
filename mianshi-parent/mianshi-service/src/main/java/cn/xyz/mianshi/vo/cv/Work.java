package cn.xyz.mianshi.vo.cv;

import org.bson.types.ObjectId;

public class Work {
	private ObjectId id;
	// 开始时间
	private long begin;
	// 公司名称
	private String companyName;
	// 部门
	private String department;
	// 工作描述
	private String desc;
	// 结束时间
	private long end;

	// 职位Id/职能Id
	private int fn;
	private String fnText;

	// 公司行业Id
	private int industry;
	private String industryText;

	// 公司性质Id
	private int nature;
	private String natureText;

	// 公司规模Id
	private int scale;
	private String scaleText;

	public Work() {
		super();
	}

	public Work(long begin, String companyName, String department, String desc, long end, int fn, String fnText, int industry,
			String industryText, int nature, String natureText, int scale, String scaleText) {
		super();
		this.begin = begin;
		this.companyName = companyName;
		this.department = department;
		this.desc = desc;
		this.end = end;
		this.fn = fn;
		this.fnText = fnText;
		this.industry = industry;
		this.industryText = industryText;
		this.nature = nature;
		this.natureText = natureText;
		this.scale = scale;
		this.scaleText = scaleText;
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public int getFn() {
		return fn;
	}

	public void setFn(int fn) {
		this.fn = fn;
	}

	public String getFnText() {
		return fnText;
	}

	public void setFnText(String fnText) {
		this.fnText = fnText;
	}

	public int getIndustry() {
		return industry;
	}

	public void setIndustry(int industry) {
		this.industry = industry;
	}

	public String getIndustryText() {
		return industryText;
	}

	public void setIndustryText(String industryText) {
		this.industryText = industryText;
	}

	public int getNature() {
		return nature;
	}

	public void setNature(int nature) {
		this.nature = nature;
	}

	public String getNatureText() {
		return natureText;
	}

	public void setNatureText(String natureText) {
		this.natureText = natureText;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public String getScaleText() {
		return scaleText;
	}

	public void setScaleText(String scaleText) {
		this.scaleText = scaleText;
	}

}
