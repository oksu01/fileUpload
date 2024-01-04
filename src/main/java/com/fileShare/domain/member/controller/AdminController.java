package com.fileShare.domain.member.controller;


import com.fileShare.domain.board.service.BoardService;
import com.fileShare.domain.file.service.FileService;
import com.fileShare.domain.member.dto.MemberApi;
import com.fileShare.domain.member.service.MemberService;
import com.fileShare.domain.reply.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final ReplyService replyService;
    private final FileService fileService;
    private final BoardService boardService;

    @GetMapping("/members") // 전체 회원 조회
    public ResponseEntity<List<MemberApi>> getAllMembers() {

        List<MemberApi> members = memberService.getAllMembers()
                .stream()
                .map(this::convertToApi)
                .collect(Collectors.toList());

        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @DeleteMapping("/members/{member-id}") // 특정 회원 삭제
    public ResponseEntity<Void> deleteMember(Long memberId) {

        memberService.deleteMemberById(memberId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/replies/{reply-id}") // 댓글 삭제
    public ResponseEntity<Void> deleteReply(Long replyId) {

        replyService.deleteReplyById(replyId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/files/{file-id}") // 파일 삭제
    public ResponseEntity<Void> deleteFile(Long fileId) {

        fileService.deleteFileById(fileId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/boards/{board-id}") // 게시판 삭제
    public ResponseEntity<Void> deleteBoard(Long boardId) {

        boardService.deleteBoardById(boardId);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    private MemberApi convertToApi(MemberApi member) {
        return MemberApi.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }

}

