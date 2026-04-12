package io.github.fantasticname.xianyutradingplatform.service;

import io.github.fantasticname.xianyutradingplatform.service.dto.CreateConversationRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.SendMessageRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.ConversationVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.MessageVO;

import java.util.List;

/**
 * 会话服务接口
 * 处理买家与卖家之间的沟通会话及消息发送。
 *
 * @author FantasticName
 */
public interface ConversationService {
    /**
     * 获取或创建会话
     * 如果买家对某个商品发起咨询，若已存在会话则返回，否则新建。
     *
     * @param userId  发起会话的用户ID（买家）
     * @param request 包含商品ID的请求对象
     * @return 会话详情 VO
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果商品不存在或尝试与自己发起会话
     */
    ConversationVO getOrCreate(String userId, CreateConversationRequest request);

    /**
     * 获取用户的会话列表
     * 按最后更新时间倒序排列。
     *
     * @param userId 当前用户ID
     * @param limit  返回结果的最大数量
     * @return 会话列表
     */
    List<ConversationVO> list(String userId, int limit);

    /**
     * 获取指定会话的历史消息
     *
     * @param userId         当前用户ID（必须是会话参与者）
     * @param conversationId 会话ID
     * @param limit          返回消息的最大数量
     * @return 消息列表
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果会话不存在或用户无权访问
     */
    List<MessageVO> listMessages(String userId, String conversationId, int limit);

    /**
     * 在指定会话中发送消息
     *
     * @param userId         发送者用户ID
     * @param conversationId 会话ID
     * @param request        包含消息内容的请求对象
     * @throws io.github.fantasticname.xianyutradingplatform.exception.BusinessException 如果消息内容为空、过长、会话不存在或用户无权访问
     */
    void sendMessage(String userId, String conversationId, SendMessageRequest request);
}

