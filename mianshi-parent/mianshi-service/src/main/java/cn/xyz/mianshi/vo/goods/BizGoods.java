package cn.xyz.mianshi.vo.goods;

public class BizGoods {
	private int id;// Id
	private String imgUrl;// 图片地址
	private String name;// 名称
	private String desc;// 说明
	private int tlong;// 时长/天
	private int price;// 虚拟币数量

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getTlong() {
		return tlong;
	}

	public void setTlong(int tlong) {
		this.tlong = tlong;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
}
