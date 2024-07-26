package com.ly.gpt.enums;



public enum AiImgTypeEnum {


    LEAP_IMG("leap_img", "leapAIHandler","leap ai","leap"),
    FROMSTON_IMG("fromston_img", "fromstonAIHandler","fromston ai","6pen");


    private String serviceType;
    private String company;
    private String method;
    private String remark;

    AiImgTypeEnum() {
    }

    AiImgTypeEnum(String serviceType, String method, String remark, String company) {
        this.serviceType = serviceType;
        this.method = method;
        this.remark = remark;
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static String getMethodByType(String event) {
        for (AiImgTypeEnum eventIdentifyEnum : AiImgTypeEnum.values()) {
            if (event.equals(eventIdentifyEnum.serviceType)) {
                return eventIdentifyEnum.method;
            }
        }

        return null;
    }



}
