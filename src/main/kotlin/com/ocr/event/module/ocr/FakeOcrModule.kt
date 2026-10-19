package com.ocr.event.module.ocr

import org.springframework.stereotype.Component

@Component
class FakeOcrModule : OcrModule {

    override fun ocr(data: String): String {
        return "${data}-ocr-result"
    }

}