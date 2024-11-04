package com.grepp.somun.performance.repository

import com.grepp.somun.performance.entity.PerformanceCategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PerformanceCategoryRepository : JpaRepository<PerformanceCategoryEntity, Long>
