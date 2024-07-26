package com.ly.gpt.generater.img.spark;


import com.ly.gpt.generater.img.Text2PhotoOutput;
import okhttp3.*;

import java.util.List;

public class SparkText2PhotoOutput implements Text2PhotoOutput {


    static MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    private String prompt;

    private String status;
    private String error;


    /**
     * base64编码字符串，需要把字符串转换成png图片
     */
    private List<String> images;



    public void setImages(List<String> images) {
        this.images = images;
    }

    public static MediaType getMediaType() {
        return mediaType;
    }

    public static void setMediaType(MediaType mediaType) {
        SparkText2PhotoOutput.mediaType = mediaType;
    }


    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }



    @Override
    public List<String> getPhotos() {
        return images;
    }


}
