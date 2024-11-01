package com.grepp.somun.performance.repository

import com.grepp.somun.performance.entity.PerformanceEntity
import com.grepp.somun.performance.repository.querydsl.PerformanceRepositoryCustom
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository

interface PerformanceRepository : JpaRepository<PerformanceEntity, Long>, PerformanceRepositoryCustom {
    @org.springframework.data.jpa.repository.Lock(LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT p FROM  performance p WHERE p.performanceId= :performanceId")
    fun findByIdWithLock(@org.springframework.data.repository.query.Param("performanceId") performanceId: Long?): java.util.Optional<PerformanceEntity?>?
}
