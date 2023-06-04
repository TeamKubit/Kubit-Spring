package com.konkuk.kubit.domain.dto;

import com.konkuk.kubit.domain.Transaction;
import com.konkuk.kubit.utils.TransactionUtil;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDto {
    private Long transactionId;
    private String marketCode;
    private String koreanName;
    private String englishName;
    private double quantity;

    private String transactionType;

    private String requestTime;

    private String completeTime;

    private String resultType;

    private double charge;

    private double requestPrice;

    private double completePrice;

    public TransactionDto(Transaction t){
        setTransactionId(t.getTransactionId());
        setMarketCode(t.getMarketCode().getMarketCode());
        setKoreanName(t.getMarketCode().getKoreanName());
        setEnglishName(t.getMarketCode().getEnglishName());
        setQuantity(t.getQuantity());
        setTransactionType(t.getTransactionType());
        setRequestTime(TransactionUtil.getTimeString(t.getRequestTime()));
        setCompleteTime(TransactionUtil.getTimeString(t.getCompleteTime()));
        setResultType(t.getResultType());
        setCharge(t.getCharge());
        setRequestPrice(t.getRequestPrice());
        setCompletePrice(t.getCompletePrice());
    }
}
