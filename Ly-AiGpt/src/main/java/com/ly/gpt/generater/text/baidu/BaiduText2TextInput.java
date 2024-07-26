package com.ly.gpt.generater.text.baidu;



import com.ly.gpt.generater.text.ChatMessage;
import com.ly.gpt.generater.text.Text2TextInput;
import com.ly.gpt.common.Constants;

import java.util.List;

public class BaiduText2TextInput implements Text2TextInput {


    /*!
        temperature
        （1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定
        （2）默认0.8，范围 (0, 1.0]，不能为0
     */
    float temperature = 0.8f;
    /*!
        top_p
       （1）影响输出文本的多样性，取值越大，生成文本的多样性越强
       （2）默认0.8，取值范围 [0, 1.0]
     */
    float topP = 0.8f;
    /*!
        disable_search
        是否强制关闭实时搜索功能，默认false，表示不关闭
        原字段名称 disable_search
     */
    Boolean withSearchEnhance = false;

    /*!
       对话消息列表 (历史对话按从老到新顺序填入)
     */
    List<ChatMessage> messages;

    /*!
    模型人设，主要用于人设设定，例如，你是xxx公司制作的AI助手，说明：
    （1）长度限制，message中的content总长度、functions和system字段总内容不能超过20000 个字符，且不能超过5120 tokens
    （2）如果同时使用system和functions，可能暂无法保证使用效果，持续进行优化
     */
    String system;


    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        if (temperature < 0 || temperature > 1F) {
            throw new IllegalArgumentException("temperature 参数范围 0-1");
        }
        this.temperature = temperature;
    }

    public float getTopP() {
        return topP;
    }

    public void setTopP(float topP) {
        if (topP < 0 || topP > 1F) {
            throw new IllegalArgumentException("topP 参数范围 0-1");
        }
        this.topP = topP;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        if (messages == null || messages.size() == 0) {
            throw new IllegalArgumentException("messages不能为空");
        }

        ChatMessage chatMessage = messages.get(messages.size() - 1);
        if (!Constants.AI_ROLE_USER.equals(chatMessage.getRole())) {
            throw new IllegalArgumentException("messages 最后一个role必须是user");
        }

        if (messages.get(0).getRole().equals(Constants.AI_ROLE_SYSTEM)) {
            this.system = messages.get(0).getContent();
            messages.remove(0);
        }

        this.messages = messages;
    }

    public Boolean getWithSearchEnhance() {
        return withSearchEnhance;
    }

    public void setWithSearchEnhance(Boolean withSearchEnhance) {
        this.withSearchEnhance = withSearchEnhance;
    }


}


