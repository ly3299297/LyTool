package com.ly.gpt.generater.text.moonshot;


import com.ly.gpt.generater.text.Text2TextOutput;
import com.ly.gpt.generater.text.ChatMessage;

public class MoonshotText2TextOutput implements Text2TextOutput {


    private Integer promptTokens=0;

    private Integer completionTokens=0;
    private Integer totalTokens=0;


    private ChatMessage message=new ChatMessage();

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public Integer getCompletionTokens() {
        return completionTokens;
    }

    public Integer getTotalTokens() {
        return totalTokens;
    }

    public ChatMessage getMessage() {
        return message;
    }


    public void setPromptTokens(Integer promptTokens) {
        this.promptTokens = promptTokens;
    }

    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens;
    }

    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }



}
