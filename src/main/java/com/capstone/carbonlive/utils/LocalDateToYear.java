package com.capstone.carbonlive.utils;

import java.time.LocalDate;

public class LocalDateToYear {
    public static String getYear(LocalDate date) {
        String[] str = date.toString().split("-");
        return str[0];
    }
}
