package com.lcl.ocr.india;

import static com.lcl.ocr.util.StringUtils.aIsNumber;
import static com.lcl.ocr.util.StringUtils.aNameReplace;
import static com.lcl.ocr.util.StringUtils.containsGujaratiChars;
import static com.lcl.ocr.util.StringUtils.isAllUpperCase;
import static com.lcl.ocr.util.StringUtils.isDatePureNum;
import static com.lcl.ocr.util.StringUtils.isNumeric;
import static com.lcl.ocr.util.StringUtils.pIsCardNum;
import static com.lcl.ocr.util.StringUtils.pNameReplace;
import static com.lcl.ocr.util.StringUtils.removeAllSpace;

import com.google.mlkit.vision.text.Text;
import com.lcl.ocr.util.DateUtils;
import com.lcl.ocr.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                if (aIsFilterSpamInfo(lineTxt)) {
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
            String idCardNum = removeAllSpace(orderedData.get(i));// 去除空格
            // Aadhaar号码
            if (aIsNumber(idCardNum)) {
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
            String textName = aNameReplace(orderedData.get(i));
//            Log.e("文字识别", textName);
            if (aIsNameRule(textName)) {
                isVerifyName = true;
                hashMap.put("aadhaar_name", textName);
            }
            if (!isVerifyName && aNameFilterDateInfo(textName)) {
                // 设置前一个保存的信息
                hashMap.put("aadhaar_name", beforeName);
            }
            if (textName.length() >= 4) {
                beforeName = textName;
            }
        }

        // 设置性别
        for (int i = 0; i < orderedData.size(); i++) {
            String genderInfo = aGenderInfo(orderedData.get(i));
            if (!genderInfo.isEmpty()) {
                hashMap.put("aadhaar_gender", genderInfo);
                break;
            }
        }

        // 出生年月
        for (int i = 0; i < orderedData.size(); i++) {
            String date = aDateInfo(orderedData.get(i));
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
    private static boolean aIsFilterSpamInfo(String str) {
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
                && !str.contains("Issue")
                && !str.contains("S/O")
                && !str.contains("S/o")
                && !str.contains("S/0")
                && !str.contains("C/O")
                && !str.contains("C/o")
                && !str.contains("C/0")
                && !str.contains("Addam")
                && !str.contains("Unique")
                && !str.contains("Identification")
                && !str.contains("uidai")
                && !str.contains(".gov")
                && !str.contains(".in")
                && !str.contains("www")
                && !str.contains("Mathura")
                && !str.contains("Address");
    }

    /**
     * 印度名字规则
     */
    private static boolean aIsNameRule(String str) {
        if (str == null || str.length() < 4) {
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
                && !str.contains(" MALE")
                && !str.contains("MALE ")
                && !str.contains("Female")
                && !str.contains("FEMALE")
                && !str.contains("Date")
                && !str.contains("Dete")
                && !str.contains("GOVERRNT CBDES")
                && !str.contains("GOVERRNT");
    }

    /**
     * 过滤日期信息
     */
    private static boolean aNameFilterDateInfo(String str) {
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
    private static String aGenderInfo(String str) {
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
     * Aadhaar卡：出生日期
     */
    private static String aDateInfo(String str) {
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
            String text = removeAllSpace(str);
            // 符号识别错误转换
            if (text.contains(";")) {
                text = StringUtils.singleReplace(text, ";", ":");
            }
            // 符号识别错误转换
            if (text.contains(".")) {
                text = StringUtils.singleReplace(text,".", ":");
            }
//            Log.e("日期", "=======匹配成功======="+ dotStr);

            // 第一次匹配，成功直接返回
            String date = DateUtils.aParseDate(text);
            if (date.isEmpty()) {
                // 第一次匹配为空，采用截取的方式匹配第二次
                String dateOnly = StringUtils.aExtractDateOnly(text);
                // 通过截取的日期，再格式化一下
                date = DateUtils.aParseDate(dateOnly);
//                if (date.isEmpty() && !dateOnly.isEmpty()) {
//                    // 第二次格式化为空，把截取的内容直接返回回去
//                    date = dateOnly;
//                }
            }
            if (!date.isEmpty() && !date.equals("-1") && isDatePureNum(date)) {
                return date;
            }
        }
        return "";
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
                if (pIsFilterSpamInfo(lineTxt) && aIsFilterSpamInfo(lineTxt)) {
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
            String textName = pNameReplace( orderedData.get(i));
//            Log.e("文字识别", textName);
            if (isAllUpperCase(textName)) {
                name = textName;
                hashMap.put("pan_name", textName);
                break;
            }
        }

        // 设置唯一ID
        for (int i = 0; i < orderedData.size(); i++) {
            String nameComparison = removeAllSpace(name);// 去除空格
            String idCardNum = removeAllSpace(orderedData.get(i));// 去除空格
            // Pan号码判断
            if (!nameComparison.equals(idCardNum) && pIsCardNum(idCardNum)) {
                hashMap.put("pan_id", idCardNum);
                break;
            }
        }

        // 日期
        for (int i = 0; i < orderedData.size(); i++) {
            String date = removeAllSpace(orderedData.get(i));// 去除空格
            String parseDate = DateUtils.pParseDate(date);
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
     * PAN卡过滤垃圾信息
     */
    private static boolean pIsFilterSpamInfo(String str) {
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
                && !str.contains("PAN")
                && !str.contains("Application")
                && !str.contains("Valid unless")
                && !str.contains("Physicaly Signed")
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
                && !str.contains(" TAX ")
                && !str.contains("'")
                && !str.contains("।")
                && !str.contains("स्थायी लेखा संख्या कार्ड")
                && !str.contains("भारत सरकार")
                && !str.contains("आयकर विभाग")
                && !str.contains("जन्म की तारीख");
    }
}
