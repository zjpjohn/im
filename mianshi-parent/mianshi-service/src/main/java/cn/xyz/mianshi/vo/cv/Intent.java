package cn.xyz.mianshi.vo.cv;

/**
 * 求职意向
 * <p>
 * 工作地点、到岗时间、职能、行业、工作类型、期望薪水
 * </p>
 * <p>
 * i.c,i.d,i.f,i.i,i.w,i.s
 * </p>
 * 
 * @author luorc
 *
 */
public class Intent {
	private int countryId;
	private int provinceId;
	private int areaId;

	// 工作地点
	// @JSONField(name = "cityId")
	private int c;
	private String cityText;

	// 到岗时间
	// @JSONField(name = "dutyTime")
	private int d;
	private String dutyTimeText;

	// 职能
	// @JSONField(name = "fn")
	private String f;
	private String fnText;

	// 行业
	// @JSONField(name = "industry")
	private String i;
	private String industryText;

	// 工作类型
	// @JSONField(name = "workType")
	private int w;
	private String workTypeText;

	// 期望薪水
	// @JSONField(name = "salary")
	private int s;
	private String salaryText;

	public Intent() {
		super();
	}

	public Intent(int c, String cityText, int d, String dutyTimeText, String f, String fnText, String i, String industryText, int w,
			String workTypeText, int s, String salaryText) {
		super();
		this.c = c;
		this.cityText = cityText;
		this.d = d;
		this.dutyTimeText = dutyTimeText;
		this.f = f;
		this.fnText = fnText;
		this.i = i;
		this.industryText = industryText;
		this.w = w;
		this.workTypeText = workTypeText;
		this.s = s;
		this.salaryText = salaryText;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}

	public String getCityText() {
		return cityText;
	}

	public void setCityText(String cityText) {
		this.cityText = cityText;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	public String getDutyTimeText() {
		return dutyTimeText;
	}

	public void setDutyTimeText(String dutyTimeText) {
		this.dutyTimeText = dutyTimeText;
	}

	public String getF() {
		return f;
	}

	public void setF(String f) {
		this.f = f;
	}

	public String getFnText() {
		return fnText;
	}

	public void setFnText(String fnText) {
		this.fnText = fnText;
	}

	public String getI() {
		return i;
	}

	public void setI(String i) {
		this.i = i;
	}

	public String getIndustryText() {
		return industryText;
	}

	public void setIndustryText(String industryText) {
		this.industryText = industryText;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public String getWorkTypeText() {
		return workTypeText;
	}

	public void setWorkTypeText(String workTypeText) {
		this.workTypeText = workTypeText;
	}

	public int getS() {
		return s;
	}

	public void setS(int s) {
		this.s = s;
	}

	public String getSalaryText() {
		return salaryText;
	}

	public void setSalaryText(String salaryText) {
		this.salaryText = salaryText;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

}
