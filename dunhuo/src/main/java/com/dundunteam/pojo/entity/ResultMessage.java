package com.dundunteam.pojo.entity;


public class ResultMessage {

    private boolean isSystem;
    private String fromId;
    private Object message;//如果是系统消息是数组

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromName) {
        this.fromId = fromName;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
