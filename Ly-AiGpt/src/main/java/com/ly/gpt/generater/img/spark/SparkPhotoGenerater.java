package com.ly.gpt.generater.img.spark;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ly.gpt.generater.img.PhotoGenerater;
import com.ly.gpt.generater.img.spark.dto.SparkImgMessage;
import okhttp3.*;
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


public class SparkPhotoGenerater implements PhotoGenerater<SparkText2PhotoInput, SparkText2PhotoOutput> {

    protected static final Logger logger = LoggerFactory.getLogger(SparkPhotoGenerater.class);
    static MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpclient;
    private final String appid;
    private final String apiKey;
    private final String apiSecret;
    private String url = "https://spark-api.cn-huabei-1.xf-yun.com/v2.1/tti";

    public SparkPhotoGenerater(OkHttpClient httpclient, String appid, String apiKey, String apiSecret) {
        this.httpclient = httpclient;
        this.appid = appid;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public static String base64String(String text, String apiSecret) {
        String textSha = "";
        try {
            Mac mac = Mac.getInstance("hmacsha256");
            SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
            mac.init(spec);

            byte[] hexDigits = mac.doFinal(text.getBytes(StandardCharsets.UTF_8));
            // Base64加密
            textSha = Base64.getEncoder().encodeToString(hexDigits);
        } catch (Exception e) {
            throw new RuntimeException("生成签名失败：", e);
        }

        return textSha;
    }

    /**
     * @return String 获取认证之后的URL
     * @throws Exception 异常
     */
    public static String getAuthUrl(String url, String apiKey, String apiSecret) {
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
                "POST " + urlObject.getPath() + " HTTP/1.1";
        // SHA256加密
        String sha = base64String(preStr, apiSecret);
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

    public void url(String url) {
        this.url = url;
    }

    @Override
    public SparkText2PhotoOutput text2Photo(SparkText2PhotoInput input) {
        SparkText2PhotoOutput output = new SparkText2PhotoOutput();
        try {
            Request request = buildRequest(input);

            Response response = httpclient.newCall(request).execute();

            String data = response.body().string();


            /**
             * {"header":{"code":0,"message":"success","sid":"ase000e3dd7@hu190351fb9bb1323882","task_id":"240620180950012399465284"},"payload":null}
             */

            ONode root = ONode.load(data);
            int code = root.get("header").get("code").getInt();
            if (code != 0) {
                throw new RuntimeException(data);
            }
            List<SparkImgMessage> imgMessages = root.get("payload").get("choices").get("text").toObjectList(SparkImgMessage.class);
            //String taskId = "240621092649710245997730";
            //ArrayList<String> ids=new ArrayList<>();
            ArrayList<String> images = new ArrayList<>();
            //int count = 1;
            for (SparkImgMessage sparkImgMessage : imgMessages) {
                //ids.add(chatMessage.getContent());
                images.add(sparkImgMessage.getContent());
                //Base64ToImage.convertBase64ToImage(sparkImgMessage.getContent(), count + ".png");
                //count++;
            }

            output.setImages(images);
            //output.setIds(ids);

        } catch (Exception e) {
            throw new RuntimeException("请求异常：", e);
        }
        return output;
    }

    public Request buildRequest(SparkText2PhotoInput input) {


        JSONObject inputBody = new JSONObject();
        Request request = null;
        try {
            JSONObject header = new JSONObject();
            JSONObject parameter = new JSONObject();
            JSONObject payload = new JSONObject();
            header.set("app_id", appid);

            JSONObject chat = new JSONObject();
            chat.set("domain", "general");
            chat.set("width", 512);
            chat.set("height", 512);

            parameter.set("chat", chat);


            JSONObject message = new JSONObject();
            JSONArray texts = new JSONArray();
            JSONObject text = new JSONObject();

            text.set("role", "user");
            text.set("content", input.getPrompt());

            texts.add(text);
            message.set("text", texts);
            payload.set("message", message);


            inputBody.set("header", header);
            inputBody.set("parameter", parameter);
            inputBody.set("payload", payload);
            String s = inputBody.toString();
            System.out.println(s);

            RequestBody body = RequestBody.create(mediaType, inputBody.toString());
            String authUrl = null;
            try {
                authUrl = getAuthUrl(url, apiKey, apiSecret);
                System.out.println(authUrl);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            request = new Request.Builder().url(authUrl).method("POST", body).addHeader("Content-Type", "application/json; charset=utf-8").build();
            //Request request = new Request.Builder().url(authUrl).build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return request;
    }

}
