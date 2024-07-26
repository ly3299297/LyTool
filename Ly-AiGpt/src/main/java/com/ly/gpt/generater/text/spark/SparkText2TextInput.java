package com.ly.gpt.generater.text.spark;


import com.ly.gpt.generater.text.Text2TextInput;
import com.ly.gpt.generater.text.ChatMessage;
import com.ly.gpt.common.Constants;

import java.util.List;

public class SparkText2TextInput implements Text2TextInput {

    /*!
        temperature
       取值范围 (0，1] ，默认值0.5	核采样阈值。用于决定结果随机性，取值越高随机性越强即相同的问题得到的不同答案的可能性越高
     */ float temperature = 0.5f;
    /*!
        top_k
        取值为[1，6],默认为4	从k个候选中随机选择⼀个（⾮等概率）
     */ int topP = 4;

    List<ChatMessage> messages;


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

        this.messages = messages;
    }

    @Override
    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        if (temperature < 0 || temperature > 1F) {
            throw new IllegalArgumentException("temperature 参数范围 0-1");
        }
        this.temperature = temperature;
    }

    public int getTopP() {
        return topP;
    }

    public void setTopP(int topP) {
        this.topP = topP;
    }


}
