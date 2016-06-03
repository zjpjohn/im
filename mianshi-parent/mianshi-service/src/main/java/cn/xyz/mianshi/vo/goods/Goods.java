package cn.xyz.mianshi.vo.goods;

public class Goods {
	public static interface CATEGORY {
		public static final int PAY_GOODS = 1;
		public static final int BIZ_GOODS = 2;
	}

	private int id;// 产品Id
	private int categoryId;// 产品类别
	private String imgUrl;// 产品地址
	private String name;// 产品名称
	private String desc;// 产品说明
	private int price;// 根据产品类别确定：虚拟币、人民币
	private int value;// 根据产品类别确定：虚拟币数量、时长/天、视频面试服务次数
	private int status;// 状态

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
