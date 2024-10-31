package com.grepp.somun.performance.entity

import com.grepp.somun.member.entity.MemberCategoryEntity
import jakarta.persistence.*

@Entity(name = "category")
class CategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    val categoryId: Long? = null,

    @Column(name = "name_kr", nullable = false)
    val nameKr: String,

    @Column(name = "name_en", nullable = false)
    val nameEn: String
) {
    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], orphanRemoval = true)
    val performanceCategoryList: MutableList<PerformanceCategoryEntity> = mutableListOf()

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], orphanRemoval = true)
    val memberCategories: MutableList<MemberCategoryEntity> = mutableListOf()

}
