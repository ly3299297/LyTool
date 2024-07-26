package com.ly.gpt.generater.img;

import com.ly.gpt.generater.ContentGenerater;

/**
 * 图像生成器
 * 
 *
 *
 */
public interface PhotoGenerater<I extends Text2PhotoInput, O extends Text2PhotoOutput> extends ContentGenerater {

    O text2Photo(I input);


    //O imageGet(List<String> imgTaskIds);

}
