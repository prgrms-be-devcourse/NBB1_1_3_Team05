package com.grepp.somun.performance.repository.querydsl

import com.grepp.somun.member.entity.QMemberCategoryEntity
import com.grepp.somun.member.entity.QMemberEntity
import com.grepp.somun.performance.dto.domain.CategoryContent
import com.grepp.somun.performance.dto.domain.PerformanceDetail
import com.grepp.somun.performance.dto.domain.PerformanceWithCategory
import com.grepp.somun.performance.entity.PerformanceStatus
import com.grepp.somun.performance.entity.QCategoryEntity
import com.grepp.somun.performance.entity.QPerformanceCategoryEntity
import com.grepp.somun.performance.entity.QPerformanceEntity
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.Tuple
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.BooleanTemplate
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.locationtech.jts.geom.Point
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable


class PerformanceRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : PerformanceRepositoryCustom {


    private val qPerformanceEntity = QPerformanceEntity.performanceEntity
    private val qPerformanceCategoryEntity = QPerformanceCategoryEntity.performanceCategoryEntity
    private val qMember = QMemberEntity.memberEntity
    private val qCategoryEntity = QCategoryEntity.categoryEntity
    private val qMemberCategoryEntity = QMemberCategoryEntity.memberCategoryEntity

    private fun nullSafeBuilder(f: () -> BooleanExpression): BooleanBuilder {
        return try {
            BooleanBuilder(f())
        } catch (e: IllegalArgumentException) {
            BooleanBuilder()
        }
    }

    private fun performanceStatusEq(status: PerformanceStatus) = nullSafeBuilder {
        qPerformanceEntity.performanceStatus.eq(status)
    }

    private fun performanceIdEq(performanceId: Long) = nullSafeBuilder {
        qPerformanceEntity.performanceId.eq(performanceId)
    }

    private fun performanceCategoryEq(performanceId: Long) = nullSafeBuilder {
        qPerformanceCategoryEntity.performance.performanceId.eq(performanceId)
    }

    private fun memberEmailEq(memberEmail: String?) = if (memberEmail.isNullOrBlank()) {
        BooleanBuilder()
    } else {
        nullSafeBuilder { qMember.email.eq(memberEmail) }
    }

    private fun categoryIdEq(categoryId: Long?) = nullSafeBuilder {
        qPerformanceCategoryEntity.category.categoryId.eq(categoryId)
    }

    private fun performanceTitleLike(search: String?) = if (search.isNullOrBlank()) {
        BooleanBuilder()
    } else {
        BooleanBuilder(qPerformanceEntity.title.contains(search))
    }

    private fun categoryListWhereClause(categoryId: Long?, search: String?, email: String?): BooleanBuilder {
        val confirmedCondition = BooleanBuilder()
            .and(categoryIdEq(categoryId))
            .and(performanceTitleLike(search))
            .and(performanceStatusEq(PerformanceStatus.CONFIRMED))

        val notConfirmedCondition = BooleanBuilder()
            .and(categoryIdEq(categoryId))
            .and(performanceTitleLike(search))
            .and(memberEmailEq(email))
            .and(performanceStatusEq(PerformanceStatus.NOT_CONFIRMED))

        return BooleanBuilder().or(confirmedCondition).or(notConfirmedCondition)
    }

    private fun getContainsBooleanExpression(location: Point, radius: Int): BooleanTemplate {
        val geoFunction = "ST_CONTAINS(ST_BUFFER(ST_GeomFromText('%s', 4326), {0}), coordinate)"
        val expression = java.lang.String.format(geoFunction, location)

        return Expressions.booleanTemplate(expression, radius)
    }

