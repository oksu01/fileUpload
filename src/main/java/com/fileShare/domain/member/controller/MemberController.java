package com.fileShare.domain.member.controller;


import com.fileShare.domain.file.repository.FileRepository;
import com.fileShare.domain.member.dto.MemberInfo;
import com.fileShare.domain.member.entity.Member;
import com.fileShare.domain.reply.repository.ReplyRepository;
import com.fileShare.domain.member.dto.PasswordApi;
import com.fileShare.global.annotation.LoginId;
import com.fileShare.domain.member.repository.MemberRepository;
import com.fileShare.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Validated
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ReplyRepository replyRepository;
    private final FileRepository fileRepository;



    // 회원 정보 수정
    @PatchMapping("/{member-id}")
    public ResponseEntity<MemberInfo> updateMember(@PathVariable("member-id") @Positive @LoginId long loginId,
                                                   @RequestBody MemberInfo memberInfo) {

        Member myInfo = memberService.updateMember(loginId, memberInfo);

        return new ResponseEntity(myInfo, HttpStatus.OK);
    }

    // 비밀 번호 수정
    @PatchMapping("/{member-id}/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid PasswordApi userPassword,
                                               @PathVariable("member-id") @LoginId Long loginId) {

        memberService.updatePassword(userPassword, loginId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 회원 탈퇴
    @DeleteMapping("/{member-id}")
    public ResponseEntity<Void> deleteMember(@PathVariable("member-id") @Positive @LoginId long memberId) {

        memberService.deleteMember(memberId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


//    @GetMapping
//    public ResponseEntity getMembers(@Positive @RequestParam int page,
//                                     @Positive @RequestParam int size) {
//        Page<Member> pageMembers = memberService.findMembers(page - 1, size);
//        List<Member> members = pageMembers.getContent();
//        return new ResponseEntity<>(
//                new MultiResponseDto<>(mapper.membersToMemberResponses(members),
//                        (PageInfo) pageMembers),
//                HttpStatus.OK);
//    }


}
