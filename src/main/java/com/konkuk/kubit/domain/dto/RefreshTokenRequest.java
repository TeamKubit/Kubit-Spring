package com.konkuk.kubit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class RefreshTokenRequest {
    private String refreshToken;
}
