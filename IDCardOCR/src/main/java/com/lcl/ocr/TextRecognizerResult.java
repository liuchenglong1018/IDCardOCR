package com.lcl.ocr;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;

/**
 * 文本识别器返回
 */
public class TextRecognizerResult {

    private TextRecognizerResult() {
    }

    public static TextRecognizerResult getInstance() {
        return TextRecognizerUtilsHolder.instance;
    }

    private static class TextRecognizerUtilsHolder {
        private static final TextRecognizerResult instance = new TextRecognizerResult();
    }

    /**
     * 获取识别结果
     *
     * @param bitmap  图片
     * @param options 文本识别选择器
     */
    public void getTextResult(
            @NonNull Bitmap bitmap,
            @NonNull TextRecognizerOptionsInterface options,
            @NonNull OnTextResultListener listener) {
        TextRecognizer recognizer = TextRecognition.getClient(options);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        recognizer.process(image)
                .addOnSuccessListener(text -> {
                    listener.onSuccess(text);
                    recognizer.close();
                })
                .addOnFailureListener(
                        e -> {
                            listener.onFailure(e);
                            recognizer.close();
                        });
    }
}
