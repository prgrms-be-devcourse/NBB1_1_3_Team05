package com.grepp.somun.performance.repository

import com.grepp.somun.performance.entity.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository


interface CategoryRepository : JpaRepository<CategoryEntity, Long> {
    fun findAllByNameKrIn(nameKrList: List<String>): List<CategoryEntity>

    fun findByCategoryId(id: Long): java.util.Optional<CategoryEntity>
}
