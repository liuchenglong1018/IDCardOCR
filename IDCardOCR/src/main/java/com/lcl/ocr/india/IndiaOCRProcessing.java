package com.lcl.ocr.india;

import android.util.Log;

import com.google.mlkit.vision.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 印度OCR India
 */
public class IndiaOCRProcessing {

    /**
     * =============================== Aadhaar卡片 ===============================
     */

    /**
     * 获取Aadhaar卡片信息
     */
    public static HashMap<String, String> getAadhaarCardInfo(Text text) {
        List<Text.TextBlock> blockList = text.getTextBlocks();
        if (blockList.size() == 0) {
            return null;
        }
        HashMap<String, String> hashMap = new HashMap<>();

        // 过滤后的所有数据
        List<String> orderedData = new ArrayList<>();

//        Log.e("文字识别", "=========识别开始=======");
        StringBuilder filterText = new StringBuilder();
        for (Text.TextBlock block : text.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
//                Rect rect = line.getBoundingBox();
//                String y = String.valueOf(rect.exactCenterY());
                String lineTxt = line.getText();
                if (isAadhaarFilterInfo(lineTxt)) {
//                    Log.e("文字识别", lineTxt);
                    filterText.insert(0, lineTxt + "\n");
                    orderedData.add(lineTxt);
                }
            }
        }

        // 识别出的文字
        hashMap.put("aadhaar_text", filterText.toString());

        // 设置唯一ID
        for (int i = 0; i < orderedData.size(); i++) {
            String idCardNum = orderedData.get(i).replaceAll("\\s", "");// 去除空格
            // Aadhaar号码
            if (isAadhaarNumber(idCardNum)) {
                hashMap.put("aadhaar_id", idCardNum);
                break;
            }
        }
        // 卡片类型
//        listText.add(new TextTypeBean("id_card_type", "1"));

        // 姓名
        String beforeName = "";
        boolean isVerifyName = false;
        for (int i = 0; i < orderedData.size(); i++) {
            String textName1 = orderedData.get(i).replace(",", "");
            String textName2 = textName1.replace(".", "");
//            Log.e("文字识别", textName2);
            if (isIndiaName(textName2)) {
                isVerifyName = true;
                hashMap.put("aadhaar_name", textName2);
            }
            if (!isVerifyName && isIndiaName2(textName2)) {
                // 设置前一个保存的信息
                hashMap.put("aadhaar_name", beforeName);
            }
            if (textName2.length() >= 4) {
                beforeName = textName2;
            }
        }

        // 设置性别
        for (int i = 0; i < orderedData.size(); i++) {
            String genderInfo = getGenderInfo(orderedData.get(i));
            if (!genderInfo.isEmpty()) {
                hashMap.put("aadhaar_gender", genderInfo);
                break;
            }
        }

