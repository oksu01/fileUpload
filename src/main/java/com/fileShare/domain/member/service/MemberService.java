package com.fileShare.domain.member.service;


import com.fileShare.auth.jwt.JwtTokenizer;
import com.fileShare.domain.file.entity.File;
import com.fileShare.domain.file.repository.FileRepository;
import com.fileShare.domain.member.dto.*;
import com.fileShare.domain.reply.entity.Reply;
import com.fileShare.domain.reply.repository.ReplyRepository;
import com.fileShare.domain.member.entity.Member;
import com.fileShare.domain.member.repository.MemberRepository;
import com.fileShare.global.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReplyRepository replyRepository;
    private final FileRepository fileRepository;
    private final JwtTokenizer jwtTokenizer;


    public Long signup(MemberRequestApi request) {

        Member member = Member.createMember(
                        request.getEmail(),
                        request.getNickname(),
                        passwordEncoder.encode(request.getPassword()));

        validateEmail(request.getEmail());

        return  memberRepository.save(member).getMemberId();
    }

    public String login(MemberLoginApi request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(MemberNotFoundException::new);

        boolean matches = passwordEncoder.matches(request.getPassword(), member.getPassword());

        if (matches) {
            // 토큰 만료 시간 설정
            Date expirationTime = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

            // 권한 설정
            List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"));

            // 클레임에 권한 추가
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", roles); // 권한을 클레임에 추가

            // JWT 토큰 생성
            return jwtTokenizer.generateAccessToken(claims, member.getEmail(), expirationTime, jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey()));
        } else {
            throw new MemberAccessDeniedException();
        }
    }

    // 회원 정보 수정
    public Member updateMember(Long loginId, MemberInfo memberInfo) {

        Member member = findVerifiedMember(loginId);

       return member.updateMember(loginId, memberInfo);
    }

    // 비밀 번호 수정
    public void updatePassword(PasswordApi request, Long loginId) {

        Member member = validateUser(loginId);

        member.updatePassword(passwordEncoder.encode(request.getNewPassword()));

    }

    // 회원 탈퇴
    public void deleteMember(Long loginId) {

        validateUser(loginId);

        List<Reply> replies = replyRepository.findAllRepliesByMemberId(loginId);
        replyRepository.deleteAll(replies);

        List<File> files = fileRepository.findAllFilesByMemberId(loginId);
        fileRepository.deleteAll(files);

        memberRepository.deleteById(loginId);
    }

    public List<MemberApi> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(this::convertToApi)
                .collect(Collectors.toList());
    }

    public void deleteMemberById(Long memberId) {

        validateUser(memberId);

        memberRepository.deleteById(memberId);
    }

    public Member findVerifiedMember(long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(MemberNotFoundException::new);
    }


    public Member validateUser(Long loginId) {
        if (loginId > 1) {
            throw new MemberAccessDeniedException();
        }
        return memberRepository.findById(loginId).orElseThrow(MemberNotFoundException::new);
    }


    private void validateEmail(String email) {
        Optional<Member> mail = memberRepository.findByEmail(email);
        if (mail.isPresent())
            throw new EmailExistException();
    }

    public MemberApi convertToApi(Member member) {
        return MemberApi.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }

}
