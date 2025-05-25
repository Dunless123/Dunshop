package com.dundunteam.controller;

//要改
import com.dundunteam.pojo.entity.BaseMessage;
import com.dundunteam.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息查询控制器
 * - 提供历史消息查询接口
 * - 支持分页参数控制
 */
@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    // 通过构造器注入消息服务
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 获取指定会话的历史消息
     * @param sessionId 会话唯一标识（由两个用户openId生成的MD5值）
     * @param page 页码（从1开始，默认1）
     * @param size 每页数量（默认20，最大100）
     * @return 分页消息列表（HTTP 200）
     */
    @GetMapping("/{sessionId}")
    public ResponseEntity<List<BaseMessage>> getHistoryMessages(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // 参数校验
        page = Math.max(page, 1);
        size = Math.min(Math.max(size, 1), 100);

        // 调用服务层获取数据
        List<BaseMessage> messages = messageService.getHistoryMessages(sessionId, page, size);

        return ResponseEntity.ok(messages);
    }
}