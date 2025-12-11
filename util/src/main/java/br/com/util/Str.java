package br.com.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Str {

    public static String format(String template, Object... values) {
        for (int i = 0; i < values.length; i++) {
            template = template.replace("{" + i + "}", values[i].toString());
        }
        return template;
    }

    public static Double arredondaNumero(Double numeto, int casas) {
        BigDecimal bd = new BigDecimal(numeto);
        bd = bd.setScale(casas, RoundingMode.HALF_UP);
        double arredondado = bd.doubleValue();
        return arredondado;
    }

    public static String join(Object... valores) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < valores.length; i++) {
            sb.append(String.valueOf(valores[i]));

            if (i < valores.length - 1) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

}
