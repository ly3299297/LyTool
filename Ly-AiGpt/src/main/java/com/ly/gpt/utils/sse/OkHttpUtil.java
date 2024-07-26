package com.ly.gpt.utils.sse;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {
    private static OkHttpClient okHttpClient;

    public static ConnectionPool connectionPool = new ConnectionPool(10, 5, TimeUnit.MINUTES);

    public static OkHttpClient getInstance() {
        if (okHttpClient == null) { //加同步安全
            synchronized (OkHttpClient.class) {
                if (okHttpClient == null) { //okhttp可以缓存数据....指定缓存路径
                    okHttpClient = new OkHttpClient.Builder()//构建器
                            .proxy(Proxy.NO_PROXY) //来屏蔽系统代理
                            .connectionPool(connectionPool)
                            .connectTimeout(6000, TimeUnit.SECONDS)//连接超时
                            .writeTimeout(6000, TimeUnit.SECONDS)//写入超时
                            .readTimeout(6000, TimeUnit.SECONDS)//读取超时
                            .build();
                    okHttpClient.dispatcher().setMaxRequestsPerHost(2000);
                    okHttpClient.dispatcher().setMaxRequests(2000);
                }
            }
        }
        return okHttpClient;
    }
}
