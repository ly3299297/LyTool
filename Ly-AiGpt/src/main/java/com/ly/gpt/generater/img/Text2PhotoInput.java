package com.ly.gpt.generater.img;


import com.ly.gpt.generater.ContentInput;

public interface Text2PhotoInput extends ContentInput {

    String getPrompt();

    default int getHeight() {
        return 1024;
    }

    default int getWidth() {
        return 1024;
    }

    default int getSize() {
        return 1;
    }

//    private String prompt;
//    private String outputFormat;
//    private String negativePrompt;
//    private Integer width = 512;
//    private Integer height = 512;
//    private Integer imgCount = 4;

}
