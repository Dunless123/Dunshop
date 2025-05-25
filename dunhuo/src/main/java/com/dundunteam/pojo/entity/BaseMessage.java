package com.dundunteam.pojo.entity;

import java.util.UUID;
import com.dundunteam.common.context.MessageStatus;

public class BaseMessage {

        private String messageId = UUID.randomUUID().toString();
        private String senderOpenId;    // 发送方openId
        private String receiverOpenId;  // 接收方openId
        private String content;         // 消息内容
        private Long timestamp = System.currentTimeMillis();

    public String getMessageId() {
        return messageId;
    }

    public String getSenderOpenId() {
        return senderOpenId;
    }

    public String getReceiverOpenId() {
        return receiverOpenId;
    }

    public String getContent() {
        return content;
    }


    private MessageStatus status = MessageStatus.SENT; // 默认状态

    public Long getTimestamp() {
        return timestamp;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setSenderOpenId(String senderOpenId) {
        this.senderOpenId = senderOpenId;
    }

    public void setReceiverOpenId(String receiverOpenId) {
        this.receiverOpenId = receiverOpenId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public BaseMessage(String messageId, String senderOpenId, String receiverOpenId, String content, Long timestamp) {
        this.messageId = messageId;
        this.senderOpenId = senderOpenId;
        this.receiverOpenId = receiverOpenId;
        this.content = content;
        this.timestamp = timestamp;
    }
}
