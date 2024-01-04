package com.fileShare.domain.file.entity;

import com.fileShare.domain.board.entity.Board;
import com.fileShare.domain.reply.entity.Reply;
import com.fileShare.domain.search.entity.Search;
import com.fileShare.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long fileId;

    @OneToMany(mappedBy = "file")
    private List<Reply> replies = new ArrayList<>();

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "search_id")
    private Search search;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "member_id")
    private Member member;
}
