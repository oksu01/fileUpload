package com.fileShare.domain.board.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fileShare.domain.board.dto.BoardApi;
import com.fileShare.domain.file.entity.File;
import com.fileShare.domain.reply.entity.Reply;
import com.fileShare.domain.search.entity.Search;
import com.fileShare.domain.member.entity.Member;
import com.fileShare.global.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long boardId;

    private String title;

    @OneToOne(fetch = LAZY)
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    private List<File> files = new ArrayList<>();

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "search_id")
    private Search search;



    public static Board newBoard(BoardApi boardApi, Member member) {

        return Board.builder()
                .title(boardApi.getTitle())
                .member(member)
                .build();
    }

    public String updateBoard(String title) {
        return this.title == null ? this.title : title;
    }

}
