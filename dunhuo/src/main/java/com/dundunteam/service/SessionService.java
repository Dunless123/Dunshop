package com.dundunteam.service;


import java.util.List;

/**
 * 会话管理服务接口
 * - 负责用户会话的创建、更新和查询
 */
public interface SessionService {

    /**
     * 创建或获取会话ID
     * @param user1 用户A的openId
     * @param user2 用户B的openId
     * @return 唯一会话ID（基于用户ID生成）
     */
    String createOrGetSession(String user1, String user2);

    /**
     * 更新会话活跃时间
     * @param sessionId 会话ID
     */
    void updateSessionActivity(String sessionId);

    /**
     * 获取用户的活跃会话列表
     * @param openId 用户标识
     * @return 该用户参与的所有会话ID列表
     */
    List<String> getActiveSessions(String openId);

    /**
     * 删除会话记录
     * @param sessionId 会话ID
     */
    void deleteSession(String sessionId);
}