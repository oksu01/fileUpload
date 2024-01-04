package com.fileShare.domain.board.service;

import com.fileShare.domain.board.dto.BoardApi;
import com.fileShare.domain.board.entity.Board;
import com.fileShare.domain.board.repository.BoardRepository;
import com.fileShare.domain.file.dto.FileApi;
import com.fileShare.domain.file.entity.File;
import com.fileShare.domain.reply.entity.Reply;
import com.fileShare.domain.member.entity.Member;
import com.fileShare.global.exception.BoardNotFoundException;
import com.fileShare.global.exception.MemberAccessDeniedException;
import com.global.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BoardServiceTest extends ServiceTest {

    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardService boardService;



//    @Test
//    @DisplayName("게시판을 등록한다")
//    void createBoard() {
//        //given
//        Member member = createAndSaveMember();
//        BoardApi board = BoardApi.builder()
//                .title("title")
//                .build();
//
//        //when
//        Long boardId = boardService.createBoard(board, member.getMemberId());
//
//        //then
//        assertThat(boardId).isNotNull();
//    }

    @Test
    @DisplayName("게시판을 수정한다")
    void updateBoard() {
        //given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        BoardApi boardApi = BoardApi.builder()
                .title("new title")
                .build();

        //when
        String modifiedBoard = boardService.updateBoard(1L, boardApi, member.getMemberId());

        //then
        assertThat(modifiedBoard).isEqualTo(new BoardApi().getTitle());

    }

    @Test
    @DisplayName("게시판을 삭제한다")
    void deleteBoard() {
        //given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);

        //when
        boardService.deleteBoard(board.getBoardId(), member.getMemberId());

        //then
        assertThat(boardRepository.findById(board.getBoardId())).isEmpty();
    }

    @Test
    @DisplayName("게시판을 삭제하면 댓글과 파일도 삭제된다")
    void deleteReplyAndFile() {
        // given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);
        Reply reply = createAndSaveReply(member, board);
        File file = createAndSaveFile(board);

        // when
        boardService.deleteBoard(board.getBoardId(), member.getMemberId());

        // then
        assertThat(boardRepository.findById(board.getBoardId())).isEmpty();
        assertThat(replyRepository.findAllRepliesByMemberId(member.getMemberId())).isEmpty();
        assertThat(fileRepository.findAllFilesByMemberId(member.getMemberId())).isEmpty();
    }

    @Test
    @DisplayName("게시판을 조회한다")
    void getBoard() {
        //given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);

        //when
        BoardApi myBoard = boardService.getBoard(member.getMemberId(), board.getBoardId());

        //then
        assertThat(board.getTitle()).isEqualTo(myBoard.getTitle());
    }

//    @Test
//    @DisplayName("비회원은 게시판을 등록 할 수 없다")
//    void notCreate() {
//        //given
//        Member member = createAndSaveMember();
//        Board board = createAndSaveBoard(member);
//
//        //when & then
//        assertThrows(MemberAccessDeniedException.class, ()-> {
//            boardService.createBoard(new BoardApi(), file);
//        });
//    }

    @Test
    @DisplayName("비회원은 게시판을 수정 할 수 없다")
    void notModified() {
        //given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);

        //when & then
        assertThrows(MemberAccessDeniedException.class, ()-> {
            boardService.updateBoard(board.getBoardId(), new BoardApi(), -1L);
        });
    }

    @Test
    @DisplayName("비회원은 게시판을 삭제 할 수 없다")
    void notDelete() {
        //given
        Member member = createAndSaveMember();
        Board board = createAndSaveBoard(member);

        //when & then
        assertThrows(MemberAccessDeniedException.class, () -> {
            boardService.deleteBoard(board.getBoardId(), -1L);
        });

    }

    @Test
    @DisplayName("게시판 삭제시 게시판이 존재하지 않으면 'BoardNotFoundException' 이 발생한다")
    void notExist() {
        //given
        Member member = createAndSaveMember();

        //when & then
        assertThrows(BoardNotFoundException.class, () -> {
            boardService.deleteBoard(null, member.getMemberId());
        });
    }

}