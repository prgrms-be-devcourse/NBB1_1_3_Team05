package com.grepp.somun.performance.dto.domain

import lombok.Builder

@Builder
@JvmRecord
data class CategoryContent(val categoryId: Long, val nameKr: String, val nameEn: String) {
    companion object {
        fun of(categoryId: Long, nameKr: String, nameEn: String): CategoryContent {
            return CategoryContent(
                categoryId = categoryId,
                nameKr = nameKr,
                nameEn = nameEn
            )
        }
    }
}
