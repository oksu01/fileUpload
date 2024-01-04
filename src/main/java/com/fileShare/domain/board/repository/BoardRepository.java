package com.fileShare.domain.board.repository;

import com.fileShare.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b JOIN FETCH b.member m WHERE m.id = :memberId")
    Board finByBoardByMemberId(@Param("memberId") Long memberId);
}

