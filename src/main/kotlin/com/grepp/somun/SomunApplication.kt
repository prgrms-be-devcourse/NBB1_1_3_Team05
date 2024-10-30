package com.grepp.somun

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
class SomunApplication

fun main(args: Array<String>) {
    runApplication<SomunApplication>(*args)
}
