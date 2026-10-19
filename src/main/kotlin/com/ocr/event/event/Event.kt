package com.ocr.event.event

interface Event {
}

interface EventHandler<T : Event> {
    val eventType: Class<T>
    fun handle(event: T)
}