package com.raywenderlich.android.creaturemon.allcreatures

import androidx.lifecycle.ViewModel
import com.raywenderlich.android.creaturemon.mviBase.MviViewModel
import com.raywenderlich.android.creaturemon.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject


class AllCreaturesViewModel (
        private val actionProcessorHolder: AllCreaturesProcessorHolder
): ViewModel(), MviViewModel<AllCreaturesIntents, AllCreaturesViewState>{

    private val intentsSubject: PublishSubject<AllCreaturesIntents> = PublishSubject.create()
    private val stateObservable: Observable<AllCreaturesViewState> = compose()

    private val intentFilter: ObservableTransformer<AllCreaturesIntents, AllCreaturesIntents>
    get() = ObservableTransformer { intents ->
        intents.publish { shared ->
            Observable.merge(
                    shared.ofType(AllCreaturesIntents.LoadAllCreaturesIntent::class.java).take(1),
                    shared.notOfType(AllCreaturesIntents.LoadAllCreaturesIntent::class.java)
            )
        }
    }


    override fun processIntent(intents: Observable<AllCreaturesIntents>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<AllCreaturesViewState> = stateObservable

    private fun compose(): Observable<AllCreaturesViewState> {
        return intentsSubject
                .compose(intentFilter)
                .map(this::actonFromIntent)
                .compose(actionProcessorHolder.actionProcessor)
                .scan(AllCreaturesViewState.idle(), reducer)
                .distinctUntilChanged()
                .replay(1)
                .autoConnect(0)
    }

    private fun actonFromIntent(intents: AllCreaturesIntents) : AllCreaturesActions {
        return when (intents) {
            is AllCreaturesIntents.LoadAllCreaturesIntent -> AllCreaturesActions.LoadAllCreaturesAction
            is AllCreaturesIntents.ClearAllCreaturesIntent -> AllCreaturesActions.ClearAllCreaturesAction
        }
    }

    companion object {
        private val reducer = BiFunction { previousSate: AllCreaturesViewState, result: AllCreaturesResults ->
            when (result) {
                is AllCreaturesResults.LoadAllCreaturesResult -> when (result) {
                    is AllCreaturesResults.LoadAllCreaturesResult.Success -> { previousSate.copy(isLoading = false,  creatures = result.creatures) }
                    is AllCreaturesResults.LoadAllCreaturesResult.Failure -> { previousSate.copy(isLoading = false, error = result.error) }
                    is AllCreaturesResults.LoadAllCreaturesResult.Loading -> { previousSate.copy(isLoading = true) }
                }

                is AllCreaturesResults.ClearAllCreaturesResult -> when (result) {
                    is AllCreaturesResults.ClearAllCreaturesResult.Clearing -> { previousSate.copy(isLoading = true) }
                    is AllCreaturesResults.ClearAllCreaturesResult.Success -> { previousSate.copy(isLoading = false, creatures = emptyList()) }
                    is AllCreaturesResults.ClearAllCreaturesResult.Failure -> { previousSate.copy(isLoading = false, error = result.error) }
                }
            }
        }
    }
}