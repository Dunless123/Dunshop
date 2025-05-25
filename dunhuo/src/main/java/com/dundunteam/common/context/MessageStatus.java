package com.dundunteam.common.context;
public enum MessageStatus {
    SENT,     // 已发送（默认）
    DELIVERED,// 已送达（服务端确认接收方在线）
    READ,     // 已读（接收方查看）
    WITHDRAWN // 已撤回
}