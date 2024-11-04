package com.grepp.somun.config


import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.util.StringUtils
import redis.embedded.RedisServer
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

/**
 * @author yechanKim
 */
@Slf4j
@Profile("local")
@Configuration
class EmbeddedRedisConfig {
    @Value("\${spring.data.redis.port}")
    private val redisPort = 0

    // 윈도우를 위한 maxmemory 설정
    @Value("\${spring.data.redis.maxmemory}")
    private val redisMaxMemory: String? = null

    private var redisServer: RedisServer? = null


    @PostConstruct
    @Throws(IOException::class)
    fun startRedis() {
        val port = if (isRedisRunning) findAvailablePort() else redisPort
        if (isArmArchitecture) {
//            log.info("ARM Architecture")
            redisServer = RedisServer(Objects.requireNonNull(redisServerExecutable), port)
        } else {
            redisServer = RedisServer.builder()
                .port(port)
                .setting("maxmemory $redisMaxMemory")
                .build()
        }
        redisServer!!.start()
    }


    @PreDestroy
    fun stopRedis() {
        redisServer!!.stop()
    }

    @Throws(IOException::class)
    private fun findAvailablePort(): Int {
        for (port in 10000..65534) {
            val process = executeGrepProcessCommand(port)
            if (!isRunning(process)) {
                return port
            }
        }
        throw GeneralException(ErrorStatus.AVAILABLE_PORT_NOT_FOUND)
    }

    @get:Throws(IOException::class)
    private val isRedisRunning: Boolean
        get() = isRunning(executeGrepProcessCommand(redisPort))


    @Throws(IOException::class)
    private fun executeGrepProcessCommand(port: Int): Process {
        val os = System.getProperty("os.name").lowercase(Locale.getDefault())

        val command: String
        if (os.contains("win")) {
            // 윈도우인 경우
            command = String.format("netstat -nao | find \"LISTENING\" | find \":%d\"", port)
            val cmd = arrayOf("cmd.exe", "/y", "/c", command)
            return Runtime.getRuntime().exec(cmd)
        }
        // Unix 계열인 경우 (맥OS, 리눅스)
        command = String.format("netstat -nat | grep LISTEN | grep %d", port)
        val shell = arrayOf("/bin/sh", "-c", command)
        return Runtime.getRuntime().exec(shell)
    }


    private fun isRunning(process: Process): Boolean {
        var line: String?
        val pidInfo = StringBuilder()

        try {
            BufferedReader(InputStreamReader(process.inputStream)).use { input ->
                while ((input.readLine().also { line = it }) != null) {
                    pidInfo.append(line)
                }
            }
        } catch (e: Exception) {
            throw GeneralException(ErrorStatus.ERROR_EXECUTING_EMBEDDED_REDIS)
        }
        return StringUtils.hasText(pidInfo.toString())
    }

    private val redisServerExecutable: File
        // mac os용 redis 바이너리 파일
        get() {
            try {
                return File("src/main/resources/redis/redis-server-7.2.6-mac-arm64")
            } catch (e: Exception) {
                throw GeneralException(ErrorStatus.REDIS_SERVER_EXECUTABLE_NOT_FOUND)
            }
        }

    private val isArmArchitecture: Boolean
        // mac os 인지 확인
        get() = System.getProperty("os.arch").contains("aarch64")
}
