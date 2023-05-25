package com.konkuk.kubit.domain;

import lombok.*;
import org.yaml.snakeyaml.error.Mark;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="snapshot")
public class Snapshot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "snapshotId")
    private Long snapshotId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="marketCode")
    private Market marketCode;

    private double marketPrice;

    @OneToMany(mappedBy = "transactionId", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    private LocalDateTime time;

    @PrePersist
    protected void onCreate() {
        // DB 스키마를 바꿔주는 것은 아니고, spring data jpa를 통해서 create될 때, default 값 생성될 것임
        time = LocalDateTime.now();
    }

}
