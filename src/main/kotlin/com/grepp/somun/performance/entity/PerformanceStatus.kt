package com.grepp.somun.performance.entity

import lombok.Getter

@Getter
enum class PerformanceStatus {
    NOT_CONFIRMED,
    CONFIRMED,
    CANCELED;

    private val status: String? = null
}
