package com.ly.gpt.generater.text;

public interface Text2TextStreamObserver   {

    void onBegin();

    void onNext(String data);

    void onEnd();

    void onFailure(Throwable throwable);

}
