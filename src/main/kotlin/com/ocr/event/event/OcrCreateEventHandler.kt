package com.ocr.event.event

import com.ocr.event.module.ocr.OcrModule
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OcrCreateEventHandler(
    private val ocrModule: OcrModule,
) : EventHandler<OcrCreateEvent> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override val eventType = OcrCreateEvent::class.java

    override fun handle(event: OcrCreateEvent) {
        val result = ocrModule.ocr(event.path)
        log.info("OCR 완료 path: ${event.path}, result: $result")
    }

}
