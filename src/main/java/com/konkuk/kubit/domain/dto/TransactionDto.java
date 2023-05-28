package com.konkuk.kubit.domain.dto;

import com.konkuk.kubit.domain.Transaction;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDto {
    private Long transactionId;
    private String marketCode;
    private double quantity;

    private String transactionType;

    private LocalDateTime completeTime;

    private String resultType;

    private double charge;

    private double requestPrice;

    private double completePrice;

    public TransactionDto(Transaction t){
        setTransactionId(t.getTransactionId());
        setMarketCode(t.getMarketCode().getMarketCode());
        setQuantity(t.getQuantity());
        setTransactionType(t.getTransactionType());
        setCompleteTime(t.getCompleteTime());
        setResultType(t.getResultType());
        setCharge(t.getCharge());
        setRequestPrice(t.getRequestPrice());
        setCompletePrice(t.getCompletePrice());
    }
}
