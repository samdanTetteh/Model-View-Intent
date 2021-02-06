package com.raywenderlich.android.creaturemon.addcreature

sealed class AddCreatureIntents {
    data class AvatarIntent(val drawable: Int) : AddCreatureIntents()
    data class NameIntent(val name: String) : AddCreatureIntents()
    data class IntelligenceIntent(val intelligenceIndex: Int) : AddCreatureIntents()
    data class StrengthIntent(val strengthIndex : Int) : AddCreatureIntents()
    data class EnduranceIntent(val enduranceIndex: Int) : AddCreatureIntents()
    data class SaveIntent(
            val drawable: Int,
            val name: String,
            val intelligenceIndex: Int,
            val strengthIndex: Int,
            val enduranceIndex: Int
    ) : AddCreatureIntents()
}