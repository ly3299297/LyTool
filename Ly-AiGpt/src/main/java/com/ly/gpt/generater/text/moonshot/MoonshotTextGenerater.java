package com.ly.gpt.generater.text.moonshot;


import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.gpt.generater.text.ChatMessage;
import com.ly.gpt.generater.text.Text2TextObserver;
import com.ly.gpt.generater.text.Text2TextStreamObserver;
import com.ly.gpt.generater.text.TextGenerater;
import com.ly.gpt.utils.sse.ExecuteSSEUtil;
import com.ly.gpt.utils.sse.SSEListener;
import okhttp3.*;
import org.noear.snack.ONode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * <pre>
 * https://docs.tryleap.ai/
 * </pre>
 */

public class MoonshotTextGenerater implements TextGenerater<MoonshotText2TextInput, MoonshotText2TextOutput> {

    protected static final Logger logger = LoggerFactory.getLogger(MoonshotTextGenerater.class);
    static okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpclient;
    private final String apiKey;
    private String url = "https://api.moonshot.cn/v1/chat/completions";
    private String model = "moonshot-v1-8k";

    public MoonshotTextGenerater(OkHttpClient httpclient, String apiKey) {
        this.httpclient = httpclient;
        this.apiKey = apiKey;
    }

    public void url(String url) {
        this.url = url;
    }

    public void model(String model) {
        this.model = model;
    }

//    @Override
//    public MoonshotText2TextOutput text2Text(MoonshotText2TextInput input) {
//        //String model="moonshot-v1-8k";
////    private String model="moonshot-v1-32k";
////    private String model="moonshot-v1-128k";
//        MoonshotText2TextOutput output=new MoonshotText2TextOutput();
//        try{
//            Request request = buildRequest(input,false);
//            Response response = httpclient.newCall(request).execute();
//            String data = response.body().string();
//            complateOutput(data,output);
//
//        }catch (Exception e){
//            throw new RuntimeException("请求异常：",e);
//        }
//        return output;
//
//    }

    public CompletableFuture<String> text2TextSync2(MoonshotText2TextInput input) {
        MoonshotText2TextOutput output = new MoonshotText2TextOutput();
        StringBuffer contentBuffer = new StringBuffer();
        CompletableFuture<String> taskIdFuture;
        try {
            Request request = buildRequest(input, false);

            taskIdFuture = new CompletableFuture<>();

            // 创建一个新的异步 HTTP 请求，并指定请求的回调函数
            httpclient.newCall(request).enqueue(new Callback() {
                // 在请求成功并返回响应时被调用
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        taskIdFuture.complete(responseBody);
                    } else {
                        String responseBody = response.body().string();
                        taskIdFuture.completeExceptionally(new Exception("Request for task_id failed:" + responseBody));
                    }
                }

                // 在请求失败时被调用
                @Override
                public void onFailure(Call call, IOException e) {
                    taskIdFuture.completeExceptionally(e);
                }
            });

            // 阻塞主线程，等待 CompletableFuture 的结果，设置了最大等待时间
            //String data = taskIdFuture.get(50, TimeUnit.SECONDS);
            //
            //System.out.println(data);
            //complateOutput(data, output);


        } catch (Exception e) {
            throw new RuntimeException("请求异常：", e);
        }
        return taskIdFuture;
    }


    @Override
    public void text2TextSync(MoonshotText2TextInput input, Text2TextObserver observer) {
        MoonshotText2TextOutput output = new MoonshotText2TextOutput();
        StringBuffer contentBuffer=new StringBuffer();
        try {
            Request request = buildRequest(input, false);


            // 创建一个新的异步 HTTP 请求，并指定请求的回调函数
            httpclient.newCall(request).enqueue(new Callback() {
                // 在请求成功并返回响应时被调用
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        String jsonStr = complateOutput(responseBody, output);
                        observer.onNext(jsonStr);
                    } else {
                        String responseBody = response.body().string();
                        observer.onFailure(new Exception("Request  failed:" + responseBody));
                    }
                }

                // 在请求失败时被调用
                @Override
                public void onFailure(Call call, IOException e) {
                    observer.onFailure(e);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("请求异常：", e);
        }
    }

    public String complateOutput(String responseString, MoonshotText2TextOutput output) {
        ONode root = ONode.load(responseString);
        ChatMessage chatMessage = root.get("choices").get(0).get("message").toObject(ChatMessage.class);
        output.setPromptTokens(root.get("usage").get("prompt_tokens").getInt());
        output.setTotalTokens(root.get("usage").get("total_tokens").getInt());
        output.setCompletionTokens(root.get("usage").get("completion_tokens").getInt());
        output.setMessage(chatMessage);

        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = mapper.writeValueAsString(output);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jsonStr;
    }

    @Override
    public void text2TextStream(MoonshotText2TextInput input, Text2TextStreamObserver streamObserver) {
        List<String> dataList = new ArrayList<>();
        try {
            Request request = buildRequest(input, true);


            SSEListener sseListener = new SSEListener();
            sseListener.addObserver(streamObserver);
            ExecuteSSEUtil.executeSSE(request, sseListener, httpclient);

        } catch (Exception e) {
            throw new RuntimeException("流式请求异常：", e);
        }


    }

    public Request buildRequest(MoonshotText2TextInput input, boolean isStream) {
        JSONObject parameters = new JSONObject();
        parameters.set("model", model);
        parameters.set("messages", input.getMessages());
        parameters.set("temperature", input.getTemperature());
        parameters.set("top_p", input.getTopP());

        parameters.set("stream", isStream);

        RequestBody body = RequestBody.create(mediaType, parameters.toString());
        Request request = new Request.Builder().url(url).method("POST", body).addHeader("Authorization", "Bearer " + apiKey).addHeader("Content-Type", "application/json; charset=utf-8").build();

        return request;
    }


}