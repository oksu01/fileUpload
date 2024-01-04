package com.fileShare.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fileShare.auth.jwt.JwtTokenizer;
import com.fileShare.domain.board.entity.Board;
import com.fileShare.domain.board.repository.BoardRepository;
import com.fileShare.domain.file.entity.File;
import com.fileShare.domain.file.repository.FileRepository;
import com.fileShare.domain.member.entity.Member;
import com.fileShare.domain.member.repository.MemberRepository;
import com.fileShare.domain.reply.dto.ReplyApi;
import com.fileShare.domain.reply.entity.Reply;
import com.fileShare.domain.reply.repository.ReplyRepository;
import com.fileShare.domain.search.Repository.SearchRepository;
import com.fileShare.global.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IntegrationTest {
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected ReplyRepository replyRepository;
    @Autowired
    protected BoardRepository boardRepository;
    @Autowired
    protected FileRepository fileRepository;
    @Autowired
    protected SearchRepository searchRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected JwtTokenizer jwtTokenizer;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected EntityManager em;

    protected void flushAll() {
        memberRepository.flush();
        replyRepository.flush();
        boardRepository.flush();
        fileRepository.flush();
        searchRepository.flush();
    }
    protected void deleteAll() {
        memberRepository.deleteAll();
        replyRepository.deleteAll();
        boardRepository.deleteAll();
        fileRepository.deleteAll();
        searchRepository.deleteAll();

        em.flush();
        em.clear();
    }

    protected Member createAndSaveMember() {
        Member user = Member.builder()
                .memberId(1L)
                .email("test@email.com")
                .password("!1q2w3e4r")
                .nickname("hong")
                .authority(Authority.ROLE_USER)
                .files(List.of())
                .replies(List.of())
                .build();

        memberRepository.save(user);

        return user;
    }

    protected Member createUserWithBoard() {
        Member member = Member.builder()
                .email("test@email.com")
                .password("!!1q2w3e4r")
                .nickname("hong")
                .authority(Authority.ROLE_USER)
                .files(List.of())
                .replies(List.of())
                .build();

        memberRepository.save(member);

        createAndSaveBoard(member);

        return member;
    }

    protected File createAndSaveFile(Board board) {
        File file = File.builder()
                .fileId(1L)
                .board(board)
                .build();
        fileRepository.save(file);
        return file;
    }

    protected Board createAndSaveBoard(Member member) {
        Board board = Board.builder()
                .boardId(1L)
                .member(member)
                .title("title")
                .build();
        boardRepository.save(board);
        return board;
    }

    protected Reply createAndSaveReply(Member member, Board board) {
        Reply reply = Reply.builder()
                .member(member)
                .board(board)
                .content("content")
                .build();

        replyRepository.save(reply);

        return reply;
    }

    protected Reply createMainReplyAndSave(Member member, Board board) {
        Reply mainReply = Reply.builder()
                .content("content")
                .mainReply(Reply.newReply(new ReplyApi("content", member.getMemberId()), board.getBoardId()))
                .member(member)
                .board(board)
                .build();
        replyRepository.save(mainReply);

        return mainReply;
    }

    protected Reply createSubReplyAndSave(Member member, Board board, Reply reply) {
        Reply subReply = Reply.builder()
                .replyId(reply.getReplyId())
                .content("content")
                .mainReply(Reply.newReply(new ReplyApi("content", member.getMemberId()), board.getBoardId()))
                .subReplies(List.of())
                .member(member)
                .board(board)
                .build();

        replyRepository.save(subReply);

        return subReply;
    }

    protected Member createMember(String password) {
        return Member.builder()
                .email("test@email.com")
                .nickname("당근")
                .password(password)
                .authority(Authority.ROLE_USER)
                .build();
    }

    protected Reply createAndRecommend(Member member, Board board) {
        Reply reply = Reply.builder()
                .content("content")
                .member(member)
                .recommendCount(0)
                .upvoterMembers(new HashSet<>())
                .downvoterMembers(new HashSet<>())
                .board(board)
                .build();

        replyRepository.save(reply);

        return reply;
    }
}
