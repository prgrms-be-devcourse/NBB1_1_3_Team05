package com.grepp.somun.performance.entity

import com.grepp.somun.member.entity.MemberCategoryEntity
import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor

@Entity(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class CategoryEntity // 테스트에서 사용
@Builder constructor(
    @field:Column(name = "category_id") @field:GeneratedValue(strategy = GenerationType.IDENTITY) @field:Id private var categoryId: Long,
    @field:Column(
        name = "name_kr",
        nullable = false
    ) private var nameKr: String,
    @field:Column(
        name = "name_en",
        nullable = false
    ) private var nameEn: String
) {
    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val performanceCategoryList: List<PerformanceCategoryEntity> = ArrayList<PerformanceCategoryEntity>()

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val memberCategories: List<MemberCategoryEntity> = ArrayList<MemberCategoryEntity>()
}
