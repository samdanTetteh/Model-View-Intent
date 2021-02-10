package com.raywenderlich.android.creaturemon.mviBase

import io.reactivex.Observable

interface MviViewModel<intent: MviIntent, state: MviViewState> {

    fun processIntents(intents: Observable<intent>)

    fun states(): Observable<state>
}