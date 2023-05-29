package com.konkuk.kubit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
public class CurrentPriceResponse {

    private String market;
    private String trade_date;
    private String trade_time;
    private String trade_date_kst;
    private String trade_time_kst;
    private long trade_timestamp;
    private double opening_price;
    private double high_price;
    private double low_price;
    private double trade_price;
    private double prev_closing_price;
    private String change;
    private double change_price;
    private double change_rate;
    private double signed_change_price;
    private double signed_change_rate;
    private double trade_volume;
    private double acc_trade_price;
    private double acc_trade_price_24h;
    private double acc_trade_volume;
    private double acc_trade_volume_24h;
    private double highest_52_week_price;
    private String highest_52_week_date;
    private double lowest_52_week_price;
    private String lowest_52_week_date;
    private long timestamp;

}
