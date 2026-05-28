package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "psp_games")
data class PspGameEntity(
    @PrimaryKey val id: String,
    val title: String,
    val genre: String,
    val targetFps: Int,
    val emulationDifficulty: String, // "Fácil", "Médio", "Difícil"
    val rating: Float,
    val isOwned: Boolean = false,
    val customNotes: String = "",
    val preferredBackend: String = "Vulkan",
    val renderingResolutionMultiplier: Int = 2
)
