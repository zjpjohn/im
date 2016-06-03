package cn.xyz.commons.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


/**
 * Http工具类
 * 
 */
public class HttpURLUtil {

    /**
     * @Fields logger : 日志服务
     */
	private static Logger logger = Logger.getLogger(HttpURLUtil.class); 
    /**
     * @Fields DEFAULT_ENCODING : 统一编码
     */
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String HTTP_PARAMS_SPLITOR = "&";
    private static final String HTTP_PARAM_VALUE_SPLITOR = "=";
    private static final String HTTP_URI_PARAMS_SPLITOR = "?";

    /**
     * 发送文件请求
     * 
     * @param urlStr
     * @param paramMap
     * @return
     * @throws Exception
     */
    public static String doFilePOST(String URI, Map<String, String> paramMap) {
        Exception ex = null;
        CloseableHttpClient client = HttpClients.createDefault();
        String result = "";
        MultipartEntity entity = new MultipartEntity();
        HttpPost post = new HttpPost(URI);
        if (paramMap != null) {
            Iterator<Entry<String, String>> iter = paramMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, String> entry = iter.next();
                FileBody fileBody = new FileBody(new File(entry.getValue()));
                entity.addPart("file", fileBody);
            }
        }
        post.setEntity(entity);
        try {
            logger.debug("发送上传文件的POST请求，其中URL为：" + URI + "，参数为：" + paramMap);
            result = client.execute(post, RESPONSE_HANDLER);
            logger.debug("发送上传文件的POST请求成功，响应内容为：" + result);
            return result;
        } catch (ClientProtocolException e) {
            ex = new Exception("发送上传文件的POST请求失败：其中URL为：" + URI + "，参数为：" + paramMap, e);
        } catch (IOException e) {
            ex = new Exception("发送上传文件的POST请求失败：其中URL为：" + URI + "，参数为：" + paramMap, e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                ex = new Exception("发送上传文件的POST请求失败：其中URL为：" + URI + "，参数为：" + paramMap, e);
            }
        }
		return "发送上传文件的POST请求失败：其中URL为：" + URI + "，参数为：" + paramMap;

    }

    /**
     * 发送form表单请求，并返回响应内容。
     * 
     * @param URI
     *            表单请求地址
     * @param params
     *            表单参数
     * @return
     *         响应内容
     */
    public static String sendPostRequestByForm(String URI, Map<String, String> params) {
        return doPOST(URI, params);
    }

    /**
     * 发送httpPOST请求，并返回响应内容。
     * 
     * @param URI
     *            请求地址
     * @param HTTPPOSTMsgBody
     *            请求报文体
     * @return
     *         响应内容
     */
    public static String doPOST(String URI, String HTTPPOSTMsgBody) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPOST = new HttpPost(URI);
        httpPOST.setEntity(new StringEntity(HTTPPOSTMsgBody, DEFAULT_ENCODING));
        logger.debug("发送POST请求，其中URL为：" + URI + "，参数为：" + HTTPPOSTMsgBody);
        Exception ex = null;
        try {
            String result = client.execute(httpPOST, RESPONSE_HANDLER);
            logger.debug("POST请求成功返回，响应内容为：" + result);
            return result;
        } catch (ClientProtocolException e) {
            ex = new Exception("发送POST请求失败：其中URL为：" + URI + "，参数为：" + HTTPPOSTMsgBody, e);
        } catch (IOException e) {
            ex = new Exception("发送POST请求失败：其中URL为：" + URI + "，参数为：" + HTTPPOSTMsgBody, e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                ex = new Exception("发送POST请求失败：其中URL为：" + URI + "，参数为：" + HTTPPOSTMsgBody, e);
            }
        }
        return "发送POST请求失败：其中URL为：" + URI + "，参数为：" + HTTPPOSTMsgBody;
    }

    /**
     * 发送http POST请求，并返回响应内容。
     * 
     * @param URI
     *            请求地址
     * @param paramMap
     *            请求参数
     * @return
     *         响应内容
     */
    public static String doPOST(String URI, Map<String, String> paramMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        Exception ex = null;
        try {
            HttpPost httpPOST = new HttpPost(URI);
            List<NameValuePair> pairList = new ArrayList<NameValuePair>();
            // HTTP POST报文体中，参数的处理
            if ((null != paramMap) && !paramMap.isEmpty()) {
                for (String paramKey : paramMap.keySet()) {
                    pairList.add(new BasicNameValuePair(paramKey, paramMap.get(paramKey)));
                }
            }
            String result = "";
            httpPOST.setEntity(new UrlEncodedFormEntity(pairList, DEFAULT_ENCODING));
            logger.debug("发送POST请求，其中URL为：" + URI + "，参数为：" + paramMap);
            result = client.execute(httpPOST, RESPONSE_HANDLER);
           
            logger.info("POST请求成功，响应内容为：" + result);
            return result;
        } catch (UnsupportedEncodingException e) {
            ex = new Exception("发送POST请求失败，参数编码错误：其中URL为：" + URI + "，参数为：" + paramMap, e);
        } catch (ClientProtocolException e) {
            ex = new Exception("发送POST请求失败：其中URL为：" + URI + "，参数为：" + paramMap, e);
        } catch (IOException e) {
            ex = new Exception("发送POST请求失败：其中URL为：" + URI + "，参数为：" + paramMap, e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                ex = new Exception("发送POST请求失败：其中URL为：" + URI + "，参数为：" + paramMap, e);
            }
        }
        return "发送POST请求失败：其中URL为：" + URI + "，参数为：" + paramMap;
    }

    /**
     * 发送http Get请求，并返回响应内容。
     * 
     * @param URI
     *            请求地址
     * @param map
     *            请求参数，key,value形式
     * @return
     *         get请求响应的内容
     */
    public static String doGet(String URI, Map<String, String> map) {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet((URIPreHandle(URI, map, null)));
            logger.debug("发送GET请求，其中URI为：" + URI + "，参数为：" + map);
            String result = client.execute(httpGet, RESPONSE_HANDLER);
            logger.debug("GET请求成功，响应内容为：" + result);
            return result;
        } catch (ClientProtocolException e) {
        	return "发送GET请求失败";
        } catch (IOException e) {
        	return "发送GET请求失败";
        } finally {
            try {
                client.close();
            } catch (IOException e) {
            	return "发送GET请求失败";
            }
        }
    }

    /**
     * 对URI的预处理。
     * 
     * @param URI
     *            待处理的URI
     * @param paramMap
     *            待处理的参数，可以为null
     * @param charset
     *            指定的编码格式，如果为空，则默认UTF-8
     * @return
     *         处理完成的URI
     * @throws UnsupportedEncodingException
     */
    private static String URIPreHandle(String URI, Map<String, String> paramMap, String charset)
            throws UnsupportedEncodingException {
        String result = URI.endsWith(HTTP_URI_PARAMS_SPLITOR) ? URI : (URI + HTTP_URI_PARAMS_SPLITOR);
        if (null != paramMap) {
            result += buildURIParam(paramMap, charset);
        }
        return result;
    }

    /**
     * @Fields RESPONSE_HANDLER : HTTP请求处理器。
     *         目前只实现对200请求的处理。
     */
    private static final ResponseHandler<String> RESPONSE_HANDLER = new ResponseHandler<String>() {

        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException,
                IOException {
            int status = response.getStatusLine().getStatusCode();
            // TODO:无法处理302的情况
            if (status == 200) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity, DEFAULT_ENCODING) : "";
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        }
    };

    /**
     * 将Map中的key-value形式的参数，用指定编码转换为URI后的请求参数。
     * 
     * @param params
     *            参数的map，key为参数名称，value为参数值。
     * @param charset
     *            参数使用的指定编码方式，默认为UTF-8。
     * @return
     *         处理完的参数字符串。
     *         当参数为空时，返回空字符串。
     * @throws UnsupportedEncodingException
     */
    private static String buildURIParam(Map<String, String> params, String charset) throws UnsupportedEncodingException {
        if ((params == null) || params.isEmpty()) {
            return "";
        }
        if (StringUtils.isBlank(charset)) {
            charset = DEFAULT_ENCODING;
        }
        StringBuilder query = new StringBuilder();
        for (String paramKey : params.keySet()) {
            if (StringUtils.isBlank(paramKey)) {
                continue;
            }
            String paramValue = params.get(paramKey);
            query.append(paramKey).append(HTTP_PARAM_VALUE_SPLITOR)
                    .append(StringUtils.isBlank(paramValue) ? "" : URLEncoder.encode(paramValue, charset)).
                    append(HTTP_PARAMS_SPLITOR);
        }
        if (query.length() > 0) {
            return query.substring(0, query.length() - 1);
        } else {
            return "";
        }
    }
}
