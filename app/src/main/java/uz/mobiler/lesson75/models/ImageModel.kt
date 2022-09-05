package uz.mobiler.lesson75.models

import uz.mobiler.lesson75.database.entity.HitEntity

data class ImageModel(
    val hits: List<HitEntity>,
    val total: Int,
    val totalHits: Int
)