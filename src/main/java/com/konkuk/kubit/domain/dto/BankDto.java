package com.konkuk.kubit.domain.dto;

import com.konkuk.kubit.domain.Bank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BankDto {
    private String bankType; // 입금/출금
    private int money; // 입금/출금액
    private LocalDateTime tradeAt;
    public BankDto(Bank b){
        setBankType(b.getBankType());
        setMoney(b.getMoney());
        setTradeAt(b.getTradeAt());
    }
}
