package com.lcl.ocr.util;

import static com.lcl.ocr.util.StringUtils.convert8orOorBString;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期工具类
 */
public class DateUtils {

    /**
     * Aadhaar：时间格式处理
     */
    public static String parseAadhaarDate(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        List<String> patterns = Arrays.asList(
                "(\\d{2})/(\\d{2})/(\\d{4})",   // dd/MM/yyyy
                "(\\d{2})(\\d{2})(\\d{4})",     // ddMMyyyy
                "(\\d{2})/(\\d{2})(\\d{4})",    // dd/MMyyyy
                "(\\d{2})(\\d{2})/(\\d{4})",    // ddMM/yyyy
                "Year of Birth (\\d{4})",       // 只包含年份
                "Year of Birth: (\\d{4})",       // 只包含年份
                "Year of Birth:(\\d{4})",       // 只包含年份
                "Year of Birth(\\d{4})",       // 只包含年份
                "Birth (\\d{4})",               // 只包含年份
                "Birth: (\\d{4})",               // 只包含年份
                "Birth(\\d{4})",               // 只包含年份
                "irth (\\d{4})",               // 只包含年份
                "irth(\\d{4})",               // 只包含年份
                "(\\d{2})/(\\d{2})/(\\d{3})",   // dd/MM/yyy
                "YearofBirth:(\\d{4})"          // 只包含年份
//                "(\\d{4})"                      // 只包含年份
        );
        for (String pattern : patterns) {
            Matcher matcher = Pattern.compile(pattern).matcher(input);
            if (matcher.find()) {
                if (matcher.groupCount() == 3) {
                    try {
                        // dd/MM/yyyy 或 ddMMyyyy 格式
                        String day = matcher.group(1);
                        if (day == null || day.isEmpty()) {
                            return "-1";
                        }
                        String month = matcher.groupCount() > 1 ? matcher.group(2) : "0" + day.charAt(0); // 假设ddMMyyyy格式时，第二位是月份
                        if (month == null || month.isEmpty()) {
                            return "-1";
                        }
                        String year = matcher.group(3);
                        if (year == null || year.length() != 4) {
                            return "-1";
                        }
                        // 转换特殊字符，因为会有识别的问题
                        day = convert8orOorBString(day);
                        month = convert8orOorBString(month);
                        year = convert8orOorBString(year);
                        // 补全两位数的月和日
                        if (day.length() == 1) day = "0" + day;
                        if (month.length() == 1) month = "0" + month;
                        // 日/月/年
                        String dateStr = day + "/" + month + "/" + year;
                        // 年/月/日
//                        String dateStr = year + "-" + month + "-" + day;
//                        Log.e("日期", "获取到===========" + dateStr);
                        return dateStr;
                    } catch (Exception e) {
                        // 忽略无效的日期字符串
//                        Log.e("日期", "失败原因：" + e.getMessage());
                    }
                } else if (matcher.groupCount() == 1) {
                    // 只包含年份的情况
                    return convert8orOorBString(matcher.group(1));
                }
            }
        }
        return ""; // 没有找到匹配的日期格式
    }

    /**
     * Pan：时间格式处理
     */
    public static String parsePanDate(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        List<String> patterns = Arrays.asList(
                "(\\d{2})/(\\d{2})/(\\d{4})"   // dd/MM/yyyy
        );
        for (String pattern : patterns) {
            Matcher matcher = Pattern.compile(pattern).matcher(input);
            if (matcher.find()) {
                if (matcher.groupCount() == 3) {
                    try {
                        // dd/MM/yyyy 或 ddMMyyyy 格式
                        String day = matcher.group(1);
                        if (day == null || day.isEmpty()) {
                            return "";
                        }
                        String month = matcher.groupCount() > 1 ? matcher.group(2) : "0" + day.charAt(0); // 假设ddMMyyyy格式时，第二位是月份
                        if (month == null || month.isEmpty()) {
                            return "";
                        }
                        String year = matcher.group(3);
                        if (year == null || year.length() != 4) {
                            return "";
                        }
                        // 转换特殊字符，因为会有识别的问题
                        day = convert8orOorBString(day);
                        month = convert8orOorBString(month);
                        year = convert8orOorBString(year);
                        // 补全两位数的月和日
                        if (day.length() == 1) day = "0" + day;
                        if (month.length() == 1) month = "0" + month;
                        // 日/月/年
                        String dateStr = day + "/" + month + "/" + year;
                        // 年/月/日
//                        String dateStr = year + "-" + month + "-" + day;
//                        Log.e("日期", "获取到===========" + dateStr);
                        return dateStr;
                    } catch (Exception e) {
                        // 忽略无效的日期字符串
//                        Log.e("日期", "失败原因：" + e.getMessage());
                    }
                } else if (matcher.groupCount() == 1) {
                    // 只包含年份的情况
                    return convert8orOorBString(matcher.group(1));
                }
            }
        }
        return ""; // 没有找到匹配的日期格式
    }

    /**
     * 根据给定的日期格式字符串解析日期
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
