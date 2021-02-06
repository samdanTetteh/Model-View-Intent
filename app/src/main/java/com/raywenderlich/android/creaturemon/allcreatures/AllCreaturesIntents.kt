package com.raywenderlich.android.creaturemon.allcreatures

sealed class AllCreaturesIntents {
    object LoadAllCreaturesIntent: AllCreaturesIntents()
    object ClearAllCreaturesIntent: AllCreaturesIntents()

    /*TODO: add intent to start add activity*/
}