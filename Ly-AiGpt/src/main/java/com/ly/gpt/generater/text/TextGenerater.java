package com.ly.gpt.generater.text;

import com.ly.gpt.generater.ContentGenerater;

/**
 * 文本生成器
 * 
 *
 *
 */
public interface TextGenerater<I extends Text2TextInput, O extends Text2TextOutput> extends ContentGenerater {

    //O text2Text(I input);

    void text2TextSync(I input,Text2TextObserver observer);


    /**
     *
     * @param input
     * 输入
     * @param streamObserver
     * 流式返回观察者
     */
    void text2TextStream(I input, Text2TextStreamObserver streamObserver);


}
