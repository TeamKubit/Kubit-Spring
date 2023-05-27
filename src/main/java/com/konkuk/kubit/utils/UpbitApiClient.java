package com.konkuk.kubit.utils;

import com.konkuk.kubit.domain.dto.CurrentPriceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UpbitApiClient {
    private RestTemplate restTemplate;

    @Autowired
    public UpbitApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public CurrentPriceResponse[] callApi(String marketCode) {
        ResponseEntity<CurrentPriceResponse[]> response = restTemplate.getForEntity("https://api.upbit.com/v1/ticker?markets="+marketCode, CurrentPriceResponse[].class);
        return response.getBody();
    }
}
