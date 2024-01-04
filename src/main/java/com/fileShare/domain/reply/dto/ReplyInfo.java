package com.fileShare.domain.reply.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyInfo {

    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

}
