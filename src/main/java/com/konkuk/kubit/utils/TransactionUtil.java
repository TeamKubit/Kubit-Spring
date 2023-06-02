package com.konkuk.kubit.utils;

public class TransactionUtil {
    public static final double CHARGE = 0.0005;
    public static int getCharge(double requestPrice, double quantity){
        return (int) (requestPrice * quantity * CHARGE);
    }
}
