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

    private float quantity;

    private float totalPrice;
}

