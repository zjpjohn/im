package cn.xyz.mianshi.vo;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;

/**
 * 职位模型
 * <p>
 * 聚合索引：状态、城市Id、公司Id、公司名称、公司行业、公司性质、公司规模、职位名称、工作经验、学历、薪水范围、活跃度
 * <p>
 * 单列索引：公司Id；用户Id；刷新时间；company.id；userId；refreshTime
 * 
 * @author luorc
 *
 */
@Entity(value = "a_job", noClassnameStored = true)
@Indexes(@Index("status,cityId,co.id,co.name,co.ind,co.nature,co.scale,jobName,workExp,dip,salary,active"))
public class JobVO {

	// 职位编号
	private @Id ObjectId jobId;

	// 职位名称
	// @Indexed
	private String jobName;
	// 职位说明
	private String description;

	// 国家Id
	private int countryId;
	// 省份Id
	private int provinceId;
	// 城市Id
	// @Indexed
	private int cityId;
	// 区县Id
	private int areaId;

	// 工作经验
	// @Indexed
	private int workExp;
	// 学历
	// @Indexed
	private int dip;
	// 薪水范围
	// @Indexed
	private int salary;

	// 招聘人数
	private int vacancy;

	private String address;

	// 发布时间、创建时间
	private long publishTime;
	// 刷新时间
	private @Indexed long refreshTime;
	// 过期时间
	private long expiredTime;

	// 统计
	private @Embedded Count count = new Count();
	// 公司信息
	private Company co;
	// 职位配置
	private Profile profile;

	// 职位状态
	// @Indexed
	private int status;// 1=正常；2=暂停发 布；3=取消发布

	// 职位管理用户、职位发布用户
	// @Indexed
	private @Indexed int userId;
	private String userName;

	private int active;
	private int fnId;

	public ObjectId getJobId() {
		return jobId;
	}

	public void setJobId(ObjectId jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public int getWorkExp() {
		return workExp;
	}

	public void setWorkExp(int workExp) {
		this.workExp = workExp;
	}

	public int getDip() {
		return dip;
	}

	public void setDip(int dip) {
		this.dip = dip;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public int getVacancy() {
		return vacancy;
	}

	public void setVacancy(int vacancy) {
		this.vacancy = vacancy;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(long publishTime) {
		this.publishTime = publishTime;
	}

	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	public long getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(long expiredTime) {
		this.expiredTime = expiredTime;
	}

	public Count getCount() {
		return count;
	}

	public void setCount(Count count) {
		this.count = count;
	}

	public Company getCo() {
		return co;
	}

	public void setCo(Company co) {
		this.co = co;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getFnId() {
		return fnId;
	}

	public void setFnId(int fnId) {
		this.fnId = fnId;
	}

	public static class Company {
		// 公司编号
		private @Indexed int id;
		// 公司行业
		private int ind;
		// 公司名称
		private String name;
		// 公司性质
		private int nature;
		// 公司规模
		private int scale;

		public Company() {
			super();
		}

		public Company(CompanyVO company) {
			super();
			id = company.getId();
			name = company.getName();
			nature = company.getNatureId();
			scale = company.getScale();
			ind = company.getIndustryId();
		}

		public int getId() {
			return id;
		}

		public int getInd() {
			return ind;
		}

		public String getName() {
			return name;
		}

		public int getNature() {
			return nature;
		}

		public int getScale() {
			return scale;
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setInd(int ind) {
			this.ind = ind;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setNature(int nature) {
			this.nature = nature;
		}

		public void setScale(int scale) {
			this.scale = scale;
		}

	}

	public static class Count {
		// 活跃度=申请数+浏览数+收藏数
		private int active;
		// 申请数、收到简历数
		private int apply;
		// 浏览数
		private int browse;
		// 收藏数
		private int collect;
		// 已读简历数
		private int read;

		public int getActive() {
			return active;
		}

		public int getApply() {
			return apply;
		}

		public int getBrowse() {
			return browse;
		}

		public int getCollect() {
			return collect;
		}

		public int getRead() {
			return read;
		}

		public void setActive(int active) {
			this.active = active;
		}

		public void setApply(int apply) {
			this.apply = apply;
		}

		public void setBrowse(int browse) {
			this.browse = browse;
		}

		public void setCollect(int collect) {
			this.collect = collect;
		}

		public void setRead(int read) {
			this.read = read;
		}

	}

	public static class Profile {
		private List<Integer> examList;
		// 是否线下面试
		private int isOffline;
		// 是否视频面试
		private int isVideo;

		public List<Integer> getExamList() {
			return examList;
		}

		public void setExamList(List<Integer> examList) {
			this.examList = examList;
		}

		public int getIsOffline() {
			return isOffline;
		}

		public void setIsOffline(int isOffline) {
			this.isOffline = isOffline;
		}

		public int getIsVideo() {
			return isVideo;
		}

		public void setIsVideo(int isVideo) {
			this.isVideo = isVideo;
		}

	}
}