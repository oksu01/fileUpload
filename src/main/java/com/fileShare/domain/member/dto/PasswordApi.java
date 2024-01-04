package com.fileShare.domain.member.dto;


import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordApi {

    @NotNull(message = "{validation.member.password}")
    private String password;
    @NotNull(message = "{validation.member.password}")
    private String newPassword;

}