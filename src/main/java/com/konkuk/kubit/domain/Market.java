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
    private String koreanName; // 코인명(한글)
    private String englishName; // 코인명(영문)

    @OneToMany(mappedBy = "marketCode", cascade = CascadeType.ALL)
    private List<Wallet> wallets = new ArrayList<>();

    @OneToMany(mappedBy = "marketCode", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();
}
