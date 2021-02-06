package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.data.model.Creature
import com.raywenderlich.android.creaturemon.mviBase.MviResult

sealed class AllCreaturesResults : MviResult{
    sealed class LoadAllCreaturesResult: AllCreaturesResults() {
        object Loading : LoadAllCreaturesResult()
        data class Success(val creatures: List<Creature>) : LoadAllCreaturesResult()
        data class Failure(val error: Throwable) : LoadAllCreaturesResult()
    }

    sealed class ClearAllCreaturesResult: AllCreaturesResults() {
        object Clearing: ClearAllCreaturesResult()
        object Success: ClearAllCreaturesResult()
        data class Failure(val error : Throwable) : ClearAllCreaturesResult()
    }
}