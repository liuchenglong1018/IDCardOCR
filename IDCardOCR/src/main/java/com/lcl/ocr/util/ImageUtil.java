package com.lcl.ocr.util;

import android.media.ExifInterface;

public class ImageUtil {

    /**
     * 获取图片的旋转角度
     *
     * @param imagePath 图片的路径
     * @return 图片的旋转角度（0, 90, 180, 270）之一，如果无法获取则为0
     */
    public static int getRotationAngle(String imagePath) {
        int rotation = 0;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 如果没有EXIF信息或者发生异常，则默认返回0
        }

        return rotation;
    }
}
