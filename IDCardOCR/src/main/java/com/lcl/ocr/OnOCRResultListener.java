package com.lcl.ocr;

import androidx.annotation.NonNull;


import java.util.HashMap;

/**
 * OCR识别结果监听
 */
public interface OnOCRResultListener {

    void onSuccess(HashMap<String, String> ocrInfo);

    void onFailure(@NonNull Exception e);
}
