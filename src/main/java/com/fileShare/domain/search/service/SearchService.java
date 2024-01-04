package com.fileShare.domain.search.service;

import com.fileShare.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {

    Page<Board> search(Pageable pageable, String keyword);

    Page<Board> searches(Pageable pageable, String keyword);
}



