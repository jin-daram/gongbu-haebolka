package com.ocr.event.module.storage

import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.InputStream

// 운영 환경에서 업로드를 시험할 때 쓰는 메모리 기반 MultipartFile
class InMemoryMultipartFile(
    private val fieldName: String,
    private val fileName: String?,
    private val type: String?,
    private val content: ByteArray,
) : MultipartFile {
    override fun getName() = fieldName
    override fun getOriginalFilename() = fileName
    override fun getContentType() = type
    override fun isEmpty() = content.isEmpty()
    override fun getSize() = content.size.toLong()
    override fun getBytes() = content
    override fun getInputStream(): InputStream = content.inputStream()
    override fun transferTo(dest: File) = dest.writeBytes(content)
}
