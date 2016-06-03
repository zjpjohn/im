package cn.xyz.mianshi.example;

import javax.validation.constraints.NotNull;

public class UserExample extends BaseExample {

	private Long birthday;
	private Integer companyId;
	private String description;
	private String idcard;
	private String idcardUrl;
	private String name;
	private String nickname;
	private @NotNull String password;
	private Integer sex;
	private @NotNull String telephone;
	private Integer userType;
	private String comunityCode;// 小区Id
	
	private Integer d = 0;
	private Integer w = 0;
	private String email;

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getIdcardUrl() {
		return idcardUrl;
	}

	public void setIdcardUrl(String idcardUrl) {
		this.idcardUrl = idcardUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Integer getD() {
		return d;
	}

	public void setD(Integer d) {
		this.d = d;
	}

	public Integer getW() {
		return w;
	}

	public void setW(Integer w) {
		this.w = w;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getComunityCode() {
		return comunityCode;
	}

	public void setComunityCode(String comunityCode) {
		this.comunityCode = comunityCode;
	}

}
