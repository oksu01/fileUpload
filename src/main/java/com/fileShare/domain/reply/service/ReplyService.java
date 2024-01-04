package com.fileShare.domain.reply.service;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fileShare.domain.board.repository.BoardRepository;
import com.fileShare.domain.reply.dto.ReplyApi;
import com.fileShare.domain.reply.dto.ReplyInfo;
import com.fileShare.domain.reply.dto.RecommendStatus;
import com.fileShare.domain.reply.dto.SubReplyApi;
import com.fileShare.domain.reply.entity.Reply;
import com.fileShare.domain.reply.repository.ReplyRepository;
import com.fileShare.domain.member.repository.MemberRepository;
import com.fileShare.global.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;



    // 댓글 조회
    @Transactional(readOnly = true)
    public ReplyApi getReply(Long replyId, Long loginId, ReplyApi replyApi) {

        validateUser(loginId);

        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(ReplyNotFoundException::new);

        List<Reply> subReplies = reply.getSubReplies(); // 대댓글 가져오기

        ReplyApi replyResponse = ReplyApi.builder()
                .content(reply.getContent())
                .subReplies(subReplies.stream()
                        .map(subReply -> ReplyApi.builder()
                                .content(subReply.getContent())
                                .board(replyApi.getBoard())
                                .memberId(replyApi.getMemberId())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return replyResponse;
    }


    // 댓글 목록 조회
    @Transactional(readOnly = true)
    public Page<ReplyInfo> getReplies(Long boardId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Reply> replyPage = replyRepository.findRepliesByBoardId(boardId, pageable);

        List<ReplyInfo> replyInfos = replyPage.getContent().stream()
                .map(this::mapToReplyInfo)
                .collect(Collectors.toList());

        return new PageImpl<>(replyInfos, pageable, replyPage.getTotalElements());
    }

    // 댓글 등록
    public Long createReply(ReplyApi replyApi, Long loginId, Long boardId) {

        validateUser(loginId);
        validateBoard(boardId);

        Reply reply = Reply.newReply(replyApi, boardId);

        replyRepository.save(reply);

        return reply.getReplyId();
    }


    // 대댓글 등록
    public Long createSubReply(Long replyId, SubReplyApi replyApi, Long loginId) {

        validateUser(loginId);

        Reply mainReply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);

        Reply subReply = Reply.newSubReply(mainReply, replyApi, loginId);

        Reply savedSubReply = replyRepository.save(subReply);

        return savedSubReply.getReplyId();
    }

    // 댓글 수정
    public String updateReply(Long replyId, ReplyApi replyApi, Long loginId) {

        validateUser(loginId);

        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);

          return reply.updateReply(replyApi.getContent());
    }

    // 댓글 & 대댓글 삭제
    public void deleteReply(Long replyId, Long loginId) {

        validateUser(loginId);

        Reply deleteReply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);

        if (deleteReply.getMainReply() != null) {
            replyRepository.delete(deleteReply);
        } else {
            List<Reply> subReplies = deleteReply.getSubReplies();
            for (Reply subReply : subReplies) {
                replyRepository.delete(subReply);
            }
            replyRepository.delete(deleteReply);
        }
    }

    public RecommendStatus recommendReply(Long replyId, Long loginId) {

        validateUser(loginId);
        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);
        RecommendStatus recommendStatus = validateRecommendStatus(replyId, loginId);

        switch (recommendStatus) {
            case NONE:
                reply.increaseRecommend(); // 추천 증가
                reply.getUpvoterMembers().add(loginId);
                reply.increaseRecommend();
                return RecommendStatus.UPVOTE;
            case UPVOTE:
                reply.decreaseRecommend(); // 추천 취소 -> none으로 변경됨
                return RecommendStatus.NONE;
            case DOWNVOTE:
                reply.decreaseRecommend(); // 비추천 취소하고 추천 -> 추천 + 1
                reply.increaseRecommend();
                reply.getUpvoterMembers().add(loginId);
                return RecommendStatus.UPVOTE;
            default:
                return RecommendStatus.NONE;
        }
    }

    public RecommendStatus nonRecommendReply(Long replyId, Long loginId) {

        validateUser(loginId);
        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);
        RecommendStatus recommendStatus = validateRecommendStatus(replyId, loginId);

        switch (recommendStatus) {
            case NONE:
                reply.decreaseRecommend(); // 비추천
                reply.getDownvoterMembers().add(loginId);
                return RecommendStatus.DOWNVOTE;
            case UPVOTE:
                reply.increaseRecommend(); // 추천 취소하고 비추천
                return RecommendStatus.DOWNVOTE;
            case DOWNVOTE:// 비추천 취소하고 none
                reply.getDownvoterMembers().remove(loginId); // 다시 내려주어야 함
                return RecommendStatus.NONE;
            default:
                return RecommendStatus.NONE;
        }
    }

    public void deleteReplyById(Long replyId) {

        validateReply(replyId);

        replyRepository.deleteById(replyId);
    }


    public void validateUser(Long loginId) {
        if (loginId < 1) {
            throw new MemberAccessDeniedException();
        }
        memberRepository.findById(loginId).orElseThrow(MemberNotFoundException::new);
    }

    public void validateReply(Long replyId) {
        replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);
    }

    public void validateBoard(Long boardId) {
        boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
    }

    public ReplyInfo mapToReplyInfo(Reply reply) {

        return ReplyInfo.builder()
                .content(reply.getContent())
                .build();
    }

    public RecommendStatus validateRecommendStatus(Long replyId, Long loginId) {

        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);

        boolean hasUpvoted = reply.getUpvoterMembers().contains(loginId);
        boolean hasDownvoted = reply.getDownvoterMembers().contains(loginId);

        if (hasUpvoted && !hasDownvoted) {
            return RecommendStatus.UPVOTE; // 추천한 경우
        } else if (!hasUpvoted && hasDownvoted) {
            return RecommendStatus.DOWNVOTE; // 비추천한 경우
        } else {
            return RecommendStatus.NONE; // 아무것도 하지 않은 경우
        }
    }
}
