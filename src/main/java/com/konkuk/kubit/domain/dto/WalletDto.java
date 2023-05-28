package com.konkuk.kubit.domain.dto;

import com.konkuk.kubit.domain.Market;
import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.domain.Wallet;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
public class WalletDto {
    private String marketCode;
    private double quantityAvailable;
    private double quantity;
    private double totalPrice;

    public WalletDto(Wallet wallet){
        setMarketCode(wallet.getMarketCode().getMarketCode());
        setQuantityAvailable(wallet.getQuantityAvailable());
        setQuantity(wallet.getQuantity());
        setTotalPrice(wallet.getTotalPrice());
    }
}
