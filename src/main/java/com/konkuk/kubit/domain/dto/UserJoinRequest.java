package com.konkuk.kubit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@Getter
public class UserJoinRequest {
    @NotEmpty
    private String userId;
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
