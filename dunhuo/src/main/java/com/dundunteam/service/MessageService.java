package com.dundunteam.service;


import com.dundunteam.common.context.MessageStatus;
import com.dundunteam.pojo.entity.BaseMessage;
import java.util.List;

/**
 * 消息服务接口
 * - 定义消息存储、查询、去重等核心操作
 */
public interface MessageService {

    /**
     * 保存消息到指定会话
     * @param message 消息实体
     * @param sessionId 会话唯一标识
     */
    void saveMessage(BaseMessage message, String sessionId);

    /**
     * 分页查询历史消息
     * @param sessionId 会话ID
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 消息列表（按时间倒序）
     */
    List<BaseMessage> getHistoryMessages(String sessionId, int page, int size);

    /**
     * 更新消息状态（如已读、已撤回）
     * @param messageId 消息唯一标识
     * @param status 新状态
     */
    void updateMessageStatus(String messageId, MessageStatus status);

    /**
     * 校验消息是否重复（5分钟窗口期）
     * @param messageId 消息唯一ID
     * @return true=重复消息 false=新消息
     */
    boolean isDuplicateMessage(String messageId);
    /**
     * 存储离线消息
     * @param receiverOpenId 接收方openId
     * @param message 消息对象
     */
    void storeOfflineMessage(String receiverOpenId, BaseMessage message);

    /**
     * 获取并清空离线消息
     * @param receiverOpenId 接收方openId
     * @return 离线消息列表
     */
    List<BaseMessage> getOfflineMessages(String receiverOpenId);

    /**
     * 处理用户离线消息
     * @param openId 用户标识
     * @return 处理的离线消息数量
     */
    int processOfflineMessages(String openId);
}