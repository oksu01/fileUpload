package com.fileShare.domain.reply.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fileShare.domain.board.entity.Board;
import com.fileShare.domain.file.entity.File;
import com.fileShare.domain.reply.dto.ReplyApi;
import com.fileShare.domain.reply.dto.SubReplyApi;
import com.fileShare.domain.search.entity.Search;
import com.fileShare.domain.member.entity.Member;
import com.fileShare.global.BaseEntity;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.annotation.PostConstruct;
import javax.persistence.*;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long replyId;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "mainReply_id")
    private Reply mainReply;

    @OneToMany(mappedBy = "mainReply", orphanRemoval = true)
    private List<Reply> subReplies = new ArrayList<>();

    @ManyToOne(fetch = LAZY, cascade = ALL)
    private Member member;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "file_id")
    private File file;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "search_id")
    private Search search;

    private int recommendCount;

    @ElementCollection
    private Set<Long> upvoterMembers = new HashSet<>();

    @ElementCollection
    private Set<Long> downvoterMembers = new HashSet<>();



    public static Reply newReply(ReplyApi replyApi, Long boardId) {

        return Reply.builder()
                .content(replyApi.getContent())
                .board(replyApi.getBoard())
                .build();
    }

    public static Reply newSubReply(Reply mainReply, SubReplyApi subReplyApi, Long loginId) {
        return Reply.builder()
                .content(subReplyApi.getContent())
                .mainReply(mainReply)
                .build();

    }

    public String updateReply(String content) {
        return this.content == null ? this.content : content;
    }

    public List<Reply> getSubReplies() {
        return this.subReplies;
    }

    public void increaseRecommend() {
        recommendCount++;
    }

    public void decreaseRecommend() {
        recommendCount--;
    }

    public boolean hasUpvoted(Long memberId) {
        return upvoterMembers.contains(memberId);
    }

    public boolean hasDownvoted(Long memberId) {
        return downvoterMembers.contains(memberId);
    }

}
