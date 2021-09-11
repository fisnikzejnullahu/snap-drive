package com.fisnikz.snapdrive;

import java.math.RoundingMode;
import java.text.NumberFormat;

public class DoubleNumberFormat {

    public static String format(double num) {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.HALF_UP);

        return nf.format(num);
    }
}