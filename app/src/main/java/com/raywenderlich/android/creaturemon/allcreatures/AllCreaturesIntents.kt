package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.mviBase.MviIntent

sealed class AllCreaturesIntents : MviIntent {
    object LoadAllCreaturesIntent: AllCreaturesIntents()
    object ClearAllCreaturesIntent: AllCreaturesIntents()

    /*TODO: add intent to start add activity*/
}