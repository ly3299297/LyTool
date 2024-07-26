package com.ly.gpt.generater.text;


public class Text2TextStreamOutput  {


     private Integer promptTokens;

     private Integer completionTokens;
     private Integer totalTokens;


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
