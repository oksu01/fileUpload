package com.fileShare.domain.reply.dto;

import com.fileShare.domain.member.entity.Member;
import com.fileShare.domain.reply.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubReplyApi {
    @NotNull(message = "${validation.reply.content}")
    private Long replyId;
    private String content;
    private Long memberId;

    }