        // 出生年月
        for (int i = 0; i < orderedData.size(); i++) {
            String date = getDateInfo(orderedData.get(i));
            if (!date.isEmpty()) {
//                Log.e("日期", "=======Aadhaar=======" + date);
                hashMap.put("aadhaar_date", date);
                break;
            }
        }
//        Log.e("Aadhaar", "识别成功=======\n" + hashMap);
        return hashMap;
    }

    /**
     * Aadhaar过滤垃圾信息
     */
    private static boolean isAadhaarFilterInfo(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return !str.contains("Government of India")
                && !str.contains("GOVERNMENT OF INDIA")
                && !str.contains("Government of")
                && !str.contains("Governmentof")
                && !str.contains("Government")
                && !str.contains("GOVERNMENT")
                && !str.contains("GOVERNMENT OF")
                && !str.contains("GOVERNMENTOF")
                && !str.contains("OFINDIA")
                && !str.contains("MERI PEHCHAN")
                && !str.contains("AADHAAR")
                && !str.contains("AADHAAR MERI")
                && !str.contains("MERA AADHAAR MERI")
                && !str.contains("MERA AADHAAR, MERI PEHCHAN")
                && !str.contains("proof of")
                && !str.contains("of identity")
                && !str.contains("Aadhaar is proof of identity not of citizenship")
                && !str.contains("Aadhaar is")
                && !str.contains("Aadhaar")
                && !str.contains("Fathe")
                && !str.contains("of citizenship")
                && !str.contains("or date of birth")
                && !str.contains("should be")
                && !str.contains("with verification")
                && !str.contains("QR code")
                && !str.contains("It shou")
                && !str.contains("used with verification")
                && !str.contains("(online")
                && !str.contains("XML")
                && !str.contains("India")
                && !str.contains("INDIA")
                && !str.contains("india")
                && !str.contains("Enrollment No")
                && !str.contains("Issue Date")
                && !str.contains("Issue");
    }

    /**
     * Aadhaar号则，带空格和不带空格的校验
     */
    private static boolean isAadhaarNumber(String str) {
        // 不带空格正则
        String aadhaarNumRegular = "\\b[2-9][0-9]{11}\\b";
        // 带空格正则
        String aadhaarNumSpaceRegular = "\\b[2-9][0-9]{3} [0-9]{4} [0-9]{4}\\b";
        return str.matches(aadhaarNumRegular) || str.matches(aadhaarNumSpaceRegular);
    }

    /**
     * 印度名字规则
     */
    private static boolean isIndiaName(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("[a-zA-Z\\s+]*")
                && !str.equals("Government of India")
                && !str.equals("GOVERNMENT OF INDIA")
                && !str.contains("Government of")
                && !str.contains("Government")
                && !str.contains("GOVERNMENT")
                && !str.contains("India")
                && !str.contains("INDIA")
                && !str.contains("india")
                && !str.contains("vernme")
                && !str.contains("Male")
                && !str.contains("male")
                && !str.contains("MALE")
                && !str.contains("Female")
                && !str.contains("FEMALE")
                && !str.contains("Date")
                && !str.contains("Dete")
                && !str.contains("GOVERRNT CBDES")
                && !str.contains("GOVERRNT");
    }

    private static boolean isIndiaName2(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.contains("Year")
                || str.contains("Date")
                || str.contains("Birth")
                || str.contains("DOB")
                || str.contains("Oras");
    }

    /**
     * 性别规则
     */
    private static String getGenderInfo(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        if (str.contains("female")
                || str.contains("Female")
                || str.contains("FEMALE")
                || str.contains("/FE")
                || str.contains("/Fe")) {
            return "F";
        }
        if (str.contains("male")
                || str.contains("Male")
                || str.contains("MALE")
                || str.contains("/MA")
                || str.contains("/ALE")
                || str.contains("/ ALE")
                || str.contains("/ MAE")
                || str.contains("MAL")
                || str.contains("MAE")
                || str.contains("/Ma")) {
            return "M";
        }
        return "";
    }

    /**
     * 出生日期
     */
    private static String getDateInfo(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        if (str.contains("Year of Birth")
                || str.contains("Year of")
                || str.contains("Birth")
                || str.contains("birth")
                || str.contains("DOB")
                || str.contains("B:")
                || str.contains("8:")
                || str.contains("/DO")) {
            // 去除所有空格
            String text = str.replaceAll("\\s", "");
            // 符号识别错误转换
            if (text.contains(";")) {
                text = text.replace(";", ":");
            }
            // 符号识别错误转换
            if (text.contains(".")) {
                text = text.replace(".", ":");
            }
//            Log.e("日期", "=======匹配成功======="+ dotStr);

            // 第一次匹配，成功直接返回
            String date = parseDate(text);
            if (date.isEmpty()) {
                // 第一次匹配为空，采用截取的方式匹配第二次
                String dateOnly = extractDateOnly(text);
                // 通过截取的日期，再格式化一下
                date = parseDate(dateOnly);
                if (date.isEmpty()) {
                    // 第二次格式化为空，把截取的内容直接返回回去
                    date = dateOnly;
                }
            }
            if (isDatePureNum(date)) {
                return date;
            }
        }
        return "";
    }

    /**
     * 时间格式处理
     */
    private static String parseDate(String input) {
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
                "YearofBirth:(\\d{4})"          // 只包含年份
        );
        for (String pattern : patterns) {
            Matcher matcher = Pattern.compile(pattern).matcher(input);
            if (matcher.find()) {
                if (matcher.groupCount() == 3) {
                    try {
                        // dd/MM/yyyy 或 ddMMyyyy 格式
                        String day = matcher.group(1);
                        String month = matcher.groupCount() > 1 ? matcher.group(2) : "0" + day.substring(0, 1); // 假设ddMMyyyy格式时，第二位是月份
                        String year = matcher.group(3);
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
                    return matcher.group(1);
                }
            }
        }
        return ""; // 没有找到匹配的日期格式
    }

    /**
     * 截取字段
     */
    private static String extractDateOnly(String input) {
        // 截取格式，这里根据识别文字的事迹情况来添加格式
        List<String> patternList = Arrays.asList(
                ":", "DOB", "OB", "Birth", "rth", "th", "DO8", "O8"
        );
        for (String pattern : patternList) {
            if (input.contains(pattern)) {
                return input.substring(input.indexOf(pattern) + 1).trim();
            }
        }
        // 极端情况下没有截取到
        // 这里可以根据实际情况添加更复杂的逻辑来提取日期部分
        // 例如，使用正则表达式查找符合日期格式的部分
        // 我这里用的是，移除非数字和斜杠的字符
        return input.replaceAll("[^\\d/]", "").trim();
    }

    /**
     * 判断时间是否是纯数字
     * @return 如果字符串是由纯数字组成，则返回true；否则返回false
     */
    private static boolean isDatePureNum(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        String date = removeSlashes(input);
        return date.matches("\\d+");
    }

    /**
     * 去除所有'/'或'\'字符
     */
    private static String removeSlashes(String input) {
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
     * =============================== Pan卡片 ===============================
     */

    /**
     * 获取Pan卡片信息
     */
    public static HashMap<String, String> getPanCardInfo(Text text) {
        List<Text.TextBlock> blockList = text.getTextBlocks();
        if (blockList.size() == 0) {
            return null;
        }
        HashMap<String, String> hashMap = new HashMap<>();

        // 过滤后的所有数据
        List<String> orderedData = new ArrayList<>();

//        Log.e("文字识别", "=========识别开始=======");
        StringBuilder filterText = new StringBuilder();
        for (Text.TextBlock block : text.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
//                Rect rect = line.getBoundingBox();
//                String y = String.valueOf(rect.exactCenterY());
//                String lineTxt = line.getText().toLowerCase();
                String lineTxt = line.getText();
                if (isPanFilterInfo(lineTxt) && isAadhaarFilterInfo(lineTxt)) {
//                    Log.e("文字识别", lineTxt);
                    filterText.insert(0, lineTxt + "\n");
                    orderedData.add(lineTxt);
                }
            }
        }
        // 识别出的文字
        hashMap.put("pan_text", filterText.toString());

        // 姓名
        String name = "";
        for (int i = 0; i < orderedData.size(); i++) {
            String textName1 = orderedData.get(i).replace(",", "");
            String textName2 = textName1.replace(".", "");
//            Log.e("文字识别", textName2);
            if (isAllUpperCase(textName2)) {
                name = textName2;
                hashMap.put("pan_name", textName2);
                break;
            }
        }

        // 设置唯一ID
        for (int i = 0; i < orderedData.size(); i++) {
            String nameComparison = name.replaceAll("\\s", "");// 去除空格
            String idCardNum = orderedData.get(i).replaceAll("\\s", "");// 去除空格
            // Pan号码判断
            if (!nameComparison.equals(idCardNum) && isPanNumber(idCardNum)) {
                hashMap.put("pan_id", idCardNum);
                break;
            }
        }

        // 日期
        for (int i = 0; i < orderedData.size(); i++) {
            String date = orderedData.get(i).replaceAll("\\s", "");// 去除空格
            String parseDate = parsePanDate(date);
            if (isDatePureNum(parseDate)) {
//                Log.e("日期", "=======Pan=======" + parseDate);
                hashMap.put("pan_date", parseDate);
            }
        }

        // 卡片类型
//        listText.add(new TextTypeBean("id_card_type", "2"));
        return hashMap;
    }

    /**
     * Pan号码正则，或者长度为10,并且需要包含数字和大写英文
     */
    private static boolean isPanNumber(String str) {
        if (str == null || str.isEmpty() || str.length() != 10) {
            return false;
        }
        // Pan号码正则
        String panNumRegular = "(?i:\\b[A-Z]{3}[ABCFGHJLPT][A-Z]\\d{4}[A-Z]\\b)";
        if (str.matches(panNumRegular)) {
            return true;
        }
        int upperCaseCount = 0;
        int digitCount = 0;

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
     * PAN卡过滤垃圾信息
     */
    private static boolean isPanFilterInfo(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return !containsGujaratiChars(str)
                && !isNumeric(str)
                && !str.contains("INCOME TAX DEPARTMENT")
                && !str.contains("INCOMETAX")
                && !str.contains("TAXDEPARTMENT")
                && !str.contains("DEPARTMENT")
                && !str.contains("INCOME")
                && !str.contains("Permanent")
                && !str.contains("Permanent Account Number")
                && !str.contains("Permanent Account")
                && !str.contains("Account Number")
                && !str.contains("Number Card")
                && !str.contains("Card")
                && !str.contains("Number")
                && !str.contains("number")
                && !str.contains("GOVT. OF INDA")
                && !str.contains("GOVT OF INDIA")
                && !str.contains("OF INDIA")
                && !str.contains("GOVT OF")
                && !str.contains("GOVT.")
                && !str.contains("INDIA")
                && !str.contains("Signature")
                && !str.contains("Name")
                && !str.contains("name")
                && !str.contains("Father's")
                && !str.contains("Father")
                && !str.contains("/ Dale of")
                && !str.contains("Dale of")
                && !str.contains("Dale")
                && !str.contains("dale")
                && !str.contains("/ Date of")
                && !str.contains("Date of")
                && !str.contains("Date")
                && !str.contains("date")
                && !str.contains("'")
                && !str.contains("।")
                && !str.contains("स्थायी लेखा संख्या कार्ड")
                && !str.contains("भारत सरकार")
                && !str.contains("आयकर विभाग")
                && !str.contains("जन्म की तारीख");
    }

    private static boolean containsGujaratiChars(String input) {
        // 印地语字符的Unicode范围
        String gujaratiUnicodeRange = "[\u0A81-\u0AEF\u0AEE\u0A83]";
        return input.matches(".*?" + gujaratiUnicodeRange + ".*");
    }

    /**
     * 时间格式处理
     */
    private static String parsePanDate(String input) {
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
                        String month = matcher.groupCount() > 1 ? matcher.group(2) : "0" + day.substring(0, 1); // 假设ddMMyyyy格式时，第二位是月份
                        String year = matcher.group(3);
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
                    return matcher.group(1);
                }
            }
        }
        return ""; // 没有找到匹配的日期格式
    }

    /**
     * 是否是纯数字
     */
    private static boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    /**
     * 判断全是字母组合，支持带空格检索
     */
    private static boolean isAllUpperCase(String str) {
        return str.matches("[a-zA-Z\\s]+");
    }
}
