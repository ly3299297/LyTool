package com.ly.gpt.generater.img.fromston;


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
import java.util.List;


public class FromstonPhotoGenerater implements PhotoGenerater<FromstonText2PhotoInput, FromstonText2PhotoOutput> {

    protected static final Logger logger = LoggerFactory.getLogger(FromstonPhotoGenerater.class);
    static okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
    private String url = "https://ston.6pen.art/release/open-task";
    private String modelId = "3";
    private final OkHttpClient httpclient;
    private final String apiKey;

    public FromstonPhotoGenerater(OkHttpClient httpclient, String apiKey) {
        this.httpclient = httpclient;
        this.apiKey = apiKey;
    }


    public void url(String url) {
        this.url = url;
    }

    public void modelId(String modelId) {
        this.modelId = modelId;
    }

    @Override
    public FromstonText2PhotoOutput text2Photo(FromstonText2PhotoInput input) {
        FromstonText2PhotoOutput output = new FromstonText2PhotoOutput();
        try {
            JSONObject parameters = new JSONObject();
            parameters.set("model_id", modelId);
            parameters.set("prompt", input.getPrompt());
            parameters.set("width", input.getWidth());
            parameters.set("height", input.getHeight());
            parameters.set("multiply", input.getSize());

            JSONObject addition = new JSONObject();
            addition.set("negative_prompt", "");

            parameters.set("addition", addition);


            RequestBody body = RequestBody.create(mediaType, parameters.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("ys-api-key", apiKey)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .build();

            Response response = httpclient.newCall(request).execute();

            String data = response.body().string();


            ONode root = ONode.load(data);
            List<ONode> nodes = root.get("data").get("ids").ary();
            if (nodes.size() == 0) {
                throw new RuntimeException(data);
            }
            List<String> ids = new ArrayList<>(nodes.size());
            for (ONode node : nodes) {
                ids.add(node.getString());
            }
            output.setIds(ids);
            output.setApiKey(apiKey);
            output.setHttpclient(httpclient);
        } catch (Exception e) {
            throw new RuntimeException("请求异常：", e);
        }
        return output;
    }


    //@Override
    //public FromstonText2PhotoOutput imageGet(List<String> imgTaskIds) {
    //    FromstonText2PhotoOutput output = new FromstonText2PhotoOutput();
    //    try {
    //        String prompt = null;
    //        String status = null;
    //        String error = null;
    //        List<String> images = new ArrayList<>();
    //        for (String imgTaskId : imgTaskIds) {
    //            Request request = new Request.Builder()
    //                    .url("https://ston.6pen.art/release/open-task?id=" + imgTaskId)
    //                    .addHeader("ys-api-key", apiKey)
    //                    .addHeader("Content-Type", "application/json; charset=utf-8")
    //                    .build();
    //
    //            Response response = httpclient.newCall(request).execute();
    //
    //            String respStr = response.body().string();
    //            if (logger.isDebugEnabled()) {
    //                logger.debug(StringUtil.prettyJson(respStr));
    //            }
    //
    //            ONode root = ONode.load(respStr);
    //            ONode data = root.get("data").asObject();
    //
    //            prompt = data.get("prompt").getString();
    //            status = data.get("state").getString();
    //            error =data.get("fail_reason").getString();
    //            images.add(data.get("gen_img").getString());
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