    override fun getPerformanceWithCategoryList(
        pageable: Pageable,
        categoryId: Long?,
        search: String?,
        email: String?
    ): Page<PerformanceWithCategory> {
        val performances = jpaQueryFactory.selectDistinct(
            Projections.constructor(
                PerformanceWithCategory::class.java,
                qMember.name.`as`("memberName"),
                qPerformanceEntity.performanceId.`as`("performanceId"),
                qPerformanceEntity.title.`as`("title"),
                qPerformanceEntity.dateStartTime.`as`("dateStartTime"),
                qPerformanceEntity.dateEndTime.`as`("dateEndTime"),
                qPerformanceEntity.address.`as`("address"),
                qPerformanceEntity.imageUrl.`as`("imageUrl"),
                qPerformanceEntity.price.`as`("price"),
                qPerformanceEntity.performanceStatus.`as`("status"),
                qPerformanceEntity.remainingTickets.`as`("remainingTicket")
            )
        )
            .from(qPerformanceEntity)
            .leftJoin(qMember).on(qPerformanceEntity.member.eq(qMember))
            .leftJoin(qPerformanceCategoryEntity).on(qPerformanceCategoryEntity.performance.eq(qPerformanceEntity))
            .where(categoryListWhereClause(categoryId, search, email))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        if (performances.isEmpty()) {
            return PageImpl(emptyList(), pageable, 0)
        }

        val totalCount = jpaQueryFactory
            .select(qPerformanceEntity.count())
            .from(qPerformanceEntity)
            .leftJoin(qMember).on(qPerformanceEntity.member.eq(qMember))
            .leftJoin(qPerformanceCategoryEntity).on(qPerformanceCategoryEntity.performance.eq(qPerformanceEntity))
            .where(categoryListWhereClause(categoryId, search, email))
            .fetchOne() ?: 0L

        return PageImpl(addCategoriesToPerformances(performances), pageable, totalCount)
    }

    private fun addCategoriesToPerformances(performances: List<PerformanceWithCategory>): List<PerformanceWithCategory> {
        performances.forEach { performance ->
            val categories = getCategoriesByPerformance(performance.performanceId!!)
            performance.updateCategories(categories)
        }
        return performances
    }

    private fun getCategoriesByPerformance(performanceId: Long): List<CategoryContent> {
        return jpaQueryFactory.select(
            Projections.constructor(
                CategoryContent::class.java,
                qCategoryEntity.categoryId,
                qCategoryEntity.nameEn,
                qCategoryEntity.nameKr
            )
        )
            .from(qCategoryEntity)
            .join(qPerformanceCategoryEntity).on(qPerformanceCategoryEntity.category.eq(qCategoryEntity))
            .where(performanceCategoryEq(performanceId))
            .fetch()
    }

    override fun getRecommendedPerformancesByMember(memberId: Long): List<PerformanceWithCategory> {
        val results: List<Tuple> = jpaQueryFactory
            .select(
                qMember.name,
                qPerformanceEntity.performanceId,
                qPerformanceEntity.title,
                qPerformanceEntity.dateStartTime,
                qPerformanceEntity.dateEndTime,
                qPerformanceEntity.address,
                qPerformanceEntity.imageUrl,
                qPerformanceEntity.price,
                qPerformanceEntity.performanceStatus,
                qPerformanceEntity.remainingTickets,
                qCategoryEntity.categoryId,
                qCategoryEntity.nameKr,
                qCategoryEntity.nameEn
            )
            .from(qPerformanceCategoryEntity)
            .join(qPerformanceEntity).on(qPerformanceCategoryEntity.performance.eq(qPerformanceEntity))
            .join(qMember).on(qPerformanceEntity.member.eq(qMember))
            .join(qCategoryEntity).on(qPerformanceCategoryEntity.category.eq(qCategoryEntity))
            .join(qMemberCategoryEntity).on(qPerformanceCategoryEntity.category.eq(qMemberCategoryEntity.category))
            .where(
                qMemberCategoryEntity.member.memberId.eq(memberId)
                    .and(qPerformanceEntity.performanceStatus.eq(PerformanceStatus.CONFIRMED))
            )
            .groupBy(qPerformanceEntity.performanceId, qCategoryEntity.categoryId)
            .limit(10)
            .fetch()

        val performanceMap = mutableMapOf<Long, PerformanceWithCategory>()

        results.forEach { tuple ->
            val performanceId = tuple.get(qPerformanceEntity.performanceId)

            val performance = performanceMap.computeIfAbsent(performanceId!!) {
                PerformanceWithCategory(
                    tuple.get(qMember.name)!!,
                    performanceId,
                    tuple.get(qPerformanceEntity.title)!!,
                    tuple.get(qPerformanceEntity.dateStartTime)!!,
                    tuple.get(qPerformanceEntity.dateEndTime)!!,
                    tuple.get(qPerformanceEntity.address)!!,
                    tuple.get(qPerformanceEntity.imageUrl)!!,
                    tuple.get(qPerformanceEntity.price)!!,
                    tuple.get(qPerformanceEntity.performanceStatus)!!,
                    tuple.get(qPerformanceEntity.remainingTickets)
                )
            }

            val categories = performance.categories?.toMutableList() ?: mutableListOf()
            categories.add(
                CategoryContent(
                    tuple.get(qCategoryEntity.categoryId)!!,
                    tuple.get(qCategoryEntity.nameKr)!!,
                    tuple.get(qCategoryEntity.nameEn)!!
                )
            )
            performance.updateCategories(categories)
        }

        return performanceMap.values.sortedByDescending { it.categories!!.size }
    }



