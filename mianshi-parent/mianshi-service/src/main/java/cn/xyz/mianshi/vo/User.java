package cn.xyz.mianshi.vo;

import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.utils.IndexDirection;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.example.UserExample;
import cn.xyz.mianshi.vo.cv.ResumeV2;

import com.alibaba.fastjson.annotation.JSONField;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Entity(value = "user", noClassnameStored = true)
@Indexes({ @Index("status,companyId,birthday,sex,cityId") })
public class User {

	@Id
	private Integer userId;// 用户Id

	@JSONField(serialize = false)
	@Indexed(unique = true)
	private String userKey;// 用户唯一标识

	@JSONField(serialize = false)
	private String username;// 用户名

	@JSONField(serialize = false)
	private String password;

	private Integer userType;// 用户类型
	private Integer companyId;

	@Indexed(unique = true)
	private String telephone;

	private String name;// 姓名

	@Indexed(value = IndexDirection.ASC)
	private String nickname;// 昵称

	@Indexed(value = IndexDirection.ASC)
	private Long birthday;// 生日

	@Indexed(value = IndexDirection.ASC)
	private Integer sex;// 性别

	@Indexed(value = IndexDirection.ASC)
	private Integer active;// 最后出现时间

	@Indexed(value = IndexDirection.GEO2D)
	private Loc loc;// 地理位置

	private String description;// 签名、说说、备注

	private Integer countryId;
	private Integer provinceId;
	private Integer cityId;
	private Integer areaId;

	private Integer level;// 等级
	private Integer vip;// VIP级别

	private Integer money;
	private Integer moneyTotal;

	private Integer friendsCount;// 好友数
	private Integer fansCount;// 粉丝数
	private Integer attCount;// 关注数

	private Long createTime;
	private Long modifyTime;

	private String idcard;// 身份证号码
	private String idcardUrl;

	private Integer isAuth;
	private Integer status;
	
	private String comunityCode;// 小区Id
	
	// ********************引用字段********************
	private @NotSaved LoginLog loginLog;
	private @NotSaved UserSettings settings;
	private @NotSaved CompanyVO company;
	private @NotSaved Friends friends;
	// 第三方帐号列表
	private @NotSaved List<ThridPartyAccount> accounts;
	// 关注列表
	private @NotSaved List<Friends> attList;
	// 粉丝列表
	private @NotSaved List<Fans> fansList;
	// 好友列表
	private @NotSaved List<Friends> friendsList;
	// 简历列表
	private @NotSaved List<ResumeV2> resumeList;

