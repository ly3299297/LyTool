package com.ly.gpt.generater.text.dashscope;


import com.ly.gpt.generater.text.ChatMessage;
import com.ly.gpt.generater.text.Text2TextInput;
import com.ly.gpt.common.Constants;

import java.util.List;

public class DashscopeText2TextInput implements Text2TextInput {
    /*!
        temperature
        用于控制模型回复的随机性和多样性。具体来说，temperature值控制了生成文本时对每个候选词的概率分布进行平滑的程度。
        较高的temperature值会降低概率分布的峰值，使得更多的低概率词被选择，生成结果更加多样化；而较低的temperature值则会增强概率分布的峰值，
        使得高概率词更容易被选择，生成结果更加确定。
        取值范围： [0, 2)，不建议取值为0，无意义。
        默认 0.85
     */
    float temperature=0.85f;
    /*!
        top_p
       生成过程中的核采样方法概率阈值，例如，取值为0.8时，仅保留概率加起来大于等于0.8的最可能token的最小集合作为候选集。
       取值范围为（0,1.0)，取值越大，生成的随机性越高；取值越低，生成的确定性越高。
        默认 0.8
     */
    float topP=0.8f;
    /*!
        enable_search
        True：启用互联网搜索，模型会将搜索结果作为文本生成过程中的参考信息，但模型会基于其内部逻辑判断是否使用互联网搜索结果。
        False（默认）：关闭互联网搜索。
        原字段名  enable_search
     */
    Boolean withSearchEnhance=false;
    /*!
       对话消息列表 (历史对话按从老到新顺序填入)
     */
    List<ChatMessage> messages;


    public float getTemperature() {
        return temperature;
    }

    public float getTopP() {
        return topP;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }


    public Boolean getWithSearchEnhance() {
        return withSearchEnhance;
    }

    public void setWithSearchEnhance(Boolean withSearchEnhance) {
        this.withSearchEnhance = withSearchEnhance;
    }

    public void setTemperature(float temperature) {
        if (temperature<0 || temperature>2F) {
            throw new IllegalArgumentException( "temperature 参数范围 0-2");
        }
        this.temperature = temperature;
    }

    public void setTopP(float topP) {
        if (topP<0 || topP>1F) {
            throw new IllegalArgumentException( "topP 参数范围 0-1");
        }
        this.topP = topP;
    }

    public void setMessages(List<ChatMessage> messages) {
        if(messages==null || messages.size()==0){
            throw new IllegalArgumentException("messages不能为空");
        }

        ChatMessage chatMessage = messages.get(messages.size() - 1);
        if(!Constants.AI_ROLE_USER.equals(chatMessage.getRole())){
            throw new IllegalArgumentException("messages 最后一个role必须是user");
        }

        this.messages = messages;
    }
}
