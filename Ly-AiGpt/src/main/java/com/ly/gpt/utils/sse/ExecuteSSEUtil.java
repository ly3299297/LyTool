package com.ly.gpt.utils.sse;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

@Slf4j
public class ExecuteSSEUtil {

    public static void executeSSE(Request request, SSEListener eventSourceListener, OkHttpClient client) throws Exception {

        EventSource.Factory factory = EventSources.createFactory(client);
        //创建事件

        factory.newEventSource(request, eventSourceListener);
        //eventSourceListener.getCountDownLatch().await();
    }
}
