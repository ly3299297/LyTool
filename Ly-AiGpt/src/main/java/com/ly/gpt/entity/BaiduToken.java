package com.ly.gpt.entity;


import okhttp3.*;
import org.noear.snack.ONode;

public class BaiduToken {
    // 过期时间间隔，单位 毫秒。 时长为30天   2592000 *1000
    private Long expiresIn;

    // 用于访问百度千帆的模型  token
    private String accessToken;

    //获取token的时间   单位 毫秒
    private Long createTimestamp;
    //token真正的过期时间    单位 毫秒
    private Long endTimestamp;


    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }


    public static BaiduToken getToken(OkHttpClient httpclient, String clientId, String clientSecret) {


        //String clientId = authKeys[0];
        //String clientSecret = authKeys[1];
        BaiduToken baiduToken=new BaiduToken();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        try {
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url(String.format("https://aip.baidubce.com/oauth/2.0/token?client_id=%s&client_secret=%s&grant_type=client_credentials", clientId, clientSecret))
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Accept", "application/json; charset=utf-8")
                    .build();
            Response response = httpclient.newCall(request).execute();
            String resp = response.body().string();
            //System.out.println(resp);
            ONode root = ONode.load(resp);
            //"expires_in": 2592000,
            //        "session_key": "9mzdDoqSusiPHD7IGaEJRlc/4Ox7/8/tbyLO2yx91A3cFRee9Wyp+TsW5zPi/SMHD7bc77CEDroxnTvPFjrBCl3RwH989g==",
            //        "access_token": "24.58d4baf1a7c1bed413d6d345aa190635.2592000.1717810876.282335-62738901",

            long expiresIn = root.get("expires_in").getLong();
            String accessToken = root.get("access_token").getString();
            baiduToken.setExpiresIn( expiresIn * 1000);
            baiduToken.setAccessToken( accessToken);
            baiduToken.setCreateTimestamp(System.currentTimeMillis());
            baiduToken.setEndTimestamp(baiduToken.getCreateTimestamp()+ baiduToken.getExpiresIn()-300*1000);
        }catch (Exception e){
            throw new RuntimeException("getToken 失败",e);
        }

        return baiduToken;
    }

}