	// ********************引用字段********************

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
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

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getActive() {
		return active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public Loc getLoc() {
		return loc;
	}

	public void setLoc(Loc loc) {
		this.loc = loc;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getVip() {
		return vip;
	}

	public void setVip(Integer vip) {
		this.vip = vip;
	}

	public Integer getMoney() {
		return money;
	}

	public void setMoney(Integer money) {
		this.money = money;
	}

	public Integer getMoneyTotal() {
		return moneyTotal;
	}

	public void setMoneyTotal(Integer moneyTotal) {
		this.moneyTotal = moneyTotal;
	}

	public Integer getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(Integer friendsCount) {
		this.friendsCount = friendsCount;
	}

	public Integer getFansCount() {
		return fansCount;
	}

	public void setFansCount(Integer fansCount) {
		this.fansCount = fansCount;
	}

	public Integer getAttCount() {
		return attCount;
	}

	public void setAttCount(Integer attCount) {
		this.attCount = attCount;
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

	public Integer getIsAuth() {
		return isAuth;
	}

	public void setIsAuth(Integer isAuth) {
		this.isAuth = isAuth;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public LoginLog getLoginLog() {
		return loginLog;
	}

	public void setLoginLog(LoginLog loginLog) {
		this.loginLog = loginLog;
	}

	public UserSettings getSettings() {
		return settings;
	}

	public void setSettings(UserSettings settings) {
		this.settings = settings;
	}

	public CompanyVO getCompany() {
		return company;
	}

	public void setCompany(CompanyVO company) {
		this.company = company;
	}

	public Friends getFriends() {
		return friends;
	}

	public void setFriends(Friends friends) {
		this.friends = friends;
	}

	public List<ThridPartyAccount> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<ThridPartyAccount> accounts) {
		this.accounts = accounts;
	}

	public List<Friends> getAttList() {
		return attList;
	}

	public void setAttList(List<Friends> attList) {
		this.attList = attList;
	}

	public List<Fans> getFansList() {
		return fansList;
	}

	public void setFansList(List<Fans> fansList) {
		this.fansList = fansList;
	}

	public List<Friends> getFriendsList() {
		return friendsList;
	}

	public void setFriendsList(List<Friends> friendsList) {
		this.friendsList = friendsList;
	}

	public List<ResumeV2> getResumeList() {
		return resumeList;
	}

	public void setResumeList(List<ResumeV2> resumeList) {
		this.resumeList = resumeList;
	}

	public String getComunityCode() {
		return comunityCode;
	}

	public void setComunityCode(String comunityCode) {
		this.comunityCode = comunityCode;
	}

	public static class Count {
		private int att;
		private int fans;
		private int friends;

		public int getAtt() {
			return att;
		}

		public int getFans() {
			return fans;
		}

		public int getFriends() {
			return friends;
		}

		public void setAtt(int att) {
			this.att = att;
		}

		public void setFans(int fans) {
			this.fans = fans;
		}

		public void setFriends(int friends) {
			this.friends = friends;
		}
	}

	public static class ThridPartyAccount {

		private long createTime;
		private long modifyTime;
		private int status;// 状态（0：解绑；1：绑定）
		private String tpAccount;// 账号
		private String tpName;// 帐号所属平台名字或代码
		private String tpUserId;// 账号唯一标识

		public long getCreateTime() {
			return createTime;
		}

		public long getModifyTime() {
			return modifyTime;
		}

		public int getStatus() {
			return status;
		}

		public String getTpAccount() {
			return tpAccount;
		}

		public String getTpName() {
			return tpName;
		}

		public String getTpUserId() {
			return tpUserId;
		}

		public void setCreateTime(long createTime) {
			this.createTime = createTime;
		}

		public void setModifyTime(long modifyTime) {
			this.modifyTime = modifyTime;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public void setTpAccount(String tpAccount) {
			this.tpAccount = tpAccount;
		}

		public void setTpName(String tpName) {
			this.tpName = tpName;
		}

		public void setTpUserId(String tpUserId) {
			this.tpUserId = tpUserId;
		}

	}

	public static class LoginLog {
		private int isFirstLogin;
		private long loginTime;
		private String apiVersion;
		private String osVersion;
		private String model;
		private String serial;
		private double latitude;
		private double longitude;
		private String location;
		private String address;
		private long offlineTime;

		public static DBObject init(UserExample example, boolean isFirst) {
			DBObject jo = new BasicDBObject();
			jo.put("isFirstLogin", isFirst ? 1 : 0);
			jo.put("loginTime", DateUtil.currentTimeSeconds());
			jo.put("apiVersion", example.getApiVersion());
			jo.put("osVersion", example.getOsVersion());
			jo.put("model", example.getModel());
			jo.put("serial", example.getSerial());
			jo.put("latitude", example.getLatitude());
			jo.put("longitude", example.getLongitude());
			jo.put("location", example.getLocation());
			jo.put("address", example.getAddress());
			jo.put("offlineTime", 0);

			return jo;
		}

		public int getIsFirstLogin() {
			return isFirstLogin;
		}

		public void setIsFirstLogin(int isFirstLogin) {
			this.isFirstLogin = isFirstLogin;
		}

		public long getLoginTime() {
			return loginTime;
		}

		public void setLoginTime(long loginTime) {
			this.loginTime = loginTime;
		}

		public String getApiVersion() {
			return apiVersion;
		}

		public void setApiVersion(String apiVersion) {
			this.apiVersion = apiVersion;
		}

		public String getOsVersion() {
			return osVersion;
		}

		public void setOsVersion(String osVersion) {
			this.osVersion = osVersion;
		}

		public String getModel() {
			return model;
		}

		public void setModel(String model) {
			this.model = model;
		}

		public String getSerial() {
			return serial;
		}

		public void setSerial(String serial) {
			this.serial = serial;
		}

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public long getOfflineTime() {
			return offlineTime;
		}

		public void setOfflineTime(long offlineTime) {
			this.offlineTime = offlineTime;
		}

	}

	public static class UserSettings {
		private int allowAtt;
		private int allowGreet;
		private int friendsVerify;

		public int getAllowAtt() {
			return allowAtt;
		}

		public int getAllowGreet() {
			return allowGreet;
		}

		public int getFriendsVerify() {
			return friendsVerify;
		}

		public void setAllowAtt(int allowAtt) {
			this.allowAtt = allowAtt;
		}

		public void setAllowGreet(int allowGreet) {
			this.allowGreet = allowGreet;
		}

		public void setFriendsVerify(int friendsVerify) {
			this.friendsVerify = friendsVerify;
		}

		public static DBObject getDefault() {
			// UserSettings settings = new UserSettings();
			// settings.setAllowAtt(1);// 允许关注
			// settings.setAllowGreet(1);// 允许打招呼
			// settings.setFriendsVerify(1);// 加好友需验证
			DBObject dbObj = new BasicDBObject();
			dbObj.put("allowAtt", 1);// 允许关注
			dbObj.put("allowGreet", 1);// 允许打招呼
			dbObj.put("friendsVerify", 0);// 加好友不需要验证
			return dbObj;
		}
	}

	/**
	 * 坐标
	 * 
	 * @author luorc@www.youjob.co
	 *
	 */
	public static class Loc {
		public Loc() {
			super();
		}

		public Loc(double lng, double lat) {
			super();
			this.lng = lng;
			this.lat = lat;
		}

		private double lng;// longitude
		private double lat;// latitude

		public double getLng() {
			return lng;
		}

		public void setLng(double lng) {
			this.lng = lng;
		}

		public double getLat() {
			return lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

	}

	// public static String buildUserKey(String telephone) {
	// return DigestUtils.md5Hex(DigestUtils.md5Hex(telephone));
	// }
}
