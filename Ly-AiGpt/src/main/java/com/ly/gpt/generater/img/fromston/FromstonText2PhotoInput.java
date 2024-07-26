package com.ly.gpt.generater.img.fromston;


import cn.hutool.core.util.StrUtil;
import com.ly.gpt.generater.img.Text2PhotoInput;

public class FromstonText2PhotoInput implements Text2PhotoInput {

    private String prompt;

    private int height = 512;

    private int width = 512;

    private int size = 1;

    public FromstonText2PhotoInput(String prompt) {
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
        if (height < 1) {
            throw new IllegalArgumentException();
        }
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        if (width < 1) {
            throw new IllegalArgumentException();
        }
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

}
