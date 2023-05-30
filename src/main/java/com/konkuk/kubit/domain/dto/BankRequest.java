package com.konkuk.kubit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class BankRequest {
    @Pattern(regexp = "^(DEPOSIT|WITHDRAW)$", message = "requestType은 DEPOSIT이나 WITHDRAW로만 구성되어야 합니다.")
    private String requestType;
    @Positive
    private int money;

}
