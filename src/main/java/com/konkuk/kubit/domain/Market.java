package com.konkuk.kubit.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "market")
public class Market {
    @Id
    @Column(nullable = false, name = "marketCode", length = 50)
    private String marketCode;

    @Column(nullable = false)
    private String marketName;

    @Column(nullable = false)
    private String marketDescription;

    @Column(nullable = false)
    private String currency;

    @OneToMany(mappedBy = "marketCode", cascade = CascadeType.ALL)
    private List<Wallet> wallets = new ArrayList<>();

    @OneToMany(mappedBy = "marketCode", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();
}
