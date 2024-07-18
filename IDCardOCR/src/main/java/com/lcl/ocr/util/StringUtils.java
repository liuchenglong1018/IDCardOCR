package com.lcl.ocr.util;

/**
 * 字符串工具类
 */
public class StringUtils {

    /**
     * 判断时间是否是纯数字
     * @return 如果字符串是由纯数字组成，则返回true；否则返回false
     */
    public static boolean isDatePureNum(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        String date = removeSlashes(input);
        return date.matches("\\d+");
    }

    /**
     * 去除所有'/'或'\'字符
     */
    public static String removeSlashes(String input) {
        // 使用StringBuilder来构建结果字符串，因为它比字符串连接更高效
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            // 如果字符不是'/'或'\'，则添加到StringBuilder中
            if (c != '/' && c != '\\' && c != '-') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Pan卡号码转换特殊字符：'০'、'o'、'O'、'৪'、'b'、'B'等
     */
    public static String panNumModifySpecialStr(String input) {
        if (input == null || input.length() < 10) {
            return input;
        }
        StringBuilder sb = new StringBuilder(input);
        // 处理前5位字符
        // 判断其中是否包含'8'、'৪'、'0'、'০'、'o'，
        // 如果包含，就将'8'或'৪'改为大写字母'B'，如果包含'0'、'০'、'o'就改为大写字母'O'，其它保持不变；
        for (int i = 0; i < 5; i++) {
            char c = sb.charAt(i);
            if (c == '8' || c == '৪') {
                sb.setCharAt(i, 'B');
            } else if (c == '0' || c == '০' || c == 'o') {
                sb.setCharAt(i, 'O');
            }
        }

        // 处理第6到第9位字符
        // 判断其中是否包含'O'、'০'、'o'、'৪'，
        // 如果包含，就将其改为数字'0'，其它保持不变；
        for (int i = 5; i < 9; i++) {
            char c = sb.charAt(i);
            if (c == 'O' || c == '০' || c == 'o') {
                sb.setCharAt(i, '0');
            } else if (c == '৪') {
                sb.setCharAt(i, '8');
            }
        }

        // 处理最后一位字符
        // 判断其中是否包含'0'、'০'、'o'，
        // 如果包含，就将其改为大写字母'O'，其它保持不变。
        char lastChar = sb.charAt(9);
        if (lastChar == '0' || lastChar == '০' || lastChar == 'o') {
            sb.setCharAt(9, 'O');
        }
        return sb.toString();
    }

    /**
     * 转换字符串中的字符：'০'、'o'、'O'、'৪'、'b'、'B'
     *
     * @param input 输入的字符串
     * @return 转换后的字符串
     */
    public static String convert8orOorBString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (c == '০' || c == 'o' || c == 'O') {
                sb.append('0'); // 将'০'、'o'、'O'转换为'0'
            } else if (c == '৪' || c == 'b' || c == 'B') {
                sb.append('8'); // 将'৪'、'b'、'B'转换为'8'
            } else {
                sb.append(c); // 其他字符保持不变
            }
        }
        return sb.toString();
    }

    /**
     * 印地语字符的Unicode范围
     */
    public static boolean containsGujaratiChars(String input) {
        String gujaratiUnicodeRange = "[\u0A81-\u0AEF\u0AEE\u0A83]";
        return input.matches(".*?" + gujaratiUnicodeRange + ".*");
    }

    /**
     * 判断全是字母组合，支持带空格检索
     */
    public static boolean isAllUpperCase(String str) {
        if (str == null || str.length() < 4) {
            return false;
        }
        return str.matches("[a-zA-Z\\s]+");
    }

    /**
     * 是否是纯数字
     */
    public static boolean isNumeric(String str) {
        return str.matches("\\d+");
    }
}
