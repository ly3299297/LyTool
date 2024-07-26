package com.ly.gpt.enums;


public enum AiChatTypeEnum {


    DASHSCOPE_CHAT("dashscope_chat", "chatDashscopeHandler","通义千问","阿里"),
    BAICHUAN_CHAT("baichuan_chat", "chatBaichuanHandler","百川","百川"),
    MOONSHOT_CHAT("moonshot_chat", "chatMoonshotHandler","kimi","月之暗面") ,
    BAIDU_CHAT("baidu_chat", "chatBaiduHandler","文心一言","百度") ;


    private String serviceType;
    private String company;
    private String method;
    private String remark;

    AiChatTypeEnum() {
    }

    AiChatTypeEnum(String serviceType, String method, String remark, String company) {
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
        for (AiChatTypeEnum eventIdentifyEnum : AiChatTypeEnum.values()) {
            if (event.equals(eventIdentifyEnum.serviceType)) {
                return eventIdentifyEnum.method;
            }
        }

        return null;
    }



}
