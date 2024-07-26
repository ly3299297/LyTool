package com.ly.gpt.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ai_qa_log
 */
@Data
public class AiQaLog implements Serializable {
    private Long id;

    /**
     * 问题
     */
    private String question;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 请求id
     */
    private String requestId;

    /**
     * ai接口类型
     */
    private String serviceType;

    /**
     * 内容
     */
    private String content;

    private Date createDate;

    private static final long serialVersionUID = 1L;

    public AiQaLog() {

    }

    public AiQaLog(String question, String username, String serviceType, String content, Date createDate) {
        this.question = question;
        this.username = username;
        this.serviceType = serviceType;
        this.content = content;
        this.createDate = createDate;
    }
}