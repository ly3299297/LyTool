package com.ly.gpt.generater.text.spark;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.gpt.generater.text.*;
import io.reactivex.annotations.Nullable;
import okhttp3.*;
import okio.ByteString;
import org.noear.snack.ONode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public class SparkTextGenerater implements TextGenerater<SparkText2TextInput, SparkText2TextOutput> {

    protected static final Logger logger = LoggerFactory.getLogger(SparkTextGenerater.class);
    static okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");


    private final OkHttpClient httpclient;
    private final String appid;
    private final String apiKey;
    //spark-api.xf-yun.com/v1.1/chat
    private final String apiSecret;
    private WebSocket webSocket;
    private String url = "https://spark-api.xf-yun.com/v1.1/chat";
    //private String domain = "generalv2";
    private String domain = "general";


    public SparkTextGenerater(OkHttpClient httpclient, String appid, String apiKey, String apiSecret) {
        this.httpclient = httpclient;
        this.appid = appid;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }


    /**
     * @return String 获取认证之后的URL
     * @throws Exception 异常
     */
    public String getAuthUrl() throws Exception {
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        URL urlObject = null;
        try {
            urlObject = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        // 拼接
        String preStr = "host: " + urlObject.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + urlObject.getPath() + " HTTP/1.1";
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);

        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + urlObject.getHost() + urlObject.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", urlObject.getHost()).//
                build();

        return httpUrl.toString();
    }

    public Request buildRequest() {

        String authWsUrl = null;
        try {
            authWsUrl = getAuthUrl().replace("http://", "ws://").replace("https://", "wss://");
        } catch (Exception e) {
            throw new RuntimeException("构建鉴权url失败", e);
        }

        Request request = new Request.Builder().url(authWsUrl).build();

        return request;
    }


    public String buildWebsocketBody(SparkText2TextInput input) {
        JSONObject websocketParams = new JSONObject();

        JSONObject header = new JSONObject();  // header参数
        header.put("app_id", this.appid);
        header.put("uid", UUID.randomUUID().toString().substring(0, 10));

        JSONObject parameter = new JSONObject(); // parameter参数
        JSONObject chat = new JSONObject();
        chat.put("domain", this.domain);
        chat.put("temperature", input.getTemperature());
        chat.put("top_k", input.getTopP());
        chat.put("max_tokens", 4096);
        parameter.put("chat", chat);

        JSONObject payload = new JSONObject(); // payload参数
        JSONObject message = new JSONObject();
        JSONArray text = new JSONArray();

        if (input.getMessages().size() > 0) {
            for (ChatMessage chatMessage : input.getMessages()) {
                text.add(chatMessage);
            }
        }
        message.put("text", text);
        payload.put("message", message);


        websocketParams.put("header", header);
        websocketParams.put("parameter", parameter);
        websocketParams.put("payload", payload);


        return websocketParams.toString();
    }

    @Override
    public void text2TextSync(SparkText2TextInput input, Text2TextObserver observer) {
        SparkText2TextOutput output = new SparkText2TextOutput();
        StringBuffer buffer = new StringBuffer();
        try {
            Request request = buildRequest();
            String websocketBody = buildWebsocketBody(input);

            CompletableFuture<String> taskIdFuture = new CompletableFuture<>();

            // 创建WebSocket对象，并指定一个监听器处理事件
            webSocket = httpclient.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    //System.out.println( text);
                    int status = complateOutput(text, output, buffer);
                    if (status == 2) {
                        ObjectMapper mapper = new ObjectMapper();
                        String jsonStr = null;
                        try {
                            jsonStr = mapper.writeValueAsString(output);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        observer.onNext(jsonStr);
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                    observer.onFailure(new Exception("Request  failed:", t));
                }
            });

            webSocket.send(websocketBody);


            webSocket.close(1000, "Goodbye, world!");


        } catch (Exception e) {
            throw new RuntimeException("请求异常：", e);
        }



    }


    public Integer complateOutput(String responseString, SparkText2TextOutput output, StringBuffer buffer) {
        ONode root = ONode.load(responseString);
        int code = root.get("header").get("code").getInt();
        if (code != 0) {
            throw new RuntimeException("请求异常：" + responseString);
        }

        ONode payload = root.get("payload");

        buffer.append(payload.get("choices").get("text").get(0).get("content").toString());
        ONode usage = payload.get("usage");
        ChatMessage chatMessage = null;
        int status = root.get("header").get("status").getInt();
        if (status == 2) {
            output.setPromptTokens(usage.get("text").get("prompt_tokens").getInt());
            output.setTotalTokens(usage.get("text").get("total_tokens").getInt());
            output.setCompletionTokens(usage.get("text").get("completion_tokens").getInt());
            chatMessage = payload.get("choices").get("text").get(0).toObject(ChatMessage.class);
            chatMessage.setContent(buffer.toString());
            output.setMessage(chatMessage);
        }
        return status;

    }


    public String complateOutputStream(String responseString) {
        Text2TextStreamOutput output = new Text2TextStreamOutput();
        ONode root = ONode.load(responseString);
        int code = root.get("header").get("code").getInt();
        if (code != 0) {
            throw new RuntimeException("请求异常：" + responseString);
        }
        ONode payload = root.get("payload");
        ONode usage = payload.get("usage");
        output.setPromptTokens(usage.get("text").get("prompt_tokens").getInt());
        output.setTotalTokens(usage.get("text").get("total_tokens").getInt());
        output.setCompletionTokens(usage.get("text").get("completion_tokens").getInt());
        ChatMessage chatMessage = payload.get("choices").get("text").get(0).toObject(ChatMessage.class);
        output.setMessage(chatMessage);

        String stringify = "data:" + ONode.stringify(output);

        int status = root.get("header").get("status").getInt();
        if (status == 2) {
            stringify += "\n\ndata:[DONE]";
        } else {
            stringify += "\n\n";
        }
        return stringify;

    }

    @Override
    public void text2TextStream(SparkText2TextInput input, Text2TextStreamObserver streamObserver) {
        try {
            Request request = buildRequest();
            String websocketBody = buildWebsocketBody(input);

            // 创建WebSocket对象，并指定一个监听器处理事件
            webSocket = httpclient.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    //System.out.println( text);
                    String resultLine = complateOutputStream(text);
                    streamObserver.onNext(resultLine);
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    streamObserver.onBegin();
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    streamObserver.onEnd();
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                    streamObserver.onFailure(t);
                }
            });

            webSocket.send(websocketBody);

            // 阻塞主线程，等待 CompletableFuture 的结果，设置了最大等待时间

            webSocket.close(1000, "Goodbye, world!");

        } catch (Exception e) {
            throw new RuntimeException("流式请求异常：", e);
        }
    }


    public void url(String url) {
        this.url = url;
    }

    public void domain(String domain) {
        this.domain = domain;
    }
}