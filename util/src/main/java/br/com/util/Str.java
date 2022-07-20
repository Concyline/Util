package br.com.util;

public class Str {

    public static <T extends Object> String Format(String value, T... parms) throws Exception{

        for (int i = 0; i < parms.length; i++) {

            String param = String.valueOf(parms[i]);
            String tag = String.format("{%s}",i);

            if(value.contains(tag)) {
                value = value.replace(tag, param);
            }

        }

        return value;
    }

}
