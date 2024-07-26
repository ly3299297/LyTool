package com.ly.gpt.generater.img.spark;


import cn.hutool.core.util.StrUtil;
import com.ly.gpt.generater.img.spark.enums.SparkImgWidthHeightEnum;
import com.ly.gpt.generater.img.Text2PhotoInput;

public class SparkText2PhotoInput implements Text2PhotoInput {

    private String prompt;

    /**
     * 禁止生成的提示词	string	字符长度0 ~ 2000的字符串
     * negative_prompt
     */
    private String negativePrompt;

    private int height = 512;

    private int width = 512;


    /**
     * 一次生成的图片数量	int	1: 生成1张图片2: 生成2张图片3: 生成3张图片4: 生成4张图片
     * img_count
     */
    private int size = 1;

    public SparkText2PhotoInput(String prompt) {
        if (StrUtil.isBlank(prompt)) {
            throw new IllegalArgumentException();
        }
        this.prompt = prompt;
    }

    @Override
    public String getPrompt() {
        return prompt;
    }


    @Override
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        Integer heightByWidth = SparkImgWidthHeightEnum.getHeightByWidth(this.width);
        if(heightByWidth.equals(height)){
            this.height = height;
        }else{
            throw new IllegalArgumentException("宽高不符合规范");
        }

    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size < 1 || size > 20) {
            throw new IllegalArgumentException();
        }
        this.size = size;
    }

    public String getNegativePrompt() {
        return negativePrompt;
    }

    public void setNegativePrompt(String negativePrompt) {
        this.negativePrompt = negativePrompt;
    }
}
