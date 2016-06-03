package cn.xyz.commons.utils;

/**
 * 
 */
public class ESBConstants {

	private ESBConstants() {
	};

	/**
	 * 物管报文体中，公共字段 开始
	 */
	/*
	 * 请求报文中公共参数
	 */
	public static final String WG_COMM_M_SERVICE_CODE = "M_ServiceCode";
	public static final String WG_COMM_M_PACKAGE_TYPE = "M_PackageType";
	public static final String WG_COMM_M_SERVICE_NO = "M_ServicerNo";
	public static final String WG_COMM_M_CALL_METHOD = "M_CallMethod";
	public static final String WG_COMM_M_MESG_ID = "M_MesgId";
	public static final String WG_COMM_M_CHANNEL_CODE = "channelcode";
	public static final String WG_COMM_M_CHANNEL_DATE = "channeldate";
	public static final String WG_COMM_M_CHANNEL_TIME = "channeltime";
	public static final String WG_COMM_M_CHANNEL_SERNO = "channelserno";
	public static final String WG_COMM_M_USER_ID = "uid";
	public static final String WG_COMM_M_USER_NAME = "uname";
	public static final String WG_COMM_M_ROLE_ID = "roleid";
	public static final String WG_COMM_M_TERMINAL_TYPE = "terminaltype";

	/*
	 * 请求报文中分页信息参数
	 */
	public static final String WG_COMM_M_OPER_TYPE = "_opertype_";
	public static final String WG_COMM_M_CURR_PAGE = "_currpage_";
	public static final String WG_COMM_M_PAGE_NUM = "_pagenum_";

	/*
	 * 应答报文中公共参数
	 */
	public static final String WG_COMM_DEAL_CODE = "dealcode";
	public static final String WG_COMM_DEAL_MSG = "dealmsg";
	public static final String WG_COMM_TOTAL_ROWNUM = "totalrownum";
	public static final String WG_COMM_TOTAL_PAGE_NUM = "totalpagenum";
	public static final String WG_COMM_NOW_PAGE_NUM = "nowpagenum";
	public static final String WG_COMM_PAGE_ROW_NUM = "pagerownum";

	public static final String WG_SERVICE_NO = "C3HE"; // 物管的固定的服务方系统编号
	public static final String WG_CHANNEL_CODE = "005"; // 物管的固定的渠道编码
	public static final String WG_ROLE_ID = "2"; // 物管的固定的角色ID

	/*
	 * ESB的调用方式
	 */
	public static final String WG_ESB_M_CALL_METHOD_1 = "1"; // 同步调用
	public static final String WG_ESB_M_CALL_METHOD_2 = "2"; // 异步调用
	public static final String WG_ESB_M_CALL_METHOD_3 = "3"; // 同步转异步调用
	/**
	 * 物管报文体中，公共部分 结束
	 */

	/**
	 * ESB报文的类型
	 */
	public static final String ESB_PACKAGE_TYPE_JSON = "JSON";
	public static final String ESB_PACKAGE_TYPE_XML = "XML";
	/**
	 * ESB的调用方式
	 */
	public static final String ESB_M_CALL_METHOD_1 = "1"; // 同步调用
	public static final String ESB_M_CALL_METHOD_2 = "2"; // 异步调用
	public static final String ESB_M_CALL_METHOD_3 = "3"; // 同步转异步调用

	/**
	 * @Fields ESB的服务地址的KEY
	 */
	public static final String ESB_HOST_NAME = "esb_host_ip";
}
