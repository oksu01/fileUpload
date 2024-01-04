package com.fileShare.domain.board.service;


import com.fileShare.domain.board.dto.BoardApi;
import com.fileShare.domain.board.entity.Board;
import com.fileShare.domain.board.repository.BoardRepository;
import com.fileShare.domain.file.entity.File;
import com.fileShare.domain.file.repository.FileRepository;
import com.fileShare.domain.reply.entity.Reply;
import com.fileShare.domain.reply.repository.ReplyRepository;
import com.fileShare.domain.member.entity.Member;
import com.fileShare.domain.member.repository.MemberRepository;
import com.fileShare.global.exception.BoardNotFoundException;
import com.fileShare.global.exception.MemberAccessDeniedException;
import com.fileShare.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service

@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository userRepository;
    private final FileRepository fileRepository;
    private final ReplyRepository replyRepository;


    // 게시판 등록
    public Long createBoard(BoardApi boardApi, MultipartFile file, Long loginId) {

        Member user = validateUser(loginId);

        Board board = Board.newBoard(boardApi, user);

        Board saveBoard = boardRepository.save(board);

        return saveBoard.getBoardId();
    }

    // 게시판 수정
    public String updateBoard(Long boardId, BoardApi boardApi, Long loginId) {

        validateUser(loginId);

        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);

        return board.updateBoard(boardApi.getTitle());
    }

    // 게시판 삭제
    public void deleteBoard(Long boardId, Long loginId) {

        validateUser(loginId);

        validateBoard(boardId);

        List<Reply> replies = replyRepository.findAllRepliesByMemberId(loginId);
        replyRepository.deleteAll(replies);

        List<File> files = fileRepository.findAllFilesByMemberId(loginId);
        fileRepository.deleteAll(files);

        Board board = boardRepository.finByBoardByMemberId(loginId);
        boardRepository.deleteById(board.getBoardId());
    }

    // 게시판 조회
    public BoardApi getBoard(Long loginId, Long boardId) {

        validateUser(loginId);
        validateBoard(boardId);

        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);

        return BoardApi.builder()
                .title(board.getTitle())
                .build();
    }

    public void deleteBoardById(Long boardId) {

        validateBoard(boardId);

        boardRepository.deleteById(boardId);
    }


    private void validateBoard(Long boardId) {
        if(boardId == null) {
            throw new BoardNotFoundException();
        }
    }

    public Member validateUser(Long loginId) {
        if (loginId < 1) {
            throw new MemberAccessDeniedException();
        }
        return userRepository.findById(loginId).orElseThrow(MemberNotFoundException::new);
    }

}
