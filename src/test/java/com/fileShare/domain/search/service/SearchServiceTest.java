package com.fileShare.domain.search.service;

import com.fileShare.domain.board.entity.Board;
import com.fileShare.domain.board.repository.BoardRepository;
import com.fileShare.domain.board.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class SearchServiceTest {

    @Autowired
    private SearchService searchService;
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("게시물을 제목으로 검색 할 수 있다")
    public void testSearch() {
        // given
        Board board1 = Board.builder()
                .title("otherTitle")
                .build();
        Board board2 = Board.builder()
                        .title("searchKeyword")
                        .build();
        em.persist(board1);
        em.persist(board2);

        em.flush();
        em.clear();

        // when
        Page<Board> result = searchService.search(PageRequest.of(0, 10), "searchKeyword");

        // then
        assertThat(result.getContent().size()).isEqualTo(1); // 검색 결과가 1개여야 함
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("searchKeyword"); // 검색된 결과의 제목 확인
    }

    @Test
    @DisplayName("일부 검색어로 게시물을 검색 할 수 있다")
    public void testSearches() {
        // given
        Board board1 = Board.builder()
                .title("otherTitle")
                .build();
        Board board2 = Board.builder()
                .title("searchKeyword")
                .build();

        boardRepository.save(board1);
        boardRepository.save(board2);

        // when
        Page<Board> result = searchService.searches(PageRequest.of(0, 10), "search"); // 일부만 검색해도 검색되는지 테스트

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("searchKeyword");
    }
}