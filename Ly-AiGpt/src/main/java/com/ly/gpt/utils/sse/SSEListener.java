package com.ly.gpt.utils.sse;


import cn.hutool.core.util.StrUtil;
import com.ly.gpt.generater.text.ChatMessage;
import com.ly.gpt.generater.text.Text2TextStreamObserver;
import com.ly.gpt.generater.text.Text2TextStreamOutput;
import com.ly.gpt.common.Constants;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.noear.snack.ONode;

import java.io.IOException;
import java.util.Vector;

@Slf4j
public class SSEListener extends EventSourceListener  {


    private Vector<Text2TextStreamObserver> obs;


    private final StringBuffer output = new StringBuffer();

    public SSEListener() {
        this.obs = new Vector<>();
    }

    public synchronized void addObserver(Text2TextStreamObserver o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (!obs.contains(o)) {
            obs.addElement(o);
        }
    }

    public synchronized void deleteObserver(Text2TextStreamObserver o) {
        obs.removeElement(o);
    }

    @Override
    public void onOpen(final EventSource eventSource, final Response
            response) {

        log.info("建立sse连接...");

    }


    public void oberversUpdate(String data){
        Object[] arrLocal;
        synchronized (this) {
            arrLocal = obs.toArray();
        }
        for (int i = arrLocal.length-1; i>=0; i--)
            ((Text2TextStreamObserver)arrLocal[i]).onNext(data);
    }

    public String complateOutput(String outputStr) {
        //System.out.println(outputStr);

        if ("[DONE]".equals(outputStr)) {
            return outputStr;
        }

        Text2TextStreamOutput streamOutput = new Text2TextStreamOutput();
        //System.out.println(outputStr);
        ONode root = ONode.load(outputStr);
        ChatMessage chatMessage = null;

        // 百度  结束时 is_end":false
        // {"is_end":false,"result":"以下是十","usage":{"prompt_tokens":8,"completion_tokens":0,"total_tokens":8}}
        String content = root.get("result").getString();
        boolean isEnd = root.get("is_end").getBoolean();

        if (isEnd || !StrUtil.isBlank(content)) {
            chatMessage = new ChatMessage(Constants.AI_ROLE_ASSISTANT, content);
        } else {
            // 百川  结束 [DONE]
            //{"choices":[{"index":0,"delta":{"role":"assistant","content":"以下是"}}], "usage":{"prompt_tokens":10,"completion_tokens":267,"total_tokens":277}}
            // 灵积  结束 [DONE]
            //{"choices":[{"delta":{"content":"，增加口感和风味。"}  无usage
            chatMessage = root.get("choices").get(0).get("delta").toObject(ChatMessage.class);
        }
        int completionTokens = root.get("usage").get("completion_tokens").getInt();
        if (completionTokens != 0) {
            streamOutput.setPromptTokens(root.get("usage").get("prompt_tokens").getInt());
            streamOutput.setTotalTokens(root.get("usage").get("total_tokens").getInt());
            streamOutput.setCompletionTokens(root.get("usage").get("completion_tokens").getInt());
        }

        streamOutput.setMessage(chatMessage);

        String stringify = ONode.stringify(streamOutput);

        if (isEnd) {
            stringify += "\n\ndata:[DONE]";
        }
        return stringify;
    }


    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        try {
            output.append(data);
            if ("DONE".equals(type) || data.contains("\"is_end\":true")) {
                //log.info("请求结束{} ", output.toString());
                log.info("请求结束{} ");
            }
            if ("error".equals(type)) {
                log.info("{}source ", data);
            }

            String[] dataArr = data.split("\\n");
            for (int i = 0; i < dataArr.length; i++) {
                String s = complateOutput(dataArr[i]);
                if (i == dataArr.length - 1) {
                    oberversUpdate("data:" + s + "\n\n");
                } else {
                    oberversUpdate("data:" + s + "\n");
                }
            }
        } catch (Exception e) {
            log.error("消息错误[" + "]", e);
            //countDownLatch.countDown();
            throw new RuntimeException(e);
        }

    }


    @Override
    public void onClosed(final EventSource eventSource) {
        log.info("sse连接关闭:{}");

    }


    @Override
    public void onFailure(final EventSource eventSource, final Throwable t, final Response response) {

        String responseBody = null;
        try {
            responseBody = response.body().string();
            oberversUpdate("data:" + responseBody + "\n");
            oberversUpdate("data:" + "[DONE]" + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.error("使用事件源时出现异常... [响应：{}]...", responseBody);
        //countDownLatch.countDown();
    }

    //public CountDownLatch getCountDownLatch() {
    //    return this.countDownLatch;
    //}
}
