package com.grepp.somun.performance.repository


import com.grepp.somun.performance.entity.PerformanceEntity
import com.grepp.somun.performance.repository.querydsl.PerformanceRepositoryCustom
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface PerformanceRepository : JpaRepository<PerformanceEntity, Long>, PerformanceRepositoryCustom {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM performance p WHERE p.performanceId = :performanceId")
    fun findByIdWithLock(@Param("performanceId") performanceId: Long): Optional<PerformanceEntity>
}
