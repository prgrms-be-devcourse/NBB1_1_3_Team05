package com.grepp.somun.member.dto.response

import com.grepp.somun.performance.entity.CategoryEntity


@JvmRecord
data class CategoryResponse(
    val categoryId: Long,
    val nameKr: String,
    val nameEn: String
) {
    companion object {
        fun fromEntity(categoryEntity: CategoryEntity): CategoryResponse {
            return CategoryResponse(
                categoryEntity.categoryId!!,
                categoryEntity.nameKr,
                categoryEntity.nameEn
            )
        }
    }
}
