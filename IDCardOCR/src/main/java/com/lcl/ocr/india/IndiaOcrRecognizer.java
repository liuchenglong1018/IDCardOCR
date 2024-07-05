package com.lcl.ocr.india;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import com.lcl.ocr.OnOCRResultListener;
import com.lcl.ocr.OnPhotoResultListener;
import com.lcl.ocr.OnTextResultListener;
import com.lcl.ocr.TextRecognizerResult;

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
            @NonNull Bitmap aadhaarBitmap,
            @NonNull Bitmap panBitmap,
            @NonNull OnOCRResultListener listener) {
        // Aadhaar
        getAadhaarInfo(aadhaarBitmap, new OnOCRResultListener() {
            @Override
            public void onSuccess(HashMap<String, String> ocrInfo) {
                aadhaarMap = ocrInfo;
                if (null != panMap && null != aadhaarMap) {
                    aadhaarMap.putAll(panMap);
                    listener.onSuccess(aadhaarMap);
                }
            }

            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure(e);
            }
        });

        // Pan
        getPanInfo(panBitmap, new OnOCRResultListener() {
            @Override
            public void onSuccess(HashMap<String, String> ocrInfo) {
                panMap = ocrInfo;
                if (null != panMap && null != aadhaarMap) {
                    aadhaarMap.putAll(panMap);
                    listener.onSuccess(aadhaarMap);
                }
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
    public void getAadhaarInfo(@NonNull Bitmap bitmap, @NonNull OnOCRResultListener listener) {
        TextRecognizerOptionsInterface textRecognizerOptions = new DevanagariTextRecognizerOptions.Builder().build();
        TextRecognizerResult.getInstance()
                .getTextResult(bitmap, textRecognizerOptions, new OnTextResultListener() {
                    @Override
                    public void onSuccess(Text text) {
                        HashMap<String, String> aadhaarInfo = IndiaOCRProcessing.getAadhaarCardInfo(text);
                        listener.onSuccess(aadhaarInfo);
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
    public void getPanInfo(@NonNull Bitmap bitmap, @NonNull OnOCRResultListener listener) {
        TextRecognizerOptionsInterface textRecognizerOptions = new DevanagariTextRecognizerOptions.Builder().build();
        TextRecognizerResult.getInstance()
                .getTextResult(bitmap, textRecognizerOptions, new OnTextResultListener() {
                    @Override
                    public void onSuccess(Text text) {
                        HashMap<String, String> panInfo = IndiaOCRProcessing.getPanCardInfo(text);
                        listener.onSuccess(panInfo);
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
    public void getPhotoAllInfo(@NonNull Bitmap bitmap, @NonNull OnPhotoResultListener listener) {
        TextRecognizerOptionsInterface textRecognizerOptions = new DevanagariTextRecognizerOptions.Builder().build();
        TextRecognizerResult.getInstance()
                .getTextResult(bitmap, textRecognizerOptions, new OnTextResultListener() {
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

    // 筛选日期
//    private static HashMap<String, String> filterDate(
//            HashMap<String, String> aadhaarMap,
//            HashMap<String, String> panMap
//    ) {
//        HashMap<String, String> map = new HashMap<>();
//        if (aadhaarMap != null || panMap == null) {
//            return map;
//        }
//        for (String key : aadhaarMap.keySet()) {
//
//        }
//
//        return map;
//    }

}
