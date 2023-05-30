package com.konkuk.kubit.domain.dto;

import com.konkuk.kubit.domain.Wallet;
import lombok.Data;

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
