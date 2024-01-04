package com.fileShare.domain.reply.service;

import com.fileShare.domain.board.dto.BoardApi;
import com.fileShare.domain.board.entity.Board;
import com.fileShare.domain.board.repository.BoardRepository;
import com.fileShare.domain.reply.dto.RecommendStatus;
import com.fileShare.domain.reply.dto.ReplyApi;
import com.fileShare.domain.reply.dto.ReplyInfo;
import com.fileShare.domain.reply.dto.SubReplyApi;
import com.fileShare.domain.reply.entity.Reply;
import com.fileShare.domain.reply.repository.ReplyRepository;
import com.fileShare.domain.member.entity.Member;
import com.fileShare.domain.member.repository.MemberRepository;
import com.fileShare.global.exception.ReplyNotFoundException;
import com.fileShare.global.exception.MemberNotFoundException;
import com.global.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ReplyServiceTest extends ServiceTest {

    @Autowired
    private ReplyService replyService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("댓글을 등록할 수 있다")
    void createReply() {
        // given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        ReplyApi replyApi = ReplyApi.builder()
                .content("content")
                .board(board)
                .memberId(member.getMemberId())
                .build();

        // when
        Long replyId = replyService.createReply(replyApi, member.getMemberId(), board.getBoardId());

        // then
        assertThat(replyId).isNotNull();
        Reply saveReply = replyRepository.findById(replyId).orElse(null);
        assertThat(saveReply.getContent()).isEqualTo("content"); // 내용물 확인


    }

    @Test
    @DisplayName("댓글을 삭제한다")
    void deleteReply() {
        //given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        Reply mainReply = createAndSaveReply(member, board);

        //when
        replyService.deleteReply(mainReply.getReplyId(), member.getMemberId());

        //then
        Optional<Reply> deletedReply = replyRepository.findById(mainReply.getReplyId());
        assertThat(deletedReply).isEmpty();

    }

    @Test
    @DisplayName("댓글을 수정한다")
    void updateReply() {
        //given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        Reply reply = createAndSaveReply(member, board);
        ReplyApi newReply = ReplyApi.builder()
                .content("new content")
                        .build();
        //when
        String modifiedReply = replyService.updateReply(reply.getReplyId(), newReply, member.getMemberId());

        //then
        assertThat(modifiedReply).isEqualTo(newReply.getContent());

    }

    @Test
    @DisplayName("댓글을 한 건 조회한다")
    void getReply() {
        //given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        Reply reply = createAndSaveReply(member, board);

        //when
        ReplyApi replyId = replyService.getReply(
                reply.getReplyId(),
                member.getMemberId(),
                new ReplyApi()
        );

        //then
        assertThat(replyId).isNotNull();
        assertThat(replyId.getContent()).isEqualTo("content");

    }

    @Test
    @DisplayName("댓글 목록으로 조회한다")
    void getReplies() {
        // given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        createAndSaveReply(member, board);
        createAndSaveReply(member, board);
        createAndSaveReply(member, board);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ReplyInfo> replyInfoPage = replyService.getReplies(board.getBoardId(), 0, 10);

        // then
        assertThat(replyInfoPage).isNotNull();
        assertThat(replyInfoPage.getContent()).hasSize(3);
        assertThat(replyInfoPage.getTotalElements()).isEqualTo(3);
        assertThat(replyInfoPage.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("대댓글을 등록 할 수 있다")
    void createSubReply() {
        //given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        Reply mainReply = createMainReplyAndSave(member, board);
        Reply subReply = createSubReplyAndSave(member, board, mainReply);
        SubReplyApi subReplyApi = SubReplyApi.builder()
                .memberId(member.getMemberId())
                .replyId(mainReply.getReplyId())
                .content("subReply")
                .build();

        //when
        Long subReplyId = replyService.createSubReply(mainReply.getReplyId(), subReplyApi, member.getMemberId());

        //then
        assertThat(subReplyId).isNotNull();
        Reply saveSubReply = replyRepository.findById(subReplyId).orElse(null);
        assertThat(saveSubReply.getContent()).isEqualTo("subReply"); // 내용물 확인
    }

    @Test
    @DisplayName("비회원은 댓글을 등록 할 수 없다")
    void accessDeniedUser() {
        //given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        Reply reply = createAndSaveReply(member, board);

        //when & then
        if(reply.getMember().getMemberId() != member.getMemberId()) {
            assertThrows(MemberNotFoundException.class, () ->
                    replyService.createReply(new ReplyApi(), member.getMemberId(), board.getBoardId()));
        }
    }

    @Test
    @DisplayName("비회원은 댓글을 수정 할 수 없다")
    void canNotModified() {
        //given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        Reply reply = createAndSaveReply(member, board);

        //when & then
        if(reply.getMember().getMemberId() != member.getMemberId()) {
            assertThrows(MemberNotFoundException.class, () ->
                    replyService.updateReply(reply.getReplyId(), new ReplyApi(), member.getMemberId()));
        }
    }

    @Test
    @DisplayName("비회원은 댓글을 삭제 할 수 없다")
    void notDelete() {
        //given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        Reply reply = createAndSaveReply(member, board);

        //when & then
        if(reply.getMember().getMemberId() != member.getMemberId()) {
            assertThrows(MemberNotFoundException.class, () ->
                    replyService.deleteReply(reply.getReplyId(), member.getMemberId()));
        }
    }

    @Test
    @DisplayName("댓글 삭제시 존재하지 않는 댓글이면 'ReplyNotFoundException' 이 발생한다")
    void replyNFException() {
        //given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);

        //when & then
        assertThrows(ReplyNotFoundException.class, () -> {
            replyService.deleteReply(999999999L, member.getMemberId());
        });
    }

    @Test
    @DisplayName("댓글 단 건 조회시 댓글이 존재하지 않으면 'ReplyNotFoundException' 이 발생한다")
    void ReNotFound() {
        // given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);

        // when & then
        assertThrows(ReplyNotFoundException.class, () -> {
            replyService.getReply(99999999L, member.getMemberId(), new ReplyApi());
        });
    }

    @Test
    @DisplayName("댓글 수정시 존재하지 않는 댓글이면 'ReplyNotFoundException' 이 발생한다")
    void notModified() {
        // given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);

        // when & then
        assertThrows(ReplyNotFoundException.class, () -> {
            replyService.updateReply(99999999L, new ReplyApi(), member.getMemberId());
        });
    }

    @Test
    @DisplayName("댓글을 추천 할 수 있다")
    public void recommendReply() {
        // given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        Reply reply = createAndRecommend(member, board);

        // when
        RecommendStatus recommendStatus = replyService.recommendReply(reply.getReplyId(), member.getMemberId());

        // then
        assertThat(RecommendStatus.UPVOTE).isEqualTo(recommendStatus);
    }

    @Test
    @DisplayName("댓글을 비추천 할 수 있다")
    public void nonRecommendReply() {
        // given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        Reply reply = createAndRecommend(member, board);

        // when
        RecommendStatus recommendStatus = replyService.nonRecommendReply(reply.getReplyId(), member.getMemberId());

        // then
        assertThat(RecommendStatus.DOWNVOTE).isEqualTo(recommendStatus);
    }

    @Test
    @DisplayName("댓글 추천 후 비추천을 한다")
    void recommendAndNonRecommendReply() {
        // given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        Reply reply = createAndRecommend(member, board);

        // when
        replyService.recommendReply(reply.getReplyId(), member.getMemberId());
        replyService.nonRecommendReply(reply.getReplyId(), member.getMemberId());

        // then
        assertThat(reply.hasDownvoted(member.getMemberId())).isTrue();
    }

    @Test
    @DisplayName("비추천을 취소하고 none으로 변경 할 수 있다")
    void changeRecommendStatusNone() {
        // given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        Reply reply = createAndRecommend(member, board);

        // when
       RecommendStatus nonRecommend =  replyService.nonRecommendReply(reply.getReplyId(), member.getMemberId());
       RecommendStatus none = replyService.nonRecommendReply(reply.getReplyId(), member.getMemberId());

        // then
        assertThat(none).isEqualTo(RecommendStatus.NONE);
        assertThat(reply.hasDownvoted(member.getMemberId())).isFalse();
    }

    @Test
    @DisplayName("추천을 취소하고 none으로 변경 할 수 있다")
    void changeRecommendStatus() {
        // given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        Reply reply = createAndRecommend(member, board);

        // when
        RecommendStatus recommend = replyService.recommendReply(reply.getReplyId(), member.getMemberId());
        RecommendStatus none = replyService.recommendReply(reply.getReplyId(), member.getMemberId());

        // then
        assertThat(none).isEqualTo(RecommendStatus.NONE);
        assertThat(reply.hasUpvoted(member.getMemberId())).isFalse();
    }

}


