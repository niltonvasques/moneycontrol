package br.niltonvasques.moneycontrol.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by niltonvasques on 6/10/15.
 */
public class MathUtil {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
