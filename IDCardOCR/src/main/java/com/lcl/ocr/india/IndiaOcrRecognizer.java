package com.lcl.ocr.india;


import androidx.annotation.NonNull;

import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import com.lcl.ocr.OnOCRResultListener;
import com.lcl.ocr.OnPhotoResultListener;
import com.lcl.ocr.OnTextResultListener;
import com.lcl.ocr.TextRecognizerResult;
import com.lcl.ocr.util.DateUtils;

import java.util.HashMap;

/**
 * 印度OCR识别器
 */
public class IndiaOcrRecognizer {

    private IndiaOcrRecognizer() {
    }

    public static IndiaOcrRecognizer getInstance() {
        return OcrRecognizerHolder.instance;
    }

    private static class OcrRecognizerHolder {
        private static final IndiaOcrRecognizer instance = new IndiaOcrRecognizer();
    }

    // Aadhaar卡识别信息
    private HashMap<String, String> aadhaarMap = null;
    // Pan卡识别信息
    private HashMap<String, String> panMap = null;

    /**
     * Aadhaar和Pan卡识别信息
     */
    public void getAadhaarAndPanInfo(
            @NonNull String aadhaarPath,
            @NonNull String panPath,
            @NonNull OnOCRResultListener listener) {
        // Aadhaar
        getAadhaarInfo(aadhaarPath, new OnOCRResultListener() {
            @Override
            public void onSuccess(HashMap<String, String> ocrInfo) {
                aadhaarMap = ocrInfo;
                handleResultDate(listener);
            }

            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure(e);
            }
        });

        // Pan
        getPanInfo(panPath, new OnOCRResultListener() {
            @Override
            public void onSuccess(HashMap<String, String> ocrInfo) {
                panMap = ocrInfo;
                handleResultDate(listener);
            }

            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure(e);
            }
        });
    }

    /**
     * Aadhaar卡识别信息
     */
    public void getAadhaarInfo(@NonNull String path, @NonNull OnOCRResultListener listener) {
        TextRecognizerOptionsInterface textRecognizerOptions = new DevanagariTextRecognizerOptions.Builder().build();
        TextRecognizerResult.getInstance()
                .getTextResult(path, textRecognizerOptions, new OnTextResultListener() {
                    @Override
                    public void onSuccess(Text text) {
                        HashMap<String, String> map = IndiaOCRProcessing.getAadhaarCardInfo(text);
                        singleHandleResultDate(map, true, listener);
                    }

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e);
                    }
                });
    }

    /**
     * Pan卡识别信息
     */
    public void getPanInfo(@NonNull String path, @NonNull OnOCRResultListener listener) {
        TextRecognizerOptionsInterface textRecognizerOptions = new DevanagariTextRecognizerOptions.Builder().build();
        TextRecognizerResult.getInstance()
                .getTextResult(path, textRecognizerOptions, new OnTextResultListener() {
                    @Override
                    public void onSuccess(Text text) {
                        HashMap<String, String> map = IndiaOCRProcessing.getPanCardInfo(text);
                        singleHandleResultDate(map, false, listener);
                    }

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e);
                    }
                });
    }

    /**
     * 相册图片
     */
    public void getPhotoAllInfo(@NonNull String path, @NonNull OnPhotoResultListener listener) {
        TextRecognizerOptionsInterface textRecognizerOptions = new DevanagariTextRecognizerOptions.Builder().build();
        TextRecognizerResult.getInstance()
                .getTextResult(path, textRecognizerOptions, new OnTextResultListener() {
                    @Override
                    public void onSuccess(Text text) {
                        listener.onSuccess(text.getText());
                    }

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e);
                    }
                });
    }

    /**
     * 处理返回的数据
     * 1.日期：日期是否正确，错误的日期需要删除，把正确的日期返回出去
     */
    private void handleResultDate(OnOCRResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        if (null != panMap && null != aadhaarMap) {
            map.putAll(aadhaarMap);
            map.putAll(panMap);
            // Aadhaar是否验证通过过，验证过就不在
            boolean isAadhaarDateVerifyYse = false;
            for (String key : map.keySet()) {
                String value = map.get(key);
                if ("aadhaar_date".equals(key) && DateUtils.isValidDate(value)) {
                    // Aadhaar日期
                    isAadhaarDateVerifyYse = true;
                    map.put("dateOfBirth", value);
                    break;
                }
            }
            if (!isAadhaarDateVerifyYse) {
                // Aadhaar未通过，校验Pan卡的日期
                for (String key : map.keySet()) {
                    String value = map.get(key);
                    if ("pan_date".equals(key) && DateUtils.isValidDate(value)) {
                        // Aadhaar日期
                        map.put("dateOfBirth", value);
                        break;
                    }
                }
            }
            listener.onSuccess(map);
        }
    }

    /**
     * 单独处理返回的数据
     * 1.日期：日期是否正确，错误的日期需要删除，把正确的日期返回出去
     */
    private void singleHandleResultDate(HashMap<String, String> map, boolean isAadhaarCard, OnOCRResultListener listener) {
        if (null != map) {
            for (String key : map.keySet()) {
                String value = map.get(key);
                if (isAadhaarCard) {
                    if ("aadhaar_date".equals(key) && DateUtils.isValidDate(value)) {
                        // Aadhaar日期
                        map.put("dateOfBirth", value);
                        break;
                    }
                } else {
                    if ("pan_date".equals(key) && DateUtils.isValidDate(value)) {
                        // Aadhaar日期
                        map.put("dateOfBirth", value);
                        break;
                    }
                }

            }
            listener.onSuccess(map);
        }
    }
}
