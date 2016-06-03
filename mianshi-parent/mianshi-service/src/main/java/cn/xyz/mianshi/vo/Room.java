package cn.xyz.mianshi.vo;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import com.alibaba.fastjson.annotation.JSONField;

@Entity(value = "shiku_room", noClassnameStored = true)
@Indexes({ @Index("userId"), @Index("jid"), @Index("userId,jid") })
public class Room {

	// 房间编号
	@Id
	private ObjectId id;

	private String jid;
	// 房间名称
	private String name;
	// 房间描述
	private String desc;
	// 房间主题
	private String subject;
	// 房间分类
	private Integer category;
	// 房间标签
	private List<String> tags;

	// 房间公告
	private Notice notice;
	// 公告列表
	private List<Notice> notices;

	// 当前成员数
	private Integer userSize;
	// 最大成员数
	private Integer maxUserSize;
	// 自己
	private Member member;
	// 成员列表
	private List<Member> members;
	
	private Integer countryId;
	private Integer provinceId;
	private Integer cityId;
	private Integer areaId;

	private Double longitude;
	private Double latitude;

	// 创建者Id
	private Integer userId;
	// 创建者昵称
	private String nickname;
	// 创建者小区Id
	private String comunityCode;
		
	// 创建时间
	private Long createTime;
	// 修改人
	private Integer modifier;
	// 修改时间
	private Long modifyTime;

	// 状态
	private Integer s;

	// 显示状态
	private String showType;
		
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notice) {
		this.notice = notice;
	}

	public List<Notice> getNotices() {
		return notices;
	}

	public void setNotices(List<Notice> notices) {
		this.notices = notices;
	}

	public Integer getUserSize() {
		return userSize;
	}

	public void setUserSize(Integer userSize) {
		this.userSize = userSize;
	}

	public Integer getMaxUserSize() {
		return maxUserSize;
	}

	public void setMaxUserSize(Integer maxUserSize) {
		this.maxUserSize = maxUserSize;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
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

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getModifier() {
		return modifier;
	}

	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}

	public Long getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Long modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Integer getS() {
		return s;
	}

	public void setS(Integer s) {
		this.s = s;
	}

	public String getComunityCode() {
		return comunityCode;
	}

	public void setComunityCode(String comunityCode) {
		this.comunityCode = comunityCode;
	}

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	@Entity(value = "shiku_room_notice")
	@Indexes({ @Index("roomId"), @Index("userId") })
	public static class Notice {
		@Id
		@JSONField(serialize = false)
		private ObjectId id;
		@JSONField(serialize = false)
		private ObjectId roomId;
		private String text;
		private String userId;
		private String nickname;
		private String time;

		public ObjectId getId() {
			return id;
		}

		public void setId(ObjectId id) {
			this.id = id;
		}

		public ObjectId getRoomId() {
			return roomId;
		}

		public void setRoomId(ObjectId roomId) {
			this.roomId = roomId;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

	}

	@Entity(value = "shiku_room_member")
	@Indexes({ @Index("roomId"), @Index("userId"), @Index("roomId,userId"),
			@Index("userId,role") })
	public static class Member {
		@Id
		@JSONField(serialize = false)
		private ObjectId id;

		// 房间Id
		@JSONField(serialize = false)
		private ObjectId roomId;

		// 成员Id
		private Integer userId;

		// 成员昵称
		private String nickname;

		// 成员角色：1=创建者、2=管理员、3=成员
		private Integer role;

		// 订阅群信息：0=否、1=是
		private Integer sub;

		// 大于当前时间时禁止发言
		private Long talkTime;

		// 最后一次互动时间
		private Long active;

		// 创建时间
		private Long createTime;

		// 修改时间
		private Long modifyTime;

		public ObjectId getId() {
			return id;
		}

		public void setId(ObjectId id) {
			this.id = id;
		}

		public ObjectId getRoomId() {
			return roomId;
		}

		public void setRoomId(ObjectId roomId) {
			this.roomId = roomId;
		}

		public Integer getUserId() {
			return userId;
		}

		public void setUserId(Integer userId) {
			this.userId = userId;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public Integer getRole() {
			return role;
		}

		public void setRole(Integer role) {
			this.role = role;
		}

		public Integer getSub() {
			return sub;
		}

		public void setSub(Integer sub) {
			this.sub = sub;
		}

		public Long getTalkTime() {
			return talkTime;
		}

		public void setTalkTime(Long talkTime) {
			this.talkTime = talkTime;
		}

		public Long getActive() {
			return active;
		}

		public void setActive(Long active) {
			this.active = active;
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

	}
}
