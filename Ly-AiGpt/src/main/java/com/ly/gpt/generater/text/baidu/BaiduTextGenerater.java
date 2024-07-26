package com.ly.gpt.generater.text.baidu;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.gpt.generater.text.*;
import com.ly.gpt.entity.BaiduToken;
import com.ly.gpt.utils.sse.ExecuteSSEUtil;
import com.ly.gpt.utils.sse.OkHttpUtil;
import com.ly.gpt.utils.sse.SSEListener;
import okhttp3.*;
import org.noear.snack.ONode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * https://docs.tryleap.ai/
 * </pre>
 */
public class BaiduTextGenerater implements TextGenerater<BaiduText2TextInput, BaiduText2TextOutput> {

    protected static final Logger logger = LoggerFactory.getLogger(BaiduTextGenerater.class);

    //private RestTemplate template;
    static MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpclient;
    private final String clientId;
    private final String clientSecret;
    private BaiduToken token = new BaiduToken();
    /**
     * 百度千帆的url和模型名称是放到一起的，所以如果需要更改模型，只需要更改url
     */
    private String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie-3.5-8k-0205";

    public BaiduTextGenerater(OkHttpClient httpclient, String clientId, String clientSecret) {
        this.httpclient = httpclient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public void url(String url) {
        this.url = url;
    }

    //@Override
    //public BaiduText2TextOutput text2Text(BaiduText2TextInput input) {
    //    BaiduText2TextOutput output=new BaiduText2TextOutput();
    //
    //    try {
    //        token = getToken(httpclient, clientId, clientSecret);
    //        //token.setAccessToken("24.41ae2dee80a4cc3f76b1f04b95266f46.2592000.1717832531.282335-62738901");
    //
    //        Request request = buildRequest(input,false);
    //        Response response = httpclient.newCall(request).execute();
    //
    //        String data = response.body().string();
    //        if (logger.isDebugEnabled()) {
    //            logger.debug(StringUtil.prettyJson(data));
    //        }
    //
    //
    //        complateOutput(data,output);
    //
    //    }catch (Exception e){
    //        throw new RuntimeException("请求异常：",e);
    //
    //    }
    //
    //    return output;
    //
    //}

    public Request buildRequest(BaiduText2TextInput input, boolean isStream) {

        JSONObject parameters = new JSONObject();
        parameters.set("messages", input.getMessages());
        parameters.set("temperature", input.getTemperature());
        parameters.set("top_p", input.getTopP());
        if (!StrUtil.isBlank(input.getSystem())) {
            parameters.set("system", input.getSystem());
        }
        parameters.set("disable_search", input.getWithSearchEnhance());
        parameters.set("stream", isStream);
        RequestBody body = RequestBody.create(mediaType, parameters.toString());
        Request request = new Request.Builder().url(url + "?access_token=" + token.getAccessToken()).method("POST", body).addHeader("Content-Type", "application/json; charset=utf-8").build();

        return request;
    }

    @Override
    public void text2TextSync(BaiduText2TextInput input, Text2TextObserver observer) {
        BaiduText2TextOutput output = new BaiduText2TextOutput();

        try {
            token = BaiduToken.getToken(httpclient, clientId, clientSecret);
            //token.setAccessToken("24.41ae2dee80a4cc3f76b1f04b95266f46.2592000.1717832531.282335-62738901");

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
                        observer.onFailure(new Exception("Request failed:" + responseBody));
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

    @Override
    public void text2TextStream(BaiduText2TextInput input, Text2TextStreamObserver streamObserver) {
        try {
            token = BaiduToken.getToken(httpclient, clientId, clientSecret);
            //token.setAccessToken("24.41ae2dee80a4cc3f76b1f04b95266f46.2592000.1717832531.282335-62738901");
            Request request = buildRequest(input, true);


            SSEListener sseListener = new SSEListener();
            sseListener.addObserver(streamObserver);
            ExecuteSSEUtil.executeSSE(request, sseListener, httpclient);
        } catch (Exception e) {
            throw new RuntimeException("流式请求异常：", e);
        }

    }



    public String complateOutput(String responseString, BaiduText2TextOutput output) {
        ONode root = ONode.load(responseString);
        ChatMessage chatMessage = new ChatMessage("assistant", root.get("result").toString());
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

//    private static final String ROLE_SYSTEM = "system";
//    private static final String ROLE_USER = "user";
//    private static final String ROLE_ASSISTANT = "assistant";
//
//    private static final String BAIDU_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie_speed";
//    private static final String BAIDU_HELLO ="你是 BaiDu，由 BaiDu AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。";
//
//
//    public static void main(String[] args) {
//        List<ChatMessage> messages = new ArrayList<>();
//        Text2TextInput input = new BaiduText2TextInput();
//        messages.add(new ChatMessage(ROLE_SYSTEM, BAIDU_HELLO));
//        messages.add(new ChatMessage(ROLE_USER,"简述中华五千年历史"));
//        input.setMessages(messages);
//
//        BaiduText2TextInput requestEntiry = (BaiduText2TextInput) input;
//        OkHttpClient client = OkHttpUtil.getInstance();
//
//        BaiduTextGenerater generater = new BaiduTextGenerater(client, "CXUNZS8lM9eajYHUp24BBNNm", "Tc5Ywb2WHZgDfRWLj82mrqxENA3mSfk5");
//        generater.url(BAIDU_URL);
//        generater.text2TextStream(requestEntiry, streamOutputResposeOberverBuild());
//    }
//
//    static Text2TextStreamObserver streamOutputResposeOberverBuild() {
//        StringBuffer sb = new StringBuffer();
//        return new Text2TextStreamObserver() {
//            Boolean flag = Boolean.TRUE;
//            @Override
//            public void onBegin() {
//            }
//
//            @Override
//            public void onNext(String data) {
//                    System.out.println(":::"+data);
//            }
//
//            @Override
//            public void onEnd() {
//                System.out.println("结束");
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                System.out.println("报错了");
//            }
//        };
//
//
//    }

}