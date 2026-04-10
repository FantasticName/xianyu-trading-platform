package io.github.fantasticname.xianyutradingplatform.service.impl;

import io.github.fantasticname.xianyutradingplatform.common.ErrorCode;
import io.github.fantasticname.xianyutradingplatform.dao.ConversationDao;
import io.github.fantasticname.xianyutradingplatform.dao.ListingDao;
import io.github.fantasticname.xianyutradingplatform.dao.MessageDao;
import io.github.fantasticname.xianyutradingplatform.dao.UserDao;
import io.github.fantasticname.xianyutradingplatform.exception.BusinessException;
import io.github.fantasticname.xianyutradingplatform.model.Conversation;
import io.github.fantasticname.xianyutradingplatform.model.Listing;
import io.github.fantasticname.xianyutradingplatform.model.Message;
import io.github.fantasticname.xianyutradingplatform.model.User;
import io.github.fantasticname.xianyutradingplatform.service.ConversationService;
import io.github.fantasticname.xianyutradingplatform.service.dto.CreateConversationRequest;
import io.github.fantasticname.xianyutradingplatform.service.dto.SendMessageRequest;
import io.github.fantasticname.xianyutradingplatform.service.vo.ConversationVO;
import io.github.fantasticname.xianyutradingplatform.service.vo.MessageVO;
import io.github.fantasticname.xianyutradingplatform.util.TxManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author FantasticName
 */
public class ConversationServiceImpl implements ConversationService {
    private final DataSource ds;
    private final ConversationDao conversationDao;
    private final MessageDao messageDao;
    private final ListingDao listingDao;
    private final UserDao userDao;

    public ConversationServiceImpl(DataSource ds, ConversationDao conversationDao, MessageDao messageDao, ListingDao listingDao, UserDao userDao) {
        this.ds = ds;
        this.conversationDao = conversationDao;
        this.messageDao = messageDao;
        this.listingDao = listingDao;
        this.userDao = userDao;
    }

    @Override
    public ConversationVO getOrCreate(String userId, CreateConversationRequest request) {
        if (request == null || request.getListingId() == null || request.getListingId().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        Listing listing = listingDao.findById(request.getListingId().trim());
        if (listing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        String sellerId = listing.getSellerId();
        if (userId.equals(sellerId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能和自己发起会话");
        }

        Conversation conv = TxManager.executeInTransaction(ds, () -> {
            Conversation existed = conversationDao.findByUnique(listing.getId(), userId, sellerId);
            if (existed != null) {
                return existed;
            }
            return conversationDao.insert(listing.getId(), userId, sellerId);
        });
        return toVO(userId, conv);
    }

    @Override
    public List<ConversationVO> list(String userId, int limit) {
        int l = Math.min(Math.max(limit, 1), 50);
        List<Conversation> list = conversationDao.listByUser(userId, l);
        List<ConversationVO> out = new ArrayList<>();
        for (Conversation c : list) {
            out.add(toVO(userId, c));
        }
        return out;
    }

    @Override
    public List<MessageVO> listMessages(String userId, String conversationId, int limit) {
        Conversation c = conversationDao.findById(conversationId);
        if (c == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会话不存在");
        }
        if (!userId.equals(c.getBuyerId()) && !userId.equals(c.getSellerId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限访问该会话");
        }
        int l = Math.min(Math.max(limit, 1), 200);
        List<Message> list = messageDao.listByConversation(conversationId, l);
        List<MessageVO> out = new ArrayList<>();
        for (Message m : list) {
            MessageVO vo = new MessageVO();
            vo.setId(m.getId());
            vo.setConversationId(m.getConversationId());
            vo.setSenderId(m.getSenderId());
            vo.setContent(m.getContent());
            vo.setCreatedAt(m.getCreatedAt() == null ? null : m.getCreatedAt().toString());
            out.add(vo);
        }
        return out;
    }

    @Override
    public void sendMessage(String userId, String conversationId, SendMessageRequest request) {
        if (request == null || request.getContent() == null || request.getContent().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "消息不能为空");
        }
        String content = request.getContent().trim();
        if (content.length() > 1000) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "消息过长");
        }
        TxManager.executeInTransaction(ds, () -> {
            Conversation c = conversationDao.findById(conversationId);
            if (c == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "会话不存在");
            }
            if (!userId.equals(c.getBuyerId()) && !userId.equals(c.getSellerId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无权限访问该会话");
            }
            messageDao.insert(conversationId, userId, content);
            conversationDao.touch(conversationId);
            return null;
        });
    }

    private ConversationVO toVO(String userId, Conversation c) {
        Listing listing = listingDao.findById(c.getListingId());
        String peerId = userId.equals(c.getBuyerId()) ? c.getSellerId() : c.getBuyerId();
        User peer = userDao.findById(peerId);
        ConversationVO vo = new ConversationVO();
        vo.setId(c.getId());
        vo.setListingId(c.getListingId());
        vo.setListingTitle(listing == null ? null : listing.getTitle());
        vo.setPeerUserId(peerId);
        vo.setPeerNickname(peer == null ? null : peer.getNickname());
        vo.setPeerAvatarUrl(peer == null ? null : peer.getAvatarUrl());
        vo.setUpdatedAt(c.getUpdatedAt() == null ? null : c.getUpdatedAt().toString());
        return vo;
    }
}

