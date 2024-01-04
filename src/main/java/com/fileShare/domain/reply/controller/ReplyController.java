package com.fileShare.domain.reply.controller;


import com.fileShare.domain.board.dto.BoardApi;
import com.fileShare.domain.reply.dto.RecommendStatus;
import com.fileShare.domain.reply.dto.ReplyApi;
import com.fileShare.domain.reply.dto.ReplyInfo;
import com.fileShare.domain.reply.dto.SubReplyApi;
import com.fileShare.domain.reply.entity.Reply;
import com.fileShare.domain.reply.service.ReplyService;
import com.fileShare.global.annotation.LoginId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/replies")
@Validated
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @GetMapping("/{reply-id}") // 댓글 단건 조회
    public ResponseEntity<ReplyApi> getReply(@PathVariable("reply-id") @Positive Long replyId,
                                             @LoginId Long loginId,
                                             @RequestBody ReplyApi replyApi) {

        ReplyApi reply = replyService.getReply(replyId, loginId, replyApi);

        return new ResponseEntity<>(reply, HttpStatus.OK);
    }

    @PostMapping("/{board-id}") // 댓글 등록
    public ResponseEntity<Long> createReply(@RequestBody @Valid ReplyApi replyApi,
                                            @LoginId Long loginId,
                                            @PathVariable("board-id") @Positive Long boardId) {

        Long replyId = replyService.createReply(replyApi, loginId, boardId);

        URI uri = URI.create("/replies/" + replyId);

        return ResponseEntity.created(uri).body(replyId);
    }

    @PostMapping("/{reply-id}/subReplies") // 대댓글 등록
    public ResponseEntity<Long> createSubReply(@PathVariable("reply-id") Long replyId,
                                               @RequestBody @Valid SubReplyApi replyApi,
                                               @LoginId Long loginId) {

        Long subReply = replyService.createSubReply(replyId, replyApi, loginId);

        URI uri = URI.create("/replies/" + replyId + "/subReplies/" + subReply);

        return ResponseEntity.created(uri).body(subReply);
    }

    @PatchMapping("/{reply-id}") // 댓글 수정
    public ResponseEntity<String> updateReply(@PathVariable("reply-id") Long replyId,
                                              @RequestBody @Valid ReplyApi replyApi,
                                              @LoginId Long loginId) {

        String modifyReply = replyService.updateReply(replyId, replyApi, loginId);

        return new ResponseEntity<>(modifyReply, HttpStatus.OK);
    }

    @DeleteMapping("/{reply-id}") // 댓글 삭제
    public ResponseEntity<Void> deleteReply(@PathVariable("reply-id") @Positive Long replyId,
                                            @LoginId Long loginId) {

        replyService.deleteReply(replyId, loginId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{reply-id}/recommend")
    public ResponseEntity<RecommendStatus> recommendReply(@PathVariable("reply_id") @RequestParam @Positive Long replyId,
                                                          @LoginId Long loginId) {

        RecommendStatus recommend = replyService.recommendReply(replyId, loginId);

        return new ResponseEntity<>(recommend, HttpStatus.OK);
    }

    @PostMapping("/{reply-id}/nonRecommend")
    public ResponseEntity<RecommendStatus> nonRecommendReply(@PathVariable("reply_id") @RequestParam @Positive Long replyId,
                                                             @LoginId Long loginId) {

        RecommendStatus nonRecommend = replyService.nonRecommendReply(replyId, loginId);

        return new ResponseEntity<>(nonRecommend, HttpStatus.OK);
    }


}
