package cn.xyz.mianshi.vo.cv;

import com.alibaba.fastjson.annotation.JSONField;

public class Personal {

	// 国家Id
	private int countryId;
	// 省份Id
	private int provinceId;
	// 城市Id
	private int cityId;
	// 区县Id
	private int areaId;
	// 详细地址/通讯地址
	private String address;
	// 户口
	private int homeCityId;

	// 姓名
	private String name;
	// 身份证号码
	private String idNumber;
	// 手机号码
	private String telephone;
	// 邮箱
	private String email;

	// 性别：p.s
	// @JSONField(name = "sex")
	private int s;
	// 出生日期：p.b
	// @JSONField(name = "birthday")
	private long b;
	// 婚否：p.m
	// @JSONField(name = "marital")
	private int m;
	// 求职状态：p.j
	// @JSONField(name = "jobStatus")
	private int j;
	// 工作经验：p.w
	// @JSONField(name = "workExp")
	private int w;

	// 最高学历：p.d
	private int d;
	// 就职过的公司：p.nmCo
	@JSONField(serialize = false)
	private String co;
	// 就读过的学校：p.nmSc
	@JSONField(serialize = false)
	private String sc;
	// 学习过的专业：p.nmMa
	@JSONField(serialize = false)
	private String ma;

	// 居住地
	private String location;
	// 年薪
	private int salary;
	// 自我评价
	private String evaluate;

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

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getHomeCityId() {
		return homeCityId;
	}

	public void setHomeCityId(int homeCityId) {
		this.homeCityId = homeCityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getS() {
		return s;
	}

	public void setS(int s) {
		this.s = s;
	}

	public long getB() {
		return b;
	}

	public void setB(long b) {
		this.b = b;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	public String getCo() {
		return co;
	}

	public void setCo(String co) {
		this.co = co;
	}

	public String getSc() {
		return sc;
	}

	public void setSc(String sc) {
		this.sc = sc;
	}

	public String getMa() {
		return ma;
	}

	public void setMa(String ma) {
		this.ma = ma;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public String getEvaluate() {
		return evaluate;
	}

	public void setEvaluate(String evaluate) {
		this.evaluate = evaluate;
	}

}