    override fun getPerformancesByIds(performanceIds: List<Long>): List<PerformanceWithCategory> {
        val performances = jpaQueryFactory
            .selectDistinct(
                Projections.constructor(
                    PerformanceWithCategory::class.java,
                    qMember.name.`as`("memberName"),
                    qPerformanceEntity.performanceId.`as`("performanceId"),
                    qPerformanceEntity.title.`as`("title"),
                    qPerformanceEntity.dateStartTime.`as`("dateStartTime"),
                    qPerformanceEntity.dateEndTime.`as`("dateEndTime"),
                    qPerformanceEntity.address.`as`("address"),
                    qPerformanceEntity.imageUrl.`as`("imageUrl"),
                    qPerformanceEntity.price.`as`("price"),
                    qPerformanceEntity.performanceStatus.`as`("status"),
                    qPerformanceEntity.remainingTickets.`as`("remainingTicket")
                )
            )
            .from(qPerformanceEntity)
            .leftJoin(qMember).on(qPerformanceEntity.member.eq(qMember))
            .leftJoin(qPerformanceCategoryEntity).on(qPerformanceCategoryEntity.performance.eq(qPerformanceEntity))
            .where(qPerformanceEntity.performanceId.`in`(performanceIds))
            .fetch()

        return addCategoriesToPerformances(performances)
    }


    override fun getPerformanceDetail(performanceId: Long): PerformanceDetail? {
        val performanceDetail = jpaQueryFactory.select(
            Projections.constructor(
                PerformanceDetail::class.java,
                qMember.name.`as`("memberName"),
                qPerformanceEntity.performanceId.`as`("performanceId"),
                qPerformanceEntity.title.`as`("title"),
                qPerformanceEntity.dateStartTime.`as`("dateStartTime"),
                qPerformanceEntity.dateEndTime.`as`("dateEndTime"),
                qPerformanceEntity.description.`as`("description"),
                qPerformanceEntity.maxAudience.`as`("maxAudience"),
                qPerformanceEntity.address.`as`("address"),
                qPerformanceEntity.imageUrl.`as`("imageUrl"),
                qPerformanceEntity.price.`as`("price"),
                qPerformanceEntity.remainingTickets.`as`("remainingTickets"),
                qPerformanceEntity.startDate.`as`("startDate"),
                qPerformanceEntity.performanceStatus.`as`("status"),
                qPerformanceEntity.createdAt.`as`("createdAt"),
                qPerformanceEntity.updatedAt.`as`("updatedAt")
            )
        )
            .from(qPerformanceEntity)
            .leftJoin(qMember).on(qPerformanceEntity.member.eq(qMember))
            .where(performanceIdEq(performanceId))
            .fetchOne()

        performanceDetail?.let {
            val categories = getCategoriesByPerformance(performanceDetail.performanceId!!)
            performanceDetail.updateCategories(categories)
        }

        return performanceDetail
    }




