package com.ly.gpt.utils.sse;


import cn.hutool.core.util.StrUtil;
import com.ly.gpt.generater.text.ChatMessage;
import com.ly.gpt.generater.text.Text2TextStreamOutput;
import com.ly.gpt.common.Constants;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.noear.snack.ONode;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
public class SSEListenerOld extends EventSourceListener {

    //private CountDownLatch countDownLatch = new CountDownLatch(1);

    private final HttpServletResponse rp;

    private final List<String> dataList;

    private final StringBuffer output = new StringBuffer();

    public SSEListenerOld(HttpServletResponse response, List<String> dataList) {

        this.rp = response;
        this.dataList = dataList;
    }


    @Override
    public void onOpen(final EventSource eventSource, final Response response) {
        if (rp != null) {
            rp.setContentType("text/event-stream");
            rp.setCharacterEncoding("UTF-8");
            rp.setStatus(200);
            log.info("建立sse连接...");
        } else {
            log.info("客户端非sse推送");
        }
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
        dataList.add("data:" + stringify);

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
            if (rp != null) {
                String[] dataArr = data.split("\\n");
                for (int i = 0; i < dataArr.length; i++) {
                    String s = complateOutput(dataArr[i]);
                    if (i == dataArr.length - 1) {
                        rp.getWriter();
                        rp.getWriter().write("data:" + s + "\n\n");
                        rp.getWriter().flush();
                    } else {
                        rp.getWriter().write("data:" + s + "\n");
                        rp.getWriter().flush();
                    }
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
        try {
            rp.getWriter().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //log.info("结果输出:{}" + output.toString());
        //countDownLatch.countDown();
    }


    @Override
    public void onFailure(final EventSource eventSource, final Throwable t, final Response response) {

        String responseBody = null;
        try {
            responseBody = response.body().string();
            rp.getWriter().write("data:" + responseBody + "\n");
            rp.getWriter().write("data:" + "[DONE]" + "\n");
            rp.getWriter().flush();
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
