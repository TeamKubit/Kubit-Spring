package com.konkuk.kubit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor

public class FixedTransactionRequest {
    @Pattern(regexp = "^(BID|ASK)$", message = "transaction_type은 BID나 ASK로만 구성되어야 합니다.")
    private String transactionType;
    @NotEmpty
    private String marketCode;
    @Positive
    private double requestPrice;
    @Positive
    private double quantity;
}
