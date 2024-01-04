package com.fileShare.domain.reply.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fileShare.domain.reply.dto.ReplyApi;
import com.fileShare.domain.reply.service.ReplyService;
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

import javax.transaction.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Disabled
@AutoConfigureMockMvc
@Transactional
class ReplyControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ReplyService replyService;
    @Autowired
    MockMvc mockMvc;


    @Test
    @DisplayName("댓글 개별 조회")
    void getReply() throws Exception {

        // given
        long replyId = 1L;
        long loginId = 123L;

        String reply = "reply";

        ReplyApi replyApi = ReplyApi.builder()
                .content(reply)
                .build();

        when(replyService.getReply(anyLong(), anyLong(), any())).thenReturn(replyApi);

        // when
        ResultActions actions = mockMvc.perform(get("/replies/{reply-id}", replyId)
                        .param("loginId", String.valueOf(loginId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        actions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").value(reply));
    }

    @Test
    @DisplayName("댓글 생성")
    void createReply() throws Exception {
        // given
        long boardId = 1L;
        long loginId = 123L;

        Long replyId = 1L;

        ReplyApi replyApi = ReplyApi.builder()
                .content("댓글 내용")
                .memberId(loginId)
                .build();

        when(replyService.createReply(any(), anyLong(), anyLong())).thenReturn(replyId);

        // when
        ResultActions actions = mockMvc.perform(post("/replies/{board-id}", boardId)
                        .param("loginId", String.valueOf(loginId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyApi)))
                .andExpect(status().isCreated());

        // then
        actions
                .andExpect(header().string("Location", "/replies/" + replyId))
                .andExpect(jsonPath("$").value(replyId));
    }

    @Test
    @DisplayName("대댓글 생성")
    void createSubReply() throws Exception {
        // given
        long mainReplyId = 1L;
        long loginId = 123L;
        Long subReplyId = 2L;

        ReplyApi mockedReplyApi = ReplyApi.builder()
                .content("대댓글 내용")
                .memberId(loginId)
                .build();

        when(replyService.createSubReply(anyLong(), any(), anyLong())).thenReturn(subReplyId);

        // when
        ResultActions actions = mockMvc.perform(post("/replies/{reply-id}/subReply", mainReplyId)
                        .param("loginId", String.valueOf(loginId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockedReplyApi)))
                .andExpect(status().isCreated());

        // then
        actions
                .andExpect(header().string("Location", "/replies/" + mainReplyId + "/subReply/" + subReplyId))
                .andExpect(jsonPath("$").value(subReplyId));
    }

    @Test
    @DisplayName("댓글 수정")
    void updateReply() throws Exception {
        // given
        long replyId = 1L;
        long loginId = 123L;
        String modifiedContent = "수정된 댓글 내용";

        ReplyApi mockedReplyApi = ReplyApi.builder()
                .content(modifiedContent)
                .build();

        when(replyService.updateReply(anyLong(), any(), anyLong())).thenReturn(modifiedContent);

        // when
        ResultActions actions = mockMvc.perform(patch("/replies/{reply-id}", replyId)
                        .param("loginId", String.valueOf(loginId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockedReplyApi)))
                .andExpect(status().isOk());

        // then
        actions
                .andExpect(content().string(modifiedContent));
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteReply() throws Exception {
        // given
        long replyId = 1L;
        long loginId = 123L;

        doNothing().when(replyService).deleteReply(anyLong(), anyLong());

        // when
        ResultActions actions = mockMvc.perform(delete("/replies/{reply-id}", replyId)
                        .param("loginId", String.valueOf(loginId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // then
        actions
                .andExpect(status().isNoContent());
    }

}



