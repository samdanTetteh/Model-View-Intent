package com.raywenderlich.android.creaturemon.allcreatures

import androidx.lifecycle.ViewModel
import com.raywenderlich.android.creaturemon.mviBase.MviViewModel
import io.reactivex.Observable
import java.util.function.BiFunction

class AllCreaturesViewModel (
        private val actionProcessorHolder: AllCreaturesProcessorHolder
): ViewModel(), MviViewModel<AllCreaturesIntents, AllCreaturesViewState>{


    override fun processIntent(intents: Observable<AllCreaturesIntents>) {
        TODO("Not yet implemented")
    }

    override fun states(): Observable<AllCreaturesViewState> {
        TODO("Not yet implemented")
    }

    companion object {
        private val reducer = BiFunction { previouseSate: AllCreaturesViewState, result: AllCreaturesResults ->
            when (result) {
                is AllCreaturesResults.LoadAllCreaturesResult -> when (result) {
                    is AllCreaturesResults.LoadAllCreaturesResult.Success -> { previouseSate.copy(isLoading = false,  creatures = result.creatures) }
                    is AllCreaturesResults.LoadAllCreaturesResult.Failure -> { previouseSate.copy(isLoading = false, error = result.error) }
                    is AllCreaturesResults.LoadAllCreaturesResult.Loading -> { previouseSate.copy(isLoading = true) }
                }

                is AllCreaturesResults.ClearAllCreaturesResult -> when (result) {
                    is AllCreaturesResults.ClearAllCreaturesResult.Clearing -> { previouseSate.copy(isLoading = true) }
                    is AllCreaturesResults.ClearAllCreaturesResult.Success -> { previouseSate.copy(isLoading = false, creatures = emptyList()) }
                    is AllCreaturesResults.ClearAllCreaturesResult.Failure -> { previouseSate.copy(isLoading = false, error = result.error) }
                }
            }
        }
    }
}