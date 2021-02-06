package com.raywenderlich.android.creaturemon.addcreature

sealed class AddCreaturesActions {
    data class AvatarAction(val drawable: Int) : AddCreaturesActions()
    data class NameAction(val name: String) : AddCreaturesActions()
    data class IntelligenceAction(val intelligenceIndex: Int) : AddCreaturesActions()
    data class StrengthAction(val strengthIndex: Int) : AddCreaturesActions()
    data class EnduranceIntent(val enduranceIndex: Int) : AddCreaturesActions()
    data class SaveAction(
            val drawable: Int,
            val name: String,
            val intelligenceIndex: Int,
            val strengthIndex: Int,
            val enduranceIndex: Int
    ) : AddCreaturesActions ()
}