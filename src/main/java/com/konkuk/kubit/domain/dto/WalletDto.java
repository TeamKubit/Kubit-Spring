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
    private Long walletId;
    private Long uId;
    private String marketCode;
    private float quantity;
    private float totalPrice;
    public WalletDto(Wallet wallet){
        setWalletId(wallet.getWalletId());
        setUId(wallet.getUId().getUId());
        setMarketCode(wallet.getMarketCode().getMarketCode());
        setQuantity(wallet.getQuantity());
        setTotalPrice(wallet.getTotalPrice());
    }
}
