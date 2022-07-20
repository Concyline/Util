package br.com.util;

public class Str {

    public static String Format(String value, Object param0)throws Exception{
        return Format(value, new Object[]{param0});
    }

    public static String Format(String value, Object param0, Object param1)throws Exception{
        return Format(value, new Object[]{param0, param1});
    }

    public static String Format(String value, Object param0, Object param1, Object param2)throws Exception{
        return Format(value, new Object[]{param0, param1, param2});
    }

    public static String Format(String value, Object param0, Object param1, Object param2, Object param3)throws Exception{
        return Format(value, new Object[]{param0, param1, param2, param3});
    }

    public static String Format(String value, Object param0, Object param1, Object param2, Object param3, Object param4)throws Exception{
        return Format(value, new Object[]{param0, param1, param2, param3, param4});
    }

    public static String Format(String value, Object param0, Object param1, Object param2, Object param3, Object param4, Object parem5) throws Exception{
        return Format(value, new Object[]{param0, param1, param2, param3, param4, parem5});
    }


    private static String Format(String value, Object... parms) throws Exception{

        for (int i = 0; i < parms.length; i++) {

            String param = String.valueOf(parms[i]);
            String tag = "{"+i+"}";

            if(value.contains(tag)) {
                value = value.replace(tag, param);
            }

        }

        return value;
    }


}
