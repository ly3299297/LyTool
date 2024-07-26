package com.ly.gpt.generater.text.baichuan;



import com.ly.gpt.generater.text.ChatMessage;
import com.ly.gpt.generater.text.Text2TextInput;
import com.ly.gpt.common.Constants;

import java.util.List;


public class BaichuanText2TextInput implements Text2TextInput {
    /*!
        temperature
        取值范围: [.0f, 1.0f]。 多样性，越高，多样性越好, 缺省 0.3
     */
    float temperature=0.3f;
    /*!
        top_p
       取值范围: [.0f, 1.0f)。值越小，越容易出头部, 缺省 0.85
     */
    float topP=0.85f;
    /*!
        with_search_enhance
        True: 开启web搜索增强，搜索增强会产生额外的费用, 缺省 False
        开启需要额外收费 0.03元/次

     */
    Boolean withSearchEnhance=false;
    /*!
       对话消息列表 (历史对话按从老到新顺序填入)
     */
    List<ChatMessage> messages;




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
        if (temperature<0 || temperature>1F) {
            throw new IllegalArgumentException( "temperature 参数范围 0-1");
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

    @Override
    public float getTemperature() {
        return temperature;
    }

    public float getTopP() {
        return topP;
    }
}
