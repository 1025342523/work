package com.yifarj.yifadinghuobao.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 数字处理工具
 *
 * @auther Czech.Yuan
 * @date 2017/5/21 8:58
 */

public class NumberUtil {

    /**
     * 保留一个double值三位小数,这个方法不能用于过大的数
     */
    public static double formatDouble(double d) {
        DecimalFormat df = new DecimalFormat("#.###");
        try {
            return Double.parseDouble(df.format(d));
        } catch (NumberFormatException e) {
            return d;
        }

//        return (double) Math.round(d * 100) / 100;
    }


    public static double decimalFormatDouble(double d) {
        BigDecimal bigDecimal = new BigDecimal(d);
        try {
            return bigDecimal.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (NumberFormatException e) {
            return d;
        }
    }

    public static String formatDoubleToString(Double d) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.000");//格式化设置
        return decimalFormat.format(d);
    }

    /**
     * 将String转化为int
     */
    public static int formatInt(String str) {
        return Integer.parseInt(str);
    }

    /**
     * 注意，这个方法生成的double字符串已经无法再转换成double类型，因为中间加了逗号隔断
     * 将{@link #formatDouble(double)} 的值转成String类型
     */
    public static String formatDouble2String(double d) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.setMaximumFractionDigits(4); //设置最多小数位数
        df.setMinimumFractionDigits(0); //设置最少小数位数
        return df.format(d);
    }

    /**
     * 保留三位小数,这个方法不能用于过大的数
     */
    public static float formatFloat(float f) {
        DecimalFormat df = new DecimalFormat("#.###");
        return Float.parseFloat(df.format(f));

//        return (float) Math.round(f * 100) / 100;
    }

    /**
     * 将{@link #formatFloat(float)} 的值转成String类型
     */
    public static String formatFloat2String(float f) {
        return String.valueOf(formatFloat(f));
    }

    public static String dateToString() {
        SimpleDateFormat df = new SimpleDateFormat(("yyyyMMddHHmmss"));
        return df.format(new Date());
    }
}
