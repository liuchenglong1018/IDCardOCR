package com.lcl.ocr;

import androidx.annotation.NonNull;

import com.google.mlkit.vision.text.Text;

public interface OnTextResultListener {

    void onSuccess(Text text);

    void onFailure(@NonNull Exception e);
}
