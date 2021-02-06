package com.raywenderlich.android.creaturemon.mviBase

import io.reactivex.Observable

interface MviViewModel<intent: MviIntent, state: MviViewState> {

    fun processIntent(intents: Observable<intent>)

    fun states(): Observable<state>
}