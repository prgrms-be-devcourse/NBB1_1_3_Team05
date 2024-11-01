package com.grepp.somun.performance.dto

import com.grepp.somun.performance.dto.domain.CategoryContent
import com.grepp.somun.performance.entity.CategoryEntity

data class CategoryDto(
    val categoryId: Long?,
    val nameKr: String?,
    val nameEn: String?
) {
    companion object {
        fun of(categoryId: Long?, nameKr: String?, nameEn: String?): CategoryDto {
            return CategoryDto(
                categoryId = categoryId,
                nameKr = nameKr,
                nameEn = nameEn
            )
        }

        fun toDto(categoryContent: CategoryContent): CategoryDto {
            return CategoryDto(
                categoryId = categoryContent.categoryId,
                nameKr = categoryContent.nameKr,
                nameEn = categoryContent.nameEn
            )
        }

        fun toDto(categoryEntity: CategoryEntity): CategoryDto {
            return CategoryDto(
                categoryId = categoryEntity.categoryId,
                nameKr = categoryEntity.nameKr,
                nameEn = categoryEntity.nameEn
            )
        }
    }
}