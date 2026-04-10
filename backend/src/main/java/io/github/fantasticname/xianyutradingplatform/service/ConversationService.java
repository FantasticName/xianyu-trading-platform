package io.github.fantasticname.xianyutradingplatform.service;

import io.github.fantasticname.xianyutradingplatform.service.dto.CreateConversationRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.SendMessageRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.ConversationVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.MessageVO;

import java.util.List;

/**
 * @author FantasticName
 */
public interface ConversationService {
    ConversationVO getOrCreate(String userId, CreateConversationRequest request);

    List<ConversationVO> list(String userId, int limit);

    List<MessageVO> listMessages(String userId, String conversationId, int limit);

    void sendMessage(String userId, String conversationId, SendMessageRequest request);
}

