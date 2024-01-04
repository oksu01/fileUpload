package com.fileShare.domain.reply.repository;

import com.fileShare.domain.reply.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

//    @Query("SELECT r FROM Reply r WHERE r.file.fileNum = :fileNum")
//    List<Reply> findByReplyByFileNum(@Param("fileNum") Long fileNum);

    @Query("SELECT r FROM Reply r JOIN FETCH r.member m WHERE m.id = :memberId")
    List<Reply> findAllRepliesByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT r FROM Reply r WHERE r.board.id = :boardId")
    Page<Reply> findRepliesByBoardId(@Param("boardId") Long boardId, Pageable pageable);


}
