package com.konkuk.kubit.domain.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@Data
public class ResultResponse{
    private int result_code;
    private String result_msg;
    private Object detail;
}
