package com.ly.gpt.generater.img.spark.dto;


import lombok.Data;


@Data
public class SparkImgMessage {


    private  String role;  //角色  system user ,assistant.
    private  String content; // 图片base64编码字符串   .

    public SparkImgMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public SparkImgMessage() {
    }


}
