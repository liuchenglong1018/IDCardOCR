package com.lcl.ocr.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtils {

    /**
     * 尝试根据给定的日期格式字符串解析日期
     * @param dateString 待验证的日期字符串
     * @return 如果日期有效且符合任一格式，则返回true；否则返回false
     */
    public static boolean isValidDate(String dateString) {
        String[] formats = {"dd/MM/yyyy", "yyyy"};
        for (String format : formats) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false); // 不允许解析不严格的日期
            try {
                // 尝试解析日期
                Date date = sdf.parse(dateString);
                // 如果能解析到日期，则认为是有效的
                return true;
            } catch (ParseException e) {
                // 如果解析失败，则继续尝试下一个格式
            }
        }
        // 所有格式都尝试过后仍然失败，则日期无效
        return false;
    }
}
