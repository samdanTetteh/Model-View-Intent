package com.raywenderlich.android.creaturemon.addcreature

import com.raywenderlich.android.creaturemon.mviBase.MviResult

sealed class AddCreatureResults : MviResult {
    sealed class AvatarResult : AddCreatureResults() {
        object Processing : AvatarResult()
        data class Success(val drawable: Int) : AvatarResult()
        data class Failure(val error: Throwable) : AvatarResult()
    }

    sealed class NameResult : AddCreatureResults() {
        object Processing : NameResult()
        data class Success(val name: String) : NameResult ()
        data class Failure(val error: Throwable) : NameResult()
    }

    sealed class IntelligenceResult : AddCreatureResults() {
        object Processing : IntelligenceResult()
        data class Success(val intelligence : Int) : IntelligenceResult()
        data class Failure(val error : Throwable) : IntelligenceResult()
    }

    sealed class StrengthResult : AddCreatureResults() {
        object Processing : StrengthResult()
        data class Success(val strength : Int) : StrengthResult()
        data class Failure(val error : Throwable) : StrengthResult()
    }

    sealed class EnduranceResult : AddCreatureResults() {
        object Processing : EnduranceResult()
        data class Success(val endurance : Int) : EnduranceResult()
        data class Failure(val error : Throwable) : EnduranceResult()
    }

    sealed class SaveCreatureResult : AddCreatureResults() {
        object Processing: SaveCreatureResult()
        object Success : SaveCreatureResult()
        data class Failure(val error: Throwable) : SaveCreatureResult()
    }


}
