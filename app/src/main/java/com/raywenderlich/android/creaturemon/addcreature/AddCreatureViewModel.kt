package com.raywenderlich.android.creaturemon.addcreature;

import androidx.lifecycle.ViewModel
import com.raywenderlich.android.creaturemon.data.model.CreatureAttributes
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.mviBase.MviViewModel
import io.reactivex.Observable
import java.util.function.BiFunction

class AddCreatureViewModel(
        private val addActionProcessorHolder: AddCreatureProcessorHolder
) : ViewModel(), MviViewModel<AddCreatureIntents, AddCreatureViewState> {


    override fun processIntent(intents: Observable<AddCreatureIntents>) {
        TODO("Not yet implemented")
    }

    override fun states(): Observable<AddCreatureViewState> {
        TODO("Not yet implemented")
    }


    companion object {
        private val generator = CreatureGenerator()


        private val reducer = BiFunction { previousState: AddCreatureViewState, result: AddCreatureResults ->
            when (result) {
                is AddCreatureResults.AvatarResult -> reduceAvator (previousState, result)
                is AddCreatureResults.NameResult -> reduceName (previousState, result)
                is AddCreatureResults.IntelligenceResult -> reduceIntelligence (previousState, result)
                is AddCreatureResults.StrengthResult -> reduceStrength (previousState, result)
                is AddCreatureResults.EnduranceResult -> reduceEndurance (previousState, result)
                is AddCreatureResults.SaveCreatureResult -> reduceSave (previousState, result)
             else -> {}
            }
        }


        private fun reduceAvator(
                previousState: AddCreatureViewState,
                result: AddCreatureResults.AvatarResult
        ): AddCreatureViewState = when (result) {
            is AddCreatureResults.AvatarResult.Success -> {
                previousState.copy(isProcessing = false, creature = generator.generateCreature(
                        previousState.creature.attributes, previousState.creature.name, result.drawable), isDrawableSelected = (result.drawable != 0))
            }
            is AddCreatureResults.AvatarResult.Processing -> {
                previousState.copy(isProcessing = true)
            }
            is AddCreatureResults.AvatarResult.Failure -> {
                previousState.copy(isProcessing = false, error = null)
            }
        }


        private fun reduceName(
                previousState: AddCreatureViewState,
                result: AddCreatureResults.NameResult
        ): AddCreatureViewState = when (result) {
            is AddCreatureResults.NameResult.Success -> {
                previousState.copy(isProcessing = false, error = null, creature = generator.generateCreature(
                        previousState.creature.attributes, result.name, previousState.creature.drawable))
            }
            is AddCreatureResults.NameResult.Processing -> {
                previousState.copy(isProcessing = true, error = null)
            }
            is AddCreatureResults.NameResult.Failure -> {
                previousState.copy(isProcessing = false, error = result.error)
            }
        }

        private fun reduceIntelligence (
                previousState: AddCreatureViewState,
                result: AddCreatureResults.IntelligenceResult
        ): AddCreatureViewState = when (result) {
            is AddCreatureResults.IntelligenceResult.Success -> {
                val attributes = CreatureAttributes(result.intelligence, previousState.creature.attributes.strength, previousState.creature.attributes.endurance)
                previousState.copy(isProcessing = false, error = null,
                        creature = generator.generateCreature(attributes, previousState.creature.name, previousState.creature.drawable))
            }
            is AddCreatureResults.IntelligenceResult.Failure -> {
                previousState.copy(isProcessing = false, error = result.error)
            }
            is AddCreatureResults.IntelligenceResult.Processing -> {
                previousState.copy(isProcessing = true, error = null)
            }
        }

        private fun reduceStrength(
                previousState: AddCreatureViewState,
                result: AddCreatureResults.StrengthResult
        ): AddCreatureViewState = when (result) {
            is AddCreatureResults.StrengthResult.Success -> {
                val attributes = CreatureAttributes(previousState.creature.attributes.intelligence, result.strength, previousState.creature.attributes.endurance)
                previousState.copy(isProcessing = false, error = null, creature = generator.generateCreature(attributes, previousState.creature.name, previousState.creature.drawable))
            }
            is AddCreatureResults.StrengthResult.Failure -> {
                previousState.copy(isProcessing = false, error = result.error)
            }
            is AddCreatureResults.StrengthResult.Processing -> {
                previousState.copy(isProcessing = false, error = null)
            }
        }

        private fun reduceEndurance(
                previousState: AddCreatureViewState,
                result: AddCreatureResults.EnduranceResult
        ): AddCreatureViewState = when (result) {
            is AddCreatureResults.EnduranceResult.Success -> {
                val attributes = CreatureAttributes(previousState.creature.attributes.intelligence, previousState.creature.attributes.strength, result.endurance)
                previousState.copy(isProcessing = false, error = null, creature = generator.generateCreature(attributes, previousState.creature.name, previousState.creature.drawable))
            }
            is AddCreatureResults.EnduranceResult.Failure -> {
                previousState.copy(isProcessing = false, error = result.error)
            }
            is AddCreatureResults.EnduranceResult.Processing -> {
                previousState.copy(isProcessing = true, error = null)
            }
        }


        private fun reduceSave(
                previousState: AddCreatureViewState,
                result: AddCreatureResults.SaveCreatureResult
        ): AddCreatureViewState = when (result) {
            is AddCreatureResults.SaveCreatureResult.Success -> {
                previousState.copy(isProcessing = false, isSaveComplete = true, error = null)
            }
            is AddCreatureResults.SaveCreatureResult.Failure -> {
                previousState.copy(isSaveComplete = false, error = result.error)
            }
            is AddCreatureResults.SaveCreatureResult.Processing -> {
                previousState.copy(isProcessing = true, error = null)
            }
        }
    }
}
