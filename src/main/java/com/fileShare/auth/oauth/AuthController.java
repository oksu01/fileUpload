package com.fileShare.auth.oauth;


import com.fileShare.domain.member.dto.MemberLoginApi;
import com.fileShare.domain.member.dto.MemberRequestApi;
import com.fileShare.domain.member.dto.PasswordApi;
import com.fileShare.domain.member.service.MemberService;
import com.fileShare.global.annotation.LoginId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @GetMapping("/oauth")
    public ResponseEntity<Void> signup(@RequestBody @Valid MemberRequestApi request) {

        Long memberId = memberService.signup(request);

        URI uri = URI.create("/members/" + memberId);

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid MemberLoginApi request) {

        String loginMember = memberService.login(request);

        return new ResponseEntity<>(loginMember, HttpStatus.OK);
    }

    @PatchMapping("/{member-id}/password")
    public ResponseEntity<Void> updatePassword(@RequestBody PasswordApi request,
                                               @PathVariable("member-id") @LoginId Long loginId) {

        memberService.updatePassword(request, loginId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
