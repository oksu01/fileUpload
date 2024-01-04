package com.fileShare.domain.board.controller;


import com.fileShare.domain.board.dto.BoardApi;
import com.fileShare.domain.board.service.BoardService;
import com.fileShare.domain.reply.dto.ReplyInfo;
import com.fileShare.domain.reply.service.ReplyService;
import com.fileShare.global.annotation.LoginId;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

@RestController
@RequestMapping("/boards")
@Validated
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final ReplyService replyService;


    // 게시판 등록
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createBoard(@RequestPart("boardApi") @Valid BoardApi boardApi,
                                            @RequestPart("file") MultipartFile file,
                                            @LoginId Long loginId) {

        Long boardId = boardService.createBoard(boardApi, file, loginId);

        URI uri = URI.create("/boards/" + boardId);

        return ResponseEntity.created(uri).body(boardId);
    }

    @PatchMapping("/{board-id}") // 게시판 수정
    public ResponseEntity<String> updateBoard(@PathVariable("board-id") @Positive Long boardId,
                                              @RequestBody BoardApi boardApi,
                                              @LoginId Long loginId) {

        String modifyBoard = boardService.updateBoard(boardId, boardApi, loginId);

        return new ResponseEntity<>(modifyBoard, HttpStatus.OK);
    }

    @DeleteMapping("/{board-id}") // 게시판 삭제
    public ResponseEntity<Void> deleteBoard(@Positive @PathVariable("board-id") Long boardId,
                                            @LoginId Long loginId) {

        boardService.deleteBoard(boardId, loginId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{board-id}") // 게시판 조회
    public ResponseEntity<BoardApi> getBoard(@LoginId Long loginId,
                                             @PathVariable("board-id") Long boardId) {

        BoardApi boardApi = boardService.getBoard(loginId, boardId);

        return new ResponseEntity<>(boardApi, HttpStatus.OK);
    }

    //댓글 목록 조회
    @GetMapping({"/{board-id}/replies"})
    public ResponseEntity<Page<ReplyInfo>> getReplies(@PathVariable("board-id") Long boardId,
                                                      @RequestParam(defaultValue = "1") @Positive int page,
                                                      @RequestParam(defaultValue = "10") @Positive int size) {

        Page<ReplyInfo> replies = replyService.getReplies(boardId, page-1, size);

        return new ResponseEntity<>(replies, HttpStatus.OK);
    }
}
