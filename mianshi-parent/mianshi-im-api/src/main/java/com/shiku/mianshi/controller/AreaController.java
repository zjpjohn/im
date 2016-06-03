package com.shiku.mianshi.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shiku.mianshi.wgProperties;

import cn.xyz.commons.utils.ESBUtil;
import cn.xyz.commons.utils.HttpURLUtil;

/**
 * 省市区小区查询
 * 
 * Author: chais
 *
 */
@Controller
@RequestMapping("/area")
public class AreaController {
	@Autowired
	private wgProperties prpos;
	private static Logger logger = Logger.getLogger(AreaController.class); 
	
	public String getProvince() {
	        JSONObject jsonObj = new JSONObject();
	        // 省的esb码
	        String esb_code = "aesb.104.1002.01";
	        // 公共报文头信息
	        jsonObj=pubJson();
	        // 参数
	        jsonObj.put("M_ServiceCode", esb_code);
	        jsonObj.put("country", "1");
	        jsonObj.put("province", "110000");
	        // 获得报文头
	        String content = ESBUtil.getESBReqMsg("102", "104", "JSON",esb_code,jsonObj.toString());
	        // 发送报文,返回结果报文
	        return HttpURLUtil.doPOST("http://"+prpos.getHost()+":"+prpos.getPort(), content);
	}
	

	public String getCity(String citycode) {
	        JSONObject jsonObj = new JSONObject();
	        // 市的esb码
	        String esb_code = "aesb.104.1003.01";
	        // 公共报文头信息
	        jsonObj=pubJson();
	        // 参数
	        jsonObj.put("M_ServiceCode", esb_code);
	        jsonObj.put("country", "1");
	        jsonObj.put("province", "110000");
	        jsonObj.put("citycode", citycode);
	        // 获得报文头
	        String content = ESBUtil.getESBReqMsg("102", "104", "JSON",esb_code,jsonObj.toString());
	        // 发送报文,返回结果报文
	        return HttpURLUtil.doPOST("http://"+prpos.getHost()+":"+prpos.getPort(), content);
	}
	

	public String getCounty(String citycode) {
	        JSONObject jsonObj = new JSONObject();
	        // 区县的esb码
	        String esb_code = "aesb.104.1004.01";
	        // 公共报文头信息
	        jsonObj=pubJson();
	        // 参数
	        jsonObj.put("M_ServiceCode", esb_code);
	        jsonObj.put("country", "1");
	        jsonObj.put("province", "110000");
	        jsonObj.put("citycode", citycode);
	        // 获得报文头
	        String content = ESBUtil.getESBReqMsg("102", "104", "JSON",esb_code,jsonObj.toString());
	        // 发送报文,返回结果报文
	        return HttpURLUtil.doPOST("http://"+prpos.getHost()+":"+prpos.getPort(), content);
	}
	
	public String getVillage(String citycode,String areacode) {
			JSONObject jsonObj = new JSONObject();
	        // 小区的esb码
	        String esb_code = "aesb.104.1005.01";
	        // 公共报文头信息
	        jsonObj=pubJson();
	        // 参数
	        jsonObj.put("M_ServiceCode", esb_code);
	        jsonObj.put("country", "1");
	        jsonObj.put("province", "110000");
	        jsonObj.put("citycode", citycode);
	        jsonObj.put("areacode", areacode);
	        // 获得报文头
	        String content = ESBUtil.getESBReqMsg("102", "104", "JSON",esb_code,jsonObj.toString());
	        // 发送报文,返回结果报文
	        return HttpURLUtil.doPOST("http://"+prpos.getHost()+":"+prpos.getPort(), content);
	}
	
	/**
	 * 省市区小区ztree树的json字符串
	 * 
	 * Author: chais
	 *
	 */
	@RequestMapping(value = "/getArea",produces = {"text/json;charset=UTF-8"})
	public void getArea(HttpServletResponse response) {
		String county = getCounty("110100");
		String areaname = JSONObject.parseObject(county).getString("areaname");
        String areacode = JSONObject.parseObject(county).getString("areacode");
        JSONArray areanameArray = JSONArray.parseArray(areaname);
        JSONArray areacodeArray = JSONArray.parseArray(areacode);
        
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObj.put("id", "110100");
    	jsonObj.put("pId", "0");
    	jsonObj.put("name", "北京市");
    	jsonObj.put("open", true);
    	jsonArray.add(jsonObj);
        for (int i = 0; i < areacodeArray.size(); i++) {
        	JSONObject jsonObj1 = new JSONObject();
        	jsonObj1.put("id", areacodeArray.get(i));
        	jsonObj1.put("pId", "110100");
        	jsonObj1.put("name", areanameArray.get(i));
        	jsonArray.add(jsonObj1);
        	String village = getVillage("110100",areacodeArray.get(i).toString());
        	String communityname = JSONObject.parseObject(village).getString("communityname");
	        String communitycode = JSONObject.parseObject(village).getString("communitycode");
	        JSONArray communitynameArray = JSONArray.parseArray(communityname);
	        JSONArray communitycodeArray = JSONArray.parseArray(communitycode);
	        if(communitycodeArray!=null){
	        	for (int j = 0; j < communitycodeArray.size(); j++) {
	        		JSONObject jsonObj2 = new JSONObject();
	        		jsonObj2.put("id", communitycodeArray.get(j));
	        		jsonObj2.put("pId", areacodeArray.get(i));
	        		jsonObj2.put("name", communitynameArray.get(j));
		        	jsonArray.add(jsonObj2);
				}
	        }
        }
        logger.debug("省市区小区树json："+jsonArray);
        try {
        	response.setContentType("text/html;charset=UTF-8"); 
			response.getWriter().print(jsonArray);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 /**
     * 组装esb请求的公共报文头
     * 
     * @return
     * @throws Exception
     */
    public JSONObject pubJson() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("M_PackageType", "JSON");
        jsonObj.put("M_ServicerNo", "C3HE");
        jsonObj.put("M_CallMethod", "1");
        jsonObj.put("M_MesgId", "000100010002");

        jsonObj.put("channelcode", "021");
        jsonObj.put("channeldate", "20150529");
        jsonObj.put("channeltime", "151708");
        jsonObj.put("channelserno", "99000000099");
        return jsonObj;
    }
}
