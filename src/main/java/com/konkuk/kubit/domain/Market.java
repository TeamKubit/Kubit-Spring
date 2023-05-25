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

    private String marketName;
    private String marketDescription;
    private String currency;

    @OneToMany(mappedBy = "marketCode")
    private List<Wallet> wallets = new ArrayList<>();

}
