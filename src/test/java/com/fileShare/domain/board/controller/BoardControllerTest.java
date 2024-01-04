package com.fileShare.domain.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fileShare.domain.board.dto.BoardApi;
import com.fileShare.domain.board.service.BoardService;
import com.fileShare.domain.reply.dto.ReplyInfo;
import com.fileShare.domain.reply.service.ReplyService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@Disabled
@AutoConfigureMockMvc
@Transactional
class BoardControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BoardService boardService;
    @Mock
    ReplyService replyService;
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("게시판 조회 ")
    void getBoard() throws Exception {
        // given
        Long boardId = 1L;
        Long loginId = 1L;

        BoardApi boardApi = BoardApi.builder()
                .title("title")
                .build();

        when(boardService.getBoard(loginId, boardId)).thenReturn(boardApi);

        // when
        ResultActions actions = mockMvc.perform(get("/boards/{board-id}", boardId)
                        .param("loginId", String.valueOf(boardId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("title"));
    }

    @Test
    @DisplayName("게시판 삭제")
    void deleteBoard() throws Exception {
        // given
        Long boardId = 1L;
        Long loginId = 123L;

        doNothing().when(boardService).deleteBoard(boardId, loginId);

        // when
        ResultActions actions = mockMvc.perform(delete("/boards/{board-id}", boardId)
                        .param("boardId", String.valueOf(boardId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // then
        actions
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("댓글 목록 조회")
    void getReplies() throws Exception {

        // given
        long boardId = 1L;
        int page = 1;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);

        List<ReplyInfo> replies = Collections.singletonList(new ReplyInfo("content",LocalDateTime.now(), LocalDateTime.now()));
        Page<ReplyInfo> replyInfos = new PageImpl<>(replies, pageable, replies.size());


        when(replyService.getReplies(anyLong(), anyInt(), anyInt())).thenReturn(replyInfos);

        // when
        ResultActions actions = mockMvc.perform(get("/boards/{board-id}/replies", boardId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", String.valueOf(Sort.Direction.DESC))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        actions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(replies.size()));
    }

    @Test
    @DisplayName("게시판 수정")
    void updateBoard() throws Exception {
        // given
        Long boardId = 1L;
        String title = "수정 전";

        BoardApi boardApi = BoardApi.builder()
                .title(title)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(boardApi);

        when(boardService.updateBoard(boardId, boardApi, boardId)).thenReturn("수정 후");

        // when
        ResultActions actions = mockMvc.perform(patch("/boards/{board-id}", boardId)
                        .content(jsonRequest)
                        .param("loginId", String.valueOf(boardId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("수정 후"));
    }
}