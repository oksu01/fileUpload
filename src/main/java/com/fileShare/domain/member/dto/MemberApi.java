package com.fileShare.domain.member.dto;


import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberApi {

    @NotNull(message = "{validation.auth.email}")
    private String email;

    @NotNull(message = "{validation.auth.nickname}")
    private String nickname;

}

