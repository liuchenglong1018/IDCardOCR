package com.lcl.ocr.util;


import java.util.Arrays;
import java.util.List;

/**
 * 字符串工具类
 */
public class StringUtils {

    /**
     * =============================== String ===============================
     */

    /**
     * 去除所有空格
     */
    public static String removeAllSpace(String str) {
        return allReplace(str, "\\s", "");
    }

    /**
     * 根据传进来的规则替换单个
     * @param str           原始数据
     * @param regex         正则
     * @param replacement   替代品
     * @return  替换后的数据
     */
    public static String singleReplace(String str, String regex, String replacement) {
        return str.replace(regex, replacement).trim();
    }

    /**
     * 根据传进来的规则替换所有
     * @param str           原始数据
     * @param regex         正则
     * @param replacement   替代品
     * @return  替换后的数据
     */
    public static String allReplace(String str, String regex, String replacement) {
        return str.replaceAll(regex, replacement).trim();
    }

    /**
     * =============================== Aadhaar卡片 ===============================
     */

    /**
     * Aadhaar卡姓名特殊字符替换
     */
    public static String aNameReplace(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        if (name.contains("|")) {
            name = singleReplace(name, "|", "");
        }
        if (name.contains(",")) {
            name = singleReplace(name, ",", "");
        }
        if (name.contains(".")) {
            name = singleReplace(name, ".", "");
        }
        return name;
    }

    /**
     * Aadhaar号则，带空格和不带空格的校验
     */
    public static boolean aIsNumber(String str) {
        // 不带空格正则
        String aadhaarNumRegular = "\\b[2-9][0-9]{11}\\b";
        // 带空格正则
        String aadhaarNumSpaceRegular = "\\b[2-9][0-9]{3} [0-9]{4} [0-9]{4}\\b";
        return str.matches(aadhaarNumRegular) || str.matches(aadhaarNumSpaceRegular);
    }

    /**
     * Aadhaar日期截取
     */
    public static String aExtractDateOnly(String str) {
        // 截取格式，这里根据识别文字的事迹情况来添加格式
        List<String> patternList = Arrays.asList(
                ":", "DOB", "OB", "Birth", "rth", "th", "DO8", "O8"
        );
        for (String pattern : patternList) {
            if (str.contains(pattern)) {
                return str.substring(str.indexOf(pattern) + 1).trim();
            }
        }
        // 极端情况下没有截取到
        // 这里可以根据实际情况添加更复杂的逻辑来提取日期部分
        // 例如，使用正则表达式查找符合日期格式的部分
        // 我这里用的是，移除非数字和斜杠的字符
        return allReplace(str,"[^\\d/]", "");
    }

    /**
     * =============================== Pan卡片 ===============================
     */

    /**
     * Pan号码正则，或者长度为10,并且需要包含数字和大写英文
     */
    public static boolean pIsCardNum(String str) {
        if (str == null || str.length() < 10) {
            return false;
        }

        // Pan号码正则
        String panNumRegular = "(?i:\\b[A-Z]{3}[ABCFGHJLPT][A-Z]\\d{4}[A-Z]\\b)";
        if (str.matches(panNumRegular)) {
            return true;
        }
        // 修改特殊字符，修改后再次走正则
        str = StringUtils.panNumModifySpecialStr(str);
        if (str.matches(panNumRegular)) {
            return true;
        }

        int upperCaseCount = 0;
        int digitCount = 0;
        // 修饰后的数据如果还是不能被正则匹配，
        // 那就统计判断：大写英文字母至少6位，数字至少4位
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                // 是大写英文字母
                upperCaseCount++;
            } else if (Character.isDigit(c)) {
                // 是数字
                digitCount++;
            } else {
                // 包含其他字符
                return false;
            }
        }
        // 检查大写英文字母至少6位，数字至少4位
        return upperCaseCount >= 6 && digitCount >= 4;
    }

    /**
     * Pan卡姓名特殊字符替换
     */
    public static String pNameReplace(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        name = name.replace(",", "");
        name = name.replace(".", "");
        return name;
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
     * =============================== 其它 ===============================
     */

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
