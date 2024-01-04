package com.fileShare.domain.search.service;


import com.fileShare.domain.board.entity.Board;
import com.fileShare.domain.board.entity.QBoard;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final EntityManager entityManager;

    public SearchServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<Board> search(Pageable pageable, String keyword) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QBoard qBoard = QBoard.board;

        BooleanExpression predicate = qBoard.title.contains(keyword);

        List<Board> result = queryFactory.selectFrom(qBoard)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(result, pageable, result.size());
    }

    @Override
    public Page<Board> searches(Pageable pageable, String keyword) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QBoard qBoard = QBoard.board;

        BooleanExpression predicate = qBoard.title.like("%" + keyword + "%"); // 일치하는 부분을 포함하는 검색

        QueryResults<Board> queryResults = queryFactory
                .selectFrom(qBoard)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }

}
