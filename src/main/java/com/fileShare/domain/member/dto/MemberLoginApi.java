package com.fileShare.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginApi {

    @NotBlank(message = "${validation.member.email}")
    private String email;
    @NotBlank(message = "${validation.member.nickname}")
    private String nickname;
    @NotBlank(message = "${validation.member.password}")
    private String password;

}