    override fun getMyPerformanceWithCategoryList(email: String, pageable: Pageable): Page<PerformanceWithCategory> {
        val performances = jpaQueryFactory.select(
            Projections.constructor(
                PerformanceWithCategory::class.java,
                qMember.name.`as`("memberName"),
                qPerformanceEntity.performanceId.`as`("performanceId"),
                qPerformanceEntity.title.`as`("title"),
                qPerformanceEntity.dateStartTime.`as`("dateStartTime"),
                qPerformanceEntity.dateEndTime.`as`("dateEndTime"),
                qPerformanceEntity.address.`as`("address"),
                qPerformanceEntity.imageUrl.`as`("imageUrl"),
                qPerformanceEntity.price.`as`("price"),
                qPerformanceEntity.performanceStatus.`as`("status"),
                qPerformanceEntity.remainingTickets.`as`("remainingTicket")
            )
        )
            .from(qPerformanceEntity)
            .leftJoin(qMember).on(qPerformanceEntity.member.eq(qMember))
            .where(memberEmailEq(email))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        if (performances.isEmpty()) {
            return PageImpl(emptyList(), pageable, 0)
        }

        val totalCount = jpaQueryFactory
            .select(qPerformanceEntity.count())
            .from(qPerformanceEntity)
            .leftJoin(qMember).on(qPerformanceEntity.member.eq(qMember))
            .where(memberEmailEq(email))
            .fetchOne() ?: 0L

        return PageImpl(addCategoriesToPerformances(performances), pageable, totalCount)
    }

    /**
     * 특정 좌표에서 가까운 순서대로 공연을 조회한다.
     * @Author Icecoff22
     * @param location : geo 좌표
     * @param radius : 좌표 중심 원(~km까지)
     * @param pageable : 페이징
     * @return
     */
    override fun getPerformanceAroundPoint(location: Point, radius: Int, pageable: Pageable): Page<PerformanceWithCategory> {
        // 사용자 선호 카테고리와 매칭된 공연 조회
        val performances = jpaQueryFactory.select(
            Projections.constructor(
                PerformanceWithCategory::class.java,
                qMember.name.`as`("memberName"),
                qPerformanceEntity.performanceId.`as`("performanceId"),
                qPerformanceEntity.title.`as`("title"),
                qPerformanceEntity.dateStartTime.`as`("dateStartTime"),
                qPerformanceEntity.dateEndTime.`as`("dateEndTime"),
                qPerformanceEntity.address.`as`("address"),
                qPerformanceEntity.imageUrl.`as`("imageUrl"),
                qPerformanceEntity.price.`as`("price"),
                qPerformanceEntity.performanceStatus.`as`("status"),
                qPerformanceEntity.remainingTickets.`as`("remainingTicket")
            )
        )
            .from(qPerformanceEntity)
            .leftJoin(qMember).on(qPerformanceEntity.member.eq(qMember))
            .leftJoin(qPerformanceCategoryEntity).on(qPerformanceCategoryEntity.performance.eq(qPerformanceEntity))
            .where(
                getContainsBooleanExpression(
                    location!!,
                    radius!!
                )
            ) //.orderBy(getOrderSpecifiersByDistance(location))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        if (performances.isEmpty()) {
            return PageImpl(addCategoriesToPerformances(performances), pageable, 0)
        }

        var totalCount = jpaQueryFactory
            .select(qPerformanceEntity.count())
            .from(qPerformanceEntity)
            .leftJoin(qMember).on(qPerformanceEntity.member.eq(qMember))
            .leftJoin(qPerformanceCategoryEntity).on(qPerformanceCategoryEntity.performance.eq(qPerformanceEntity))
            .where(getContainsBooleanExpression(location!!, radius!!))
            .fetchOne()

        if (totalCount == null) {
            totalCount = 0L
        }

        return PageImpl(addCategoriesToPerformances(performances), pageable, totalCount)
    }



}
