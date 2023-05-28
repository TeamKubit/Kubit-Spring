package com.konkuk.kubit.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="wallet_id")
    private Long walletId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="uId")
    private User uId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="marketCode")
    private Market marketCode;

    //총 수량
    private double quantity;

    // 주문 가능한 수량 (매도 주문에서, quantity_available을 보고, 거래 완료시점에서 update 하도록 함)
    private double quantityAvailable;

    private double totalPrice;
}

