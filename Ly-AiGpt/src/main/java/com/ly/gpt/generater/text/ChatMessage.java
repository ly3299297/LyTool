package com.ly.gpt.generater.text;


import lombok.Data;

@Data
public class ChatMessage {

    /**
     * system可以用于角色制定
     *  {
     *             "role": "system",
     *             "content": "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。"
     *         },
     */
    private  String role;  //角色  system user ,assistant.
    private  String content; // 文本  提问或者回答   .

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public ChatMessage() {
    }


}
