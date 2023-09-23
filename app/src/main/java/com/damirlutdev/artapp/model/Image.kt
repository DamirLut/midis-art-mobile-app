package com.damirlutdev.artapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val id: String,
    val views: Int,
    val favorites: Int,
    val dimension_x: Int,
    val dimension_y: Int,
    val ratio: Float,
    val path: String,
    val thumbs: ImageThumb
)

@Serializable
data class SearchResponse(
    val data: List<Image>
);

@Serializable
data class ImageThumb(
    val large: String,
    val original: String,
    val small: String
);