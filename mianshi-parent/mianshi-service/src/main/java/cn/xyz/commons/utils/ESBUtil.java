package cn.xyz.commons.utils;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;


/**
 *
 */
public class ESBUtil {

	private static Logger logger = Logger.getLogger(ESBUtil.class); 

    private ESBUtil() {
    }

    /**
     * 获取ESB请求报文
     * 
     * @param cusServ
     *            服务的消费者
     * @param proServ
     *            服务的提供者
     * @param serviceCode
     *            服务编码
     * @return
     */
    public static String getESBReqMsg(String cusServ, String proServ, String packageType, String serviceCode, String content) {
        ESBMsg esbMsg = new ESBMsg(cusServ, proServ, packageType, serviceCode, "1", content);
        return esbMsg.getMsg();
    }

    /**
     * 获取ESB响应报文
     * 
     * @param cusServ
     *            服务的消费者
     * @param proServ
     *            服务的提供者
     * @param serviceCode
     *            服务编码
     * @return
     */
    public static String getESBRspMsg(String cusServ, String proServ, String packageType, String serviceCode, String content) {
        ESBMsg esbMsg = new ESBMsg(cusServ, proServ, packageType, serviceCode, "2", content);
        return esbMsg.getMsg();
    }

    /**
     * 发送物管ESB消息，并返回响应内容
     * 
     * @param cusServ
     *            消费服务编码
     * @param serviceCode
     *            服务码
     * @param paramMap
     *            报文中，自定义的参数信息
     * @return
     */
    public static String sendWGReqESBMsg(String customServiceCode, String serviceNo, Map<String, String> paramMap) {
        logger.debug("开始发送物管ESB报文");
        JSONObject param = new JSONObject();
        String sysDate = DateUtilWG.getSimpleStringDate(new Date());
        String sysTime = DateUtilWG.getFullStringDate(new Date());
        String simpleTime = DateUtilWG.getSimpleStringTime(new Date());
        // JSON报文的公共字段部分
        param.put(ESBConstants.WG_COMM_M_SERVICE_NO, ESBConstants.WG_SERVICE_NO); // 物管需要的固定编码
        param.put(ESBConstants.WG_COMM_M_PACKAGE_TYPE, ESBConstants.ESB_PACKAGE_TYPE_JSON);
        param.put(ESBConstants.WG_COMM_M_SERVICE_CODE, serviceNo); // 服务代码，服务的唯一标识
        param.put(ESBConstants.WG_COMM_M_CALL_METHOD, ESBConstants.WG_ESB_M_CALL_METHOD_1);
        param.put(ESBConstants.WG_COMM_M_MESG_ID, sysTime);
        param.put(ESBConstants.WG_COMM_M_CHANNEL_CODE, ESBConstants.WG_CHANNEL_CODE); // 渠道代码固定
        param.put(ESBConstants.WG_COMM_M_CHANNEL_DATE, sysDate);
        param.put(ESBConstants.WG_COMM_M_CHANNEL_TIME, simpleTime);
        param.put(ESBConstants.WG_COMM_M_CHANNEL_SERNO, sysTime);
        param.put(ESBConstants.WG_COMM_M_ROLE_ID, ESBConstants.WG_ROLE_ID); // 用户id固定

        param.putAll(paramMap);
        String jsonMsg = param.toString(); // JSON报文体
        logger.debug("要发送的JSON报文体内容为：" + jsonMsg);
        String response = HttpURLUtil.doPOST("http://172.16.100.50:30001", jsonMsg);//http://172.16.100.50:30001物管系统接口WG_ESB_URL
        logger.debug("响应成功，返回的内容为：" + response);
        return response;
    }
    
