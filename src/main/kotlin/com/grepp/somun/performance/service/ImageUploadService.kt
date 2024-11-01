package com.grepp.somun.performance.service

import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * FTP 서버에 공연 이미지 등록 서비스
 */
@Service
class ImageUploadService(
    @Value("\${ftp.host}") private val ftpHost: String,
    @Value("\${ftp.username}") private val ftpUsername: String,
    @Value("\${ftp.password}") private val ftpPassword: String,
    @Value("\${ftp.poster-path}") private val ftpPosterPath: String,
    @Value("\${nginx.server.base.url}") private val nginxBaseUrl: String
) {

    private val log = LoggerFactory.getLogger(ImageUploadService::class.java)

    fun uploadFileToFTP(imageFile: MultipartFile?): String? {
        if (imageFile == null) return null

        var ftpClient: FTPClient? = null
        return try {
            ftpClient = connectToFtp()
            val uuidFileName = generateUuidFileName(imageFile.originalFilename)
            val imageUrl = buildImageUrl(uuidFileName)

            log.debug("Starting FTP file upload. File: {}, UUID File Name: {}", imageFile.originalFilename, uuidFileName)

            uploadToFtp(ftpClient, imageFile, uuidFileName)

            log.info("File upload to FTP server completed successfully. Image URL: {}", imageUrl)
            nginxBaseUrl + imageUrl
        } catch (e: IOException) {
            log.error("Error during FTP file upload. File: {}, Error: {}", imageFile.originalFilename, e.message)
            throw GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR)
        } finally {
            closeFtpClient(ftpClient)
        }
    }

    private fun connectToFtp(): FTPClient {
        return FTPClient().apply {
            connect(ftpHost)
            login(ftpUsername, ftpPassword)
            enterLocalPassiveMode()
            setFileType(FTP.BINARY_FILE_TYPE)
        }
    }

    private fun closeFtpClient(ftpClient: FTPClient?) {
        if (ftpClient != null && ftpClient.isConnected) {
            try {
                ftpClient.logout()
                ftpClient.disconnect()
            } catch (ex: IOException) {
                log.error("Error while disconnecting FTP client: {}", ex.message, ex)
                throw GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR)
            }
        }
    }

    private fun uploadToFtp(ftpClient: FTPClient, file: MultipartFile, fileName: String) {
        val remotePath = "$ftpPosterPath$fileName"
        file.inputStream.use { inputStream ->
            if (!ftpClient.storeFile(remotePath, inputStream)) {
                val reply = ftpClient.replyString
                throw RuntimeException("FTP 서버에 파일 업로드 실패: $fileName. 응답: $reply")
            }
        }
    }

    private fun generateUuidFileName(originalFileName: String?): String {
        val extension = getFileExtension(originalFileName)
        val uuid = UUID.randomUUID().toString()
        return "$uuid$extension"
    }

    private fun buildImageUrl(fileName: String): String {
        return "$ftpPosterPath$fileName"
    }

    private fun getFileExtension(fileName: String?): String {
        val dotIndex = fileName?.lastIndexOf('.') ?: -1
        if (dotIndex == -1) throw GeneralException(ErrorStatus.INVALID_IMAGE_FORMAT)

        val extension = fileName?.substring(dotIndex + 1)?.lowercase(Locale.getDefault())
        val allowedExtensions = listOf("jpg", "jpeg", "png", "gif", "bmp")

        if (extension !in allowedExtensions) throw GeneralException(ErrorStatus.INVALID_IMAGE_FORMAT)
        return ".$extension"
    }
}