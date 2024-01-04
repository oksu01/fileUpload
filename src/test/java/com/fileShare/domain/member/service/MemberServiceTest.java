package com.fileShare.domain.member.service;

import com.fileShare.domain.file.entity.File;
import com.fileShare.domain.member.dto.MemberInfo;
import com.fileShare.domain.member.dto.MemberLoginApi;
import com.fileShare.domain.member.dto.MemberRequestApi;
import com.fileShare.domain.reply.entity.Reply;
import com.fileShare.domain.member.dto.PasswordApi;
import com.fileShare.domain.member.entity.Member;
import com.fileShare.domain.member.repository.MemberRepository;
import com.fileShare.global.exception.EmailExistException;
import com.fileShare.global.exception.MemberNotFoundException;
import com.fileShare.global.exception.MemberAccessDeniedException;
import com.global.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest extends ServiceTest {

    @Autowired
    MemberRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;


    @Test
    @DisplayName("회원 가입을 할 수 있다")
    void signup() {
        //when
        userService.signup(new MemberRequestApi("test@email.com", "당근", "1111"));

        //then
        Member signupMember = userRepository.findByEmail("test@email.com").orElse(null);
        assertThat(signupMember).isNotNull();

    }

    @Test
    @DisplayName("회원 정보를 수정 할 수 있다")
    void modifyMemberInfo() {
        // given
        Member member = createAndSaveMember();
        MemberInfo modifiedUser = MemberInfo.builder()
                .nickname("당근")
                .build();

        // when
        Member updateInfo = userService.updateMember(member.getMemberId(), modifiedUser);

        em.flush();
        em.clear();

        // then
        assertThat(updateInfo.getNickname()).isEqualTo("당근");
    }

    @Test
    @DisplayName("비밀번호를 업데이트 할 수 있다")
    void updatePassword() {
        // given
        String password = "1111";
        String newPassword = "2222";
        String encodingPassword = passwordEncoder.encode(password);
        Member member = createMember(encodingPassword);

        memberRepository.save(member);

        PasswordApi passwordApi =  PasswordApi.builder()
                .password(password)
                .newPassword(newPassword)
                .build();

        // when
        memberService.updatePassword(passwordApi, member.getMemberId());

        // then
        assertThat(passwordEncoder.matches(newPassword, member.getPassword())).isTrue();

    }

    @Test
    @DisplayName("사용자를 삭제할 수 있다")
    void deleteUser() {
        // given
        Member member = createAndSaveMember();
        Long memberId = member.getMemberId();

        // when
        userService.deleteMember(memberId);

        // then
        Member deletedUser = userRepository.findById(memberId).orElse(null);
        assertNull(deletedUser);

        List<Reply> userReplies = replyRepository.findAllRepliesByMemberId(memberId);
        assertTrue(userReplies.isEmpty());

        List<File> userFiles = fileRepository.findAllFilesByMemberId(memberId);
        assertTrue(userFiles.isEmpty());
    }

    @Test
    @DisplayName("로그인시 이메일이 존재하지 않으면 'MemberNotFoundException' 이 발생한다")
    void memberNotFound() {
        // given
        String password = "1111";
        Member member = createMember(password);

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            memberService.login(new MemberLoginApi("test@email.com", "당근", "1111"));
        });
    }

    @Test
    @DisplayName("회원정보를 수정 할때 로그인하지 않으면 예외가 발생한다")
    void notLoginMember() {

        // when & then
        assertThrows(MemberNotFoundException.class, () -> {
            memberService.updateMember(9999999L, new MemberInfo("호박고구마"));
        });
    }

    @Test
    @DisplayName("회원탈퇴 시 로그인하지 않으면 예외가 발생한다")
    void notLoginUser() {

        // when & then
        assertThrows(MemberAccessDeniedException.class, () -> {
            memberService.deleteMember(999999999L);
        });
    }

    @Test
    @DisplayName("회원가입시 이메일이 존재하면 예외가 발생한다")
    void existEmail() {
        // given
        Member member = createAndSaveMember();

        //when & then
        assertThrows(EmailExistException.class, () -> {
            memberService.signup(new MemberRequestApi("test@email.com", "당근", "1111"));
        });
    }

}