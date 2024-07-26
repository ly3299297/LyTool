package com.ly.gpt.generater.img.fromston;


import cn.hutool.core.util.StrUtil;
import com.ly.gpt.generater.img.Text2PhotoOutput;
import com.ly.gpt.utils.ThreadPoolUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.noear.snack.ONode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FromstonText2PhotoOutput implements Text2PhotoOutput {


    static okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
    private String prompt;
    /*!
       fromston state 类型 in_wait in_create  success  fail cancel disabled
    */
    private String status;
    private String error;
    private OkHttpClient httpclient;
    private String apiKey;

    private List<String> ids;
    private List<String> images;

    public static MediaType getMediaType() {
        return mediaType;
    }

    public static void setMediaType(MediaType mediaType) {
        FromstonText2PhotoOutput.mediaType = mediaType;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public OkHttpClient getHttpclient() {
        return httpclient;
    }

    public void setHttpclient(OkHttpClient httpclient) {
        this.httpclient = httpclient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public List<String> getPhotos() {
        try {

            CompletableFuture<List<String>> listCompletableFuture = applyImgUrlsFuture();
            images = listCompletableFuture.get(50, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("请求异常：", e);
        }

        return images;
    }


    public CompletableFuture<List<String>> applyImgUrlsFuture() {
        // 并行发送请求
        List<CompletableFuture<String>> futures = ids.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> fetchUrl(id), ThreadPoolUtil.getThreadPoolExecutor()))
                .collect(Collectors.toList());


        // 等待所有请求完成
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        // 获取请求结果
        CompletableFuture<List<String>> listCompletableFuture = combinedFuture.thenApply(v -> {
            List<String> results = futures.stream()
                    .map(future -> future.join()) // 获取每个CompletableFuture的结果
                    .collect(Collectors.toList());

            // 输出结果或进行其他处理
            List<String> images = new ArrayList<>();
            for (String result : results) {
                ONode root = ONode.load(result);
                ONode data = root.get("data").asObject();

                prompt = data.get("prompt").getString();
                // "state":"success"
                status = data.get("state").getString();
                if (StrUtil.isBlank(status)) {
                    throw new RuntimeException(result);
                }
                error = data.get("fail_reason").getString();
                images.add(data.get("gen_img").getString());
            }
            //imageUrls=images;

            return images;
        });

        return listCompletableFuture;
    }


    public String fetchUrl(String imgTaskId) {
        try {
            //可以进一步优化，使用httpclient连接池， 以及有任务并行编排的请求，使用异步回调
            Request request = new Request.Builder()
                    .url("https://ston.6pen.art/release/open-task?id=" + imgTaskId)
                    .addHeader("ys-api-key", apiKey)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .build();

            Response response = httpclient.newCall(request).execute();
            String responStr = response.body().string();
            System.out.println(responStr);
            return responStr;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
