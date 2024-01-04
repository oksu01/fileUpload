//package com.fileShare.integration;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fileShare.domain.board.entity.Board;
//import com.fileShare.domain.member.entity.Member;
//import com.fileShare.domain.reply.dto.ReplyApi;
//import com.fileShare.domain.reply.entity.Reply;
//import com.fileShare.global.Authority;
//import com.global.ServiceTest;
//import org.apache.tomcat.util.http.parser.Authorization;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.event.EventListener;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.DynamicTest.dynamicTest;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//public class ReplyIntegrationTest extends IntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//
//
//    @TestFactory
//    @DisplayName("댓글을 조회한다")
//    Collection<DynamicTest> getReply() throws Exception {
//
//        Member member = createAndSaveMember();
//        Board board = createAndSaveBoard(member);
//        Reply reply = createAndSaveReply(member, board);
//
//        String token = jwtTokenizer.generateTokenForUser(member);
//
//        ResultActions actions = mockMvc.perform(get("/replies/{reply-id}", reply.getReplyId())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(new ReplyApi("content", member.getMemberId(), new ArrayList<>())))
//                .accept(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer " + token) // 헤더에 토큰 추가
//        );
//
//        ReplyApi replyApi = ReplyApi.builder()
//                .memberId(member.getMemberId())
//                .subReplies(new ArrayList<>())
//                .content("content")
//                .build();
//
//        ResultActions resultActions = mockMvc.perform(get("/replies/{reply-id}", reply.getReplyId())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(new ReplyApi("content", null, new ArrayList<>())))
//                .accept(MediaType.APPLICATION_JSON)
//        );
//
//        ReplyApi replyApi1 = ReplyApi.builder()
//                .content("Unauthorized")
//                .build();
//
//        return Arrays.asList(
//                dynamicTest("로그인한 사용자가 댓글을 조회한다", () -> {
//                    actions
//                            .andDo(print())
//                            .andExpect(status().isOk())
//                            .andExpect(jsonPath("$.memberId").value(replyApi.getMemberId()))
//                            .andExpect(jsonPath("$.content").value(replyApi.getContent()));
//                }),
//
//                dynamicTest("비로그인 사용자는 댓글을 조회할 수 없다", () -> {
//                    resultActions
//                            .andDo(print())
//                            .andExpect(status().isUnauthorized())
//                            .andExpect(jsonPath("$.content").value(replyApi1.getContent()));
//                }),
//
//                dynamicTest("댓글이 존재하지 않으면 예외가 발생한다", () -> {
//                    resultActions
//                            .andDo(print())
//                            .andExpect(status().isNotFound())
//                            .andExpect(jsonPath("$.content").value(replyApi.getContent()));
//                })
//        );
//    }
//}