package com.lcl.ocr;

import androidx.annotation.NonNull;

public interface OnPhotoResultListener {

    void onSuccess(String textStr);

    void onFailure(@NonNull Exception e);
}
