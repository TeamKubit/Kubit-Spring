package com.konkuk.kubit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class AskTransactionRequest {
    @NotEmpty
    private String marketCode;
    @Positive
    private int currentPrice; // 현재 해당 종목의 가격
    @Positive
    private double quantity; // 판매할 총 수량
}
