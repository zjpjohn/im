package com.shiku.lbs.vo;

public class NearbyUser extends BasePoi {

	private Integer active;
	private Long birthday;// 生日
	private String description;// 签名
	private Integer diploma;// 学历
	private Integer maxAge;
	private Integer minAge;
	private String name;// 姓名
	private String nickname;// 昵称
	private Integer salary;// 薪水
	private Integer sex;// 性别
	private Integer userId;// 用户Id
	private Integer workExp;// 工作经验

	public Integer getActive() {
		return active;
	}

	public Long getBirthday() {
		return birthday;
	}

	public String getDescription() {
		return description;
	}

	public Integer getDiploma() {
		return diploma;
	}

	public Integer getMaxAge() {
		return maxAge;
	}

	public Integer getMinAge() {
		return minAge;
	}

	public String getName() {
		return name;
	}

	public String getNickname() {
		return nickname;
	}

	public Integer getSalary() {
		return salary;
	}

	public Integer getSex() {
		return sex;
	}

	public Integer getUserId() {
		return userId;
	}

	public Integer getWorkExp() {
		return workExp;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDiploma(Integer diploma) {
		this.diploma = diploma;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}

	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setSalary(Integer salary) {
		this.salary = salary;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setWorkExp(Integer workExp) {
		this.workExp = workExp;
	}

}
