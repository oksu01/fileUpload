package com.fileShare.domain.file.repository;

import com.fileShare.domain.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    @Query("SELECT f FROM File f JOIN FETCH f.member m WHERE m.id = :memberId")
    List<File> findAllFilesByMemberId(@Param("memberId") Long memberId);
}
