package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.mviBase.MviActions

sealed class AllCreaturesActions : MviActions {
    object LoadAllCreatures : AllCreaturesActions()
    object ClearAllCreatures : AllCreaturesActions()
}