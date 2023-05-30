package com.konkuk.kubit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class MarketTransactionRequest {
    @Pattern(regexp = "^(BID|ASK)$", message = "transactionType은 BID나 ASK로만 구성되어야 합니다.")
    private String transactionType;
    @NotEmpty
    private String marketCode;
    @Positive
    private int currentPrice; // 현재 해당 종목의 가격
    @Positive
    private int totalPrice; // 시장가 매매는 총 금액만 명시함
}
