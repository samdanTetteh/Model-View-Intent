package com.raywenderlich.android.creaturemon.addcreature;

import androidx.lifecycle.ViewModel
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesViewState
import com.raywenderlich.android.creaturemon.data.model.CreatureAttributes
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.mviBase.MviViewModel
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject


class AddCreatureViewModel(
        private val addActionProcessorHolder: AddCreatureProcessorHolder
) : ViewModel(), MviViewModel<AddCreatureIntents, AddCreatureViewState> {

    private val intentsSubject: PublishSubject<AddCreatureIntents> = PublishSubject.create()
    private val stateObservable: Observable<AddCreatureViewState> = compose()

    override fun processIntents(intents: Observable<AddCreatureIntents>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<AddCreatureViewState> = stateObservable

    private fun compose(): Observable<AddCreatureViewState> {
        return intentsSubject
                .map(this::actionFromIntent)
                .compose(addActionProcessorHolder.actionProcessor)
                .scan(AddCreatureViewState.default(), reducer)
                .distinctUntilChanged()
                .replay(1)
                .autoConnect(0)
    }

    private fun actionFromIntent(intents: AddCreatureIntents) : AddCreaturesActions {
        return when (intents) {
            is AddCreatureIntents.AvatarIntent -> AddCreaturesActions.AvatarAction(intents.drawable)
            is AddCreatureIntents.NameIntent -> AddCreaturesActions.NameAction(intents.name)
            is AddCreatureIntents.IntelligenceIntent -> AddCreaturesActions.IntelligenceAction(intents.intelligenceIndex)
            is AddCreatureIntents.StrengthIntent -> AddCreaturesActions.StrengthAction(intents.strengthIndex)
            is AddCreatureIntents.EnduranceIntent -> AddCreaturesActions.EnduranceAction(intents.enduranceIndex)
            is AddCreatureIntents.SaveIntent -> AddCreaturesActions.SaveAction(
                    intents.drawable,
                    intents.name,
                    intents.intelligenceIndex,
                    intents.strengthIndex,
                    intents.enduranceIndex
            )
        }
    }

    companion object {
        private val generator = CreatureGenerator()


        private val reducer = BiFunction { previousState: AddCreatureViewState, result: AddCreatureResults ->
            when (result) {
                is AddCreatureResults.AvatarResult -> reduceAvator(previousState, result)
                is AddCreatureResults.NameResult -> reduceName(previousState, result)
                is AddCreatureResults.IntelligenceResult -> reduceIntelligence(previousState, result)
                is AddCreatureResults.StrengthResult -> reduceStrength(previousState, result)
                is AddCreatureResults.EnduranceResult -> reduceEndurance(previousState, result)
                is AddCreatureResults.SaveCreatureResult -> reduceSave(previousState, result)
            }
        }


        private fun reduceAvator(
                previousState: AddCreatureViewState,
                result: AddCreatureResults.AvatarResult
        ): AddCreatureViewState = when (result) {
            is AddCreatureResults.AvatarResult.Success -> {
                previousState.copy(isProcessing = false, error = null, creature = generator.generateCreature(
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

        private fun reduceIntelligence(
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
                previousState.copy(isProcessing = false, isSaveComplete = false, error = result.error)
            }
            is AddCreatureResults.SaveCreatureResult.Processing -> {
                previousState.copy(isProcessing = true, error = null)
            }
        }
    }
}
