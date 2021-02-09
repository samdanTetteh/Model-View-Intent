package com.raywenderlich.android.creaturemon.mviBase

import io.reactivex.Observable

interface MviView<intent: MviIntent, in state: MviViewState> {

    fun intents(): Observable<intent>

    fun render(state: state)
}