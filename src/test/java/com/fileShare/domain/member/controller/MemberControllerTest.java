package com.fileShare.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fileShare.domain.member.dto.MemberInfo;
import com.fileShare.domain.member.dto.MemberRequestApi;
import com.fileShare.domain.member.dto.PasswordApi;
import com.fileShare.domain.member.service.MemberService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import javax.xml.transform.Result;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Disabled
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    MemberService memberService;
    @Autowired
    MockMvc mockMvc;


    @Test
    @DisplayName("회원가입")
    void signup() throws Exception {
        // given
        MemberRequestApi request = MemberRequestApi.builder()
                .email("test@email.com")
                .nickname("당근")
                .password("1111")
                .build();

        Long memberId = 1L;

        given(memberService.signup(any(MemberRequestApi.class))).willReturn(memberId);

        // when
        ResultActions actions = mockMvc.perform(get("/auth/oauth")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/members/" + memberId));
    }

    @Test
    @DisplayName("회원 정보 수정")
    public void updateMemberInfo() throws Exception {
        // given
        Long memberId = 1L;
        MemberInfo memberInfo = MemberInfo.builder()
                        .nickname("당근")
                                .build();

        // when and then
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.patch("/members/{member-id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberInfo)));

        actions
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("비밀번호 수정")
    public void updatePassword() throws Exception {
        // given
        Long memberId = 1L;
        PasswordApi passwordApi = PasswordApi.builder()
                        .password("1111")
                        .newPassword("2222")
                        .build();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.patch("/auth/{member-id}/password", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(passwordApi)))
                        .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원 탈퇴")
    void deleteMember() throws Exception {
        // given
        Long memberId = 1L;

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/members/{member-id}", memberId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("비밀번호가 null이면 검증에 실패한다")
    void passwordNotNull() throws Exception {
        // given
        PasswordApi passwordApi = PasswordApi.builder()
                .password(null)
                .newPassword(null)
                .build();

        // when
        ResultActions actions = mockMvc.perform(patch("/members/{member-id}/password", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordApi)));

        // then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}