    /**
     * 发送物管ESB消息，并返回响应内容
     * 
     * @param cusServ
     *            消费服务编码
     * @param serviceCode
     *            服务码
     * @param paramMap
     *            报文中，自定义的参数信息
     * @return
     */
    public static String sendWGESBMsg(String customServiceCode, String serviceNo, Map<String, String> paramMap) {
        logger.debug("开始发送物管ESB报文");
        JSONObject param = new JSONObject();
        String sysDate = DateUtilWG.getSimpleStringDate(new Date());
        String sysTime = DateUtilWG.getFullStringDate(new Date());
        String simpleTime = DateUtilWG.getSimpleStringTime(new Date());
        // JSON报文的公共字段部分
        param.put(ESBConstants.WG_COMM_M_SERVICE_NO, "C3HE"); // 物管需要的固定编码
        param.put(ESBConstants.WG_COMM_M_PACKAGE_TYPE, ESBConstants.ESB_PACKAGE_TYPE_JSON);
        param.put(ESBConstants.WG_COMM_M_SERVICE_CODE, serviceNo); // 服务代码，服务的唯一标识
        param.put(ESBConstants.WG_COMM_M_CALL_METHOD, ESBConstants.WG_ESB_M_CALL_METHOD_1);
        param.put(ESBConstants.WG_COMM_M_MESG_ID, sysTime);
        param.put(ESBConstants.WG_COMM_M_CHANNEL_CODE, "POS"); // 渠道代码固定
        param.put(ESBConstants.WG_COMM_M_CHANNEL_DATE, sysDate);
        param.put(ESBConstants.WG_COMM_M_CHANNEL_TIME, simpleTime);
        param.put(ESBConstants.WG_COMM_M_CHANNEL_SERNO, sysTime);
        param.put(ESBConstants.WG_COMM_M_ROLE_ID, ESBConstants.WG_ROLE_ID); // 用户id固定

        param.putAll(paramMap);
        String jsonMsg = param.toString(); // JSON报文体
        //模拟ESB报文信息：长度8+141定长+8安全信息长度+安全信息(暂无)+json报文
    	String lenHead,safelen,allLen,allmsg;
		int d;
		lenHead="{H:01"
				+ customServiceCode
				+ "       104       JSON  "
				+ serviceNo
				+ "              "
				+ sysTime+"1234311310"
				+ "          1343112311"
				+ "          312                    }  ";
		safelen="00000000";
		d=lenHead.length()+safelen.length()+jsonMsg.length();
    	allLen="".format("%08d",d)+"";
    	allmsg=allLen+lenHead+safelen+jsonMsg;
    	System.out.println(allmsg);
        logger.debug("要发送的JSON报文体内容为：" + allmsg);
        String response = HttpURLUtil.doPOST("http://172.16.100.21:8877", allmsg);
        logger.debug("响应成功，返回的内容为：" + response);
        return response;
    }

    public static String getESBJsonStr() {

        JSONObject param = new JSONObject();
        String sysDate = DateUtilWG.getSimpleStringDate(new Date());
        String sysTime = DateUtilWG.getFullStringDate(new Date());
        String simpleTime = DateUtilWG.getSimpleStringTime(new Date());

        // head1
        // param.put("M_ServicerNo","IHES");
        // param.put("M_PackageType","JSON");
        // param.put("M_ServiceCode","gbpp.ihe.1105.01");
        param.put("M_MesgSndDate", sysDate);
        param.put("M_MesgSndTime", simpleTime);
        param.put("M_MesgId", sysTime);
        param.put("M_MesgRefId", sysTime);
        param.put("M_MesgPriority", "3");
        param.put("M_Direction", "2");
        param.put("M_CallMethod", "1");
        // head2
        param.put("channelcode", "005");
        param.put("channeldate", sysDate);
        param.put("channeltime", simpleTime);
        param.put("channelserno", sysTime);
        // param.put("userid","11");
        // param.put("username","用户名称");
        param.put("roleid", "2");
        param.put("_opertype_", "4");
        // param.put("_currpage_","1");
        param.put("_pagenum_", "10");

        return param.toString();
    }

    public static String sendESBRequest(String cusServ, String proServ, String serviceNo, String msgBody) {

        return null;
    }

    public static void main(String[] args) {
        System.out.println(getESBReqMsg("102", "104", "JSON", "aesb.103.1000.01", "{'param':'liruifeng'}"));
    }
}
