package br.niltonvasques.moneycontrol.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtil {
    public static String format(double value) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setMaximumFractionDigits(2);
        return decimalFormat.format(value);
    }

    public static String format(float value) {
        return format(Double.valueOf(value));
    }
}
