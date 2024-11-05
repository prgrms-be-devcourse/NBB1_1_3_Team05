package com.grepp.somun.performance.dto.request

import com.grepp.somun.performance.entity.PerformanceEntity
import com.grepp.somun.performance.entity.PerformanceStatus
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import java.time.LocalDateTime


data class PerformanceRegisterRequest(
    val title: String,
    val dateStartTime: LocalDateTime,
    val dateEndTime: LocalDateTime,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String? = null,
    val price: Int,
    val description: String,
    val maxAudience: Int,
    val startDate: LocalDateTime? = dateStartTime,
    val categories: List<Long>
) {
    fun toEntity(): PerformanceEntity {
        val geometryFactory = GeometryFactory(PrecisionModel(), 4326)
        val coordinate: Point = geometryFactory.createPoint(
            Coordinate(longitude, latitude)
        )
        return PerformanceEntity(
            title = title,
            dateStartTime = dateStartTime,
            dateEndTime = dateEndTime,
            description = description,
            maxAudience = maxAudience,
            address = address,
            coordinate = coordinate,
            imageUrl = imageUrl,
            price = price,
            remainingTickets = maxAudience,
            startDate = startDate,
            performanceStatus = PerformanceStatus.NOT_CONFIRMED
        )
    }
}