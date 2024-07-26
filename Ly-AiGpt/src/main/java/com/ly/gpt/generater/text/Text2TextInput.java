package com.ly.gpt.generater.text;


import com.ly.gpt.generater.ContentInput;

import java.util.List;

public interface Text2TextInput extends ContentInput {

     //List<ChatMessage> getMessages();

     void setMessages(List<ChatMessage> messages);


     float getTemperature();

     //float getTopP();

}
