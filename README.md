# IDCardOCR

[![](https://jitpack.io/v/liuchenglong1018/IDCardOCR.svg)](https://jitpack.io/#liuchenglong1018/IDCardOCR)

支持国家：
1.印度
更多支持，持续更新...

印度OCR支持Aadaar、Panka卡片类型识别: 
1.id号码
2.姓名
3.年月日
4.性别

# 依赖

* Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

* 添加依赖

```
dependencies {
	        implementation 'com.github.liuchenglong1018:IDCardOCR:v1.0.0'
	}
```

# Usage

印度Aadaar卡片识别：

```kotlin

IndiaOcrRecognizer.getInstance().getAadhaarInfo(imageBitmap, object :
    OnOCRResultListener {
    override fun onSuccess(ocrInfo: HashMap<String, String>?) {
        // 识别成功
    }

    override fun onFailure(e: Exception) {
        // 识别失败
    }
})

```

印度Pan卡片识别：

```kotlin

IndiaOcrRecognizer.getInstance().getPanInfo(imageBitmap, object :
    OnOCRResultListener {
    override fun onSuccess(ocrInfo: HashMap<String, String>?) {
        // 识别成功
    }

    override fun onFailure(e: Exception) {
        // 识别失败
    }
})

```

印度Aadaar和Pan卡同时识别：

```kotlin

IndiaOcrRecognizer.getInstance().getAadhaarAndPanInfo(aadhaarBitmap, panBitmap, object :
    OnOCRResultListener {
    override fun onSuccess(ocrInfo: HashMap<String, String>?) {
        // 识别成功
    }

    override fun onFailure(e: Exception) {
        // 识别失败
    }
})

```