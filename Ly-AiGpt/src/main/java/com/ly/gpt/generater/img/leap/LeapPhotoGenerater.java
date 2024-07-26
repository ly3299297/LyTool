package com.ly.gpt.generater.img.leap;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.ly.gpt.generater.img.PhotoGenerater;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.noear.snack.ONode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * <pre>
 * https://docs.tryleap.ai/
 * </pre>
 */

public class LeapPhotoGenerater implements PhotoGenerater<LeapText2PhotoInput, LeapText2PhotoOutput> {

    protected static final Logger logger = LoggerFactory.getLogger(LeapPhotoGenerater.class);
    static okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
    private final String url = "https://api.workflows.tryleap.ai/v1/runs";
    private final String workflowId = "wkf_MylTMXihomuExT";
    private final OkHttpClient httpclient;
    /**
     * Leap apikey
     */
    private final String apiKey;

    public LeapPhotoGenerater(OkHttpClient httpclient, String apiKey) {
        this.httpclient = httpclient;
        this.apiKey = apiKey;
    }


    @Override
    public LeapText2PhotoOutput text2Photo(LeapText2PhotoInput input) {
        LeapText2PhotoOutput output = new LeapText2PhotoOutput();
        try {

            JSONObject parameters = new JSONObject();
            parameters.set("workflow_id", workflowId);

            JSONObject inputJson = new JSONObject();
            inputJson.set("prompt", input.getPrompt());
            inputJson.set("width", input.getWidth());
            inputJson.set("height", input.getHeight());
            inputJson.set("numberOfImages", input.getSize());

            parameters.set("input", inputJson);


            RequestBody body = RequestBody.create(mediaType, parameters.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("X-Api-Key", apiKey)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .build();

            Response response = httpclient.newCall(request).execute();

            String data = response.body().string();
/**
 *  {
 *   "statusCode" : 402,
 *   "message" : "Your organization run out of free credits. Please upgrade your plan to continue using Leap."
 * }
 */

            ONode root = ONode.load(data);
            String id = root.get("id").getString();
            if (StrUtil.isBlank(id)) {
                throw new RuntimeException(data);
            }
            ArrayList<String> ids = new ArrayList<>();
            ids.add(id);

            output.setIds(ids);
            output.setApiKey(apiKey);
            output.setHttpclient(httpclient);


        } catch (Exception e) {
            throw new RuntimeException("请求异常：", e);
        }
        return output;
    }

    //@Override
    //public LeapText2PhotoOutput imageGet(List<String> imgTaskIds) {
    //    LeapText2PhotoOutput output = new LeapText2PhotoOutput();
    //    try {
    //        String prompt = null;
    //        String status = null;
    //        String error = null;
    //        List<String> images = new ArrayList<>();
    //        for (String imgTaskId : imgTaskIds) {
    //            Request request = new Request.Builder()
    //                    .url("https://api.workflows.tryleap.ai/v1/runs/"  + imgTaskId)
    //                    .addHeader("X-Api-Key", apiKey)
    //                    .addHeader("Content-Type", "application/json; charset=utf-8")
    //                    .build();
    //
    //            Response response = httpclient.newCall(request).execute();
    //
    //            String data = response.body().string();
    //            if (logger.isDebugEnabled()) {
    //                logger.debug(StringUtil.prettyJson(data));
    //            }
    //
    //            ONode root = ONode.load(data);
    //            prompt = root.get("input").get("prompt").getString();
    //            //"status":"completed"
    //            status = root.get("status").getString();
    //            error =root.get("error").getString();
    //            images.addAll(root.get("output").get("images").toObjectList(String.class));
    //        }
    //        output.setPrompt(prompt);
    //        output.setImageUrls(images);
    //        output.setStatus(status);
    //        output.setError(error);
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //        throw new RuntimeException("请求异常：",e);
    //    }
    //
    //    return output;
    //}

}