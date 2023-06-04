package com.konkuk.kubit.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionUtil {
    public static final double CHARGE = 0.0005;

    public static int getCharge(double requestPrice, double quantity) {
        return (int) (requestPrice * quantity * CHARGE);
    }

    public static String getTimeString(LocalDateTime localDateTime) {
        if (localDateTime == null){
            return "";
        }
        return localDateTime.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm") // 분까지만
        );
    }
}
