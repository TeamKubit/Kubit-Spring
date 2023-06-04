package com.konkuk.kubit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelTransactionList {
    @NotEmpty
    private Long[] transactionIdList;
}
