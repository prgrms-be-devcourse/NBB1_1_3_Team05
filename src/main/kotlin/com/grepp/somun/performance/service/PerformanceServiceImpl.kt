package com.grepp.somun.performance.service

import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.member.repository.MemberRepository
import com.grepp.somun.performance.dto.CategoryDto
import com.grepp.somun.performance.dto.domain.PerformanceWithCategory
import com.grepp.somun.performance.dto.request.PerformanceRegisterRequest
import com.grepp.somun.performance.dto.request.PerformanceUpdateRequest
import com.grepp.somun.performance.dto.response.PerformanceDetailResponse
import com.grepp.somun.performance.dto.response.PerformanceListResponse
import com.grepp.somun.performance.dto.response.PerformanceRegisterResponse
import com.grepp.somun.performance.dto.response.PerformanceUpdateResponse
import com.grepp.somun.performance.entity.CategoryEntity
import com.grepp.somun.performance.entity.PerformanceCategoryEntity
import com.grepp.somun.performance.entity.PerformanceEntity
import com.grepp.somun.performance.repository.CategoryRepository
import com.grepp.somun.performance.repository.PerformanceCategoryRepository
import com.grepp.somun.performance.repository.PerformanceRepository
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*


@Service
class PerformanceServiceImpl(
    private val performanceRepository: PerformanceRepository,
    private val performanceCategoryRepository: PerformanceCategoryRepository,
    private val categoryRepository: CategoryRepository,
    private val memberRepository: MemberRepository,
    private val imageUploadService: ImageUploadService
) : PerformanceService {

    @Transactional
    override fun registerPerformance(
        email: String,
        performanceRegisterRequest: PerformanceRegisterRequest,
        imageFile: MultipartFile?
    ): PerformanceRegisterResponse {
        val memberEntity = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }
        val performanceEntity = performanceRegisterRequest.toEntity()
        performanceEntity.updateMember(memberEntity)
        val imageUrl = imageUploadService.uploadFileToFTP(imageFile)
        performanceEntity.updateImageUrl(imageUrl)
        performanceRepository.save(performanceEntity)

        val categoryEntities = performanceCategorySave(performanceEntity, performanceRegisterRequest.categories)
        val categoryDtos = categoryEntities.map { CategoryDto.toDto(it) }

        return PerformanceRegisterResponse.of(performanceEntity, categoryDtos)
    }

    override fun getPerformanceList(
        page: Int,
        size: Int,
        categoryId: Long?,
        search: String?,
        email: String?
    ): PerformanceListResponse {
        val pageRequest = PageRequest.of(page, size)
        val performanceList = performanceRepository.getPerformanceWithCategoryList(pageRequest, categoryId, search, email)

        return PerformanceListResponse.from(performanceList.totalElements, performanceList.content)
    }

    override fun getPerformanceDetail(email: String, performanceId: Long): PerformanceDetailResponse {
        val performanceDetail = performanceRepository.getPerformanceDetail(performanceId)

        return if (isAccessPerformance(email, performanceId)) {
            performanceDetail?.let { PerformanceDetailResponse.from(true, it) }
                ?: throw GeneralException(ErrorStatus.PERFORMANCE_NOT_FOUND)
        } else {
            performanceDetail?.let { PerformanceDetailResponse.from(false, it) }
                ?: throw GeneralException(ErrorStatus.PERFORMANCE_NOT_FOUND)
        }
    }

    @Transactional
    override fun updatePerformance(
        email: String,
        performanceId: Long,
        performanceUpdateRequest: PerformanceUpdateRequest
    ): PerformanceUpdateResponse {
        if (!isAccessPerformance(email, performanceId)) {
            throw GeneralException(ErrorStatus.PERFORMANCE_NOT_ACCESSIBLE)
        }

        val performanceEntity = performanceRepository.findById(performanceId)
            .orElseThrow { GeneralException(ErrorStatus.PERFORMANCE_NOT_FOUND) }

        performanceEntity.updatePerformance(performanceUpdateRequest.toEntity())

        return PerformanceUpdateResponse.from(performanceEntity.performanceId!!)
    }

    @Transactional
    override fun deletePerformance(email: String, performanceId: Long) {
        if (!isAccessPerformance(email, performanceId)) {
            throw GeneralException(ErrorStatus.PERFORMANCE_NOT_ACCESSIBLE)
        }

        val performanceEntity = performanceRepository.findById(performanceId)
            .orElseThrow { GeneralException(ErrorStatus.PERFORMANCE_NOT_FOUND) }

        performanceEntity.updateDeleteAt()
    }

    private fun performanceCategorySave(performanceEntity: PerformanceEntity, categories: List<Long>): List<CategoryEntity> {
        val categoryEntities = categoryRepository.findAllById(categories)

        if (categoryEntities.isEmpty()) {
            return emptyList()
        }

        categoryEntities.forEach {
            performanceCategoryRepository.save(PerformanceCategoryEntity.of(performanceEntity, it))
        }

        return categoryEntities
    }

    override fun getMyPerformanceList(email: String, page: Int, size: Int): PerformanceListResponse {
        val pageRequest = PageRequest.of(page, size)
        val performanceList = performanceRepository.getMyPerformanceWithCategoryList(email, pageRequest)

        if (performanceList.isEmpty) {
            throw GeneralException(ErrorStatus.PERFORMANCE_NOT_FOUND)
        }

        return PerformanceListResponse.from(performanceList.totalElements, performanceList.content)
    }

    override fun getCategoryList(): List<CategoryDto> {
        val categoryEntities = categoryRepository.findAll()
        return categoryEntities.map { CategoryDto.toDto(it) }
    }

    override fun getPerformanceListByUserCategories(email: String): PerformanceListResponse {
        val member = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }

        val recommendedPerformancesByMember = performanceRepository
            .getRecommendedPerformancesByMember(member.memberId!!)

        return PerformanceListResponse.from(recommendedPerformancesByMember.size.toLong(), recommendedPerformancesByMember)
    }

    override fun getPopularPerformances(performanceIds: List<Long>): PerformanceListResponse {
        val performancesByIds = performanceRepository.getPerformancesByIds(performanceIds)

        val performanceMap = performancesByIds.associateBy { it.performanceId }

        val sortedPerformances = performanceIds.mapNotNull { performanceMap[it] }

        return PerformanceListResponse.from(sortedPerformances.size.toLong(), sortedPerformances)
    }

    override fun getAroundPoint(latitude: Double, longitude: Double, page: Int, size: Int): PerformanceListResponse? {
        val radius = 5000 // 반경 5km 이내 공연 추천

        val geometryFactory = GeometryFactory(PrecisionModel(), 4326)
        val location: Point = geometryFactory.createPoint(Coordinate(latitude, longitude))

        val pageable: Pageable = PageRequest.of(page, size)

        val performanceList: Page<PerformanceWithCategory> =
            performanceRepository.getPerformanceAroundPoint(location, radius, pageable)

        return PerformanceListResponse.from(performanceList.getTotalElements(), performanceList.getContent())
    }

    private fun isAccessPerformance(email: String?, performanceId: Long): Boolean {
        if (email == null) {
            return false
        }

        val memberEntity = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }

        val performanceEntity = performanceRepository.findById(performanceId)
            .orElseThrow { GeneralException(ErrorStatus.PERFORMANCE_NOT_FOUND) }

        return memberEntity.email == performanceEntity.member?.email
    }
}