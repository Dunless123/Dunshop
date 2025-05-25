package com.dundunteam.pojo.entity;




public class Message {
    private String toId;
    private String message;

    public String getToName() {
        return toId;
    }

    public void setToName(String toName) {
        this.toId = toName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
