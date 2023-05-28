package com.konkuk.kubit.utils;

public class TransactionUtil {
    public static final double CHARGE = 0.05;
    public static double getCharge(double requestPrice, double quantity){
        return requestPrice * quantity * CHARGE;
    }
}
