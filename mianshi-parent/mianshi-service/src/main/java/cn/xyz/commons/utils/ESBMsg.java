package cn.xyz.commons.utils;

import org.apache.commons.lang3.StringUtils;

/**
 *物管信息处理
 *
 */
public class ESBMsg {

    private String M_XMLVer;
    private String M_CustomerNo;
    private String M_ServicerNo;
    private String M_PackageType;
    private String M_ServiceCode;
    private String M_MesgSndDate;
    private String M_MesgSndTime;
    private String M_MesgId;
    private String M_MesgRefId;
    private String M_MesgPriority;
    private String M_Direction;
    private String M_CallMethod;
    private String M_Reserve;
    private String content;

    ESBMsg(String M_CustomerNo, String M_ServicerNo, String M_PackageType, String M_ServiceCode, String M_Direction,
            String content) {
        this(null, M_CustomerNo, M_ServicerNo, M_PackageType, M_ServiceCode, null, null, null, null, null, M_Direction,
                null, null, content);
    }

    ESBMsg(String M_XMLVer, String M_CustomerNo, String M_ServicerNo, String M_PackageType, String M_ServiceCode,
            String M_MesgSndDate, String M_MesgSndTime, String M_MesgId, String M_MesgRefId, String M_MesgPriority,
            String M_Direction, String M_CallMethod, String M_Reserve, String content) {
        if (StringUtils.isBlank(M_XMLVer)) {
            this.M_XMLVer = "01";
        } else {
            this.M_XMLVer = M_XMLVer;
        }
        this.M_CustomerNo = handle(M_CustomerNo, 10);
        this.M_ServicerNo = handle(M_ServicerNo, 10);
        this.M_PackageType = handle(M_PackageType, 6);
        this.M_ServiceCode = handle(M_ServiceCode, 30);

        if (StringUtils.isBlank(M_MesgSndDate)) {
            this.M_MesgSndDate = handle("", 8);
        } else {
            this.M_MesgSndDate = handle(M_MesgSndDate, 8);
        }

        if (StringUtils.isBlank(M_MesgSndTime)) {
            this.M_MesgSndTime = handle("", 6);
        } else {
            this.M_MesgSndTime = handle(M_MesgSndTime, 6);
        }

        if (StringUtils.isBlank(M_MesgId)) {
            this.M_MesgId = handle("", 20);
        } else {
            this.M_MesgId = handle(M_MesgId, 20);
        }

        if (StringUtils.isBlank(M_MesgRefId)) {
            this.M_MesgRefId = handle("", 20);
        } else {
            this.M_MesgRefId = handle(M_MesgRefId, 20);
        }

        if (StringUtils.isBlank(M_MesgPriority)) {
            this.M_MesgPriority = "3";
        } else {
            this.M_MesgPriority = M_MesgPriority;
        }

        this.M_Direction = M_Direction;

        if (StringUtils.isBlank(M_CallMethod)) {
            this.M_CallMethod = "2";
        } else {
            this.M_CallMethod = M_CallMethod;
        }

        this.M_Reserve = handle("", 20);
        this.content = content;
    }

    /**
     * 获取报文内容
     * 
     * @return
     */
    String getMsg() {
        StringBuilder msg = new StringBuilder();
        msg.append("{H:").append(this.M_XMLVer).append(this.M_CustomerNo).append(this.M_ServicerNo)
                .append(this.M_PackageType)
                .append(this.M_ServiceCode).
                append(this.M_MesgSndDate).append(this.M_MesgSndTime).append(this.M_MesgId).append(this.M_MesgRefId)
                .append(this.M_MesgPriority)
                .
                append(this.M_Direction).append(this.M_CallMethod).append(this.M_Reserve).append("}  ")
                .append("00000000")
                .append(this.content);
        return "".format("%08d", msg.length()) + msg.toString();
    }

    /**
     * 末位补齐空格
     * 
     * @param str
     * @param len
     * @return
     * @throws Exception
     */
    private String handle(String str, int len) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < (len - str.length()); i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
