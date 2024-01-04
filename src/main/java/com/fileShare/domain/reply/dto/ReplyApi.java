package com.fileShare.domain.reply.dto;


import com.fileShare.domain.board.entity.Board;
import com.fileShare.domain.reply.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReplyApi {

    @NotNull(message = "${validation.reply.content}")
    private String content;
    private Long memberId;
    private Board board;
    private List<ReplyApi> subReplies;


    public ReplyApi(String content, Long memberId) {
    }



}