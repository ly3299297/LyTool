package com.ly.gpt.generater.text.moonshot;



import com.ly.gpt.generater.text.ChatMessage;
import com.ly.gpt.generater.text.Text2TextInput;
import com.ly.gpt.common.Constants;

import java.util.List;


public class MoonshotText2TextInput  implements Text2TextInput {

    /*!
        temperature
       使用什么采样温度，介于 0 和 1 之间。较高的值（如 0.7）将使输出更加随机，而较低的值（如 0.2）将使其更加集中和确定性
       如果设置，值域须为 [0, 1] 我们推荐 0.3，以达到较合适的效果
     */
    float temperature=0.3f;
    /*!
        top_p
       另一种采样方法，即模型考虑概率质量为 topP 的标记的结果。因此，0.1 意味着只考虑概率质量最高的 10% 的标记。一般情况下，我们建议改变这一点或温度，但不建议 同时改变
        缺省 1.0
     */

    float topP=1.0f;

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

}
