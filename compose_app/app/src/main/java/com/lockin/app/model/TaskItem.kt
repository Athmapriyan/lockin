package com.lockin.app.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dateMillis: Long,
    val completed: Boolean = false,
    val isPrivate: Boolean = false
)
