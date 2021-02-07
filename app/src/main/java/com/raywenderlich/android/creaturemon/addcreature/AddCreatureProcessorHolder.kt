package com.raywenderlich.android.creaturemon.addcreature

import com.raywenderlich.android.creaturemon.data.model.AttributeStore
import com.raywenderlich.android.creaturemon.data.model.CreatureAttributes
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.data.repository.CreatureRepository
import com.raywenderlich.android.creaturemon.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import java.lang.IllegalArgumentException

class AddCreatureProcessorHolder (
        private val creatureRepository: CreatureRepository,
        private val schedulerProvider: BaseSchedulerProvider,
        private val creatureGenerator: CreatureGenerator
) {

    private val avatarProcessor =
            ObservableTransformer<AddCreaturesActions.AvatarAction,  AddCreatureResults.AvatarResult>{ actions ->
                actions
                        .map { action -> AddCreatureResults.AvatarResult.Success(action.drawable)}
                        .cast(AddCreatureResults.AvatarResult::class.java)
                        .onErrorReturn(AddCreatureResults.AvatarResult::Failure)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .startWith(AddCreatureResults.AvatarResult.Processing)
            }

    private val nameProcessor =
            ObservableTransformer<AddCreaturesActions.NameAction, AddCreatureResults.NameResult> { actions ->
                actions
                        .map { action -> AddCreatureResults.NameResult.Success(action.name) }
                        .cast(AddCreatureResults.NameResult::class.java)
                        .onErrorReturn(AddCreatureResults.NameResult::Failure)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .startWith(AddCreatureResults.NameResult.Processing)
            }


    private val intelligenceProcessor =
            ObservableTransformer<AddCreaturesActions.IntelligenceAction, AddCreatureResults.IntelligenceResult> { actions ->
                actions
                        .map { action -> AddCreatureResults.IntelligenceResult.Success(AttributeStore.INTELLIGENCE[action.intelligenceIndex].value) }
                        .cast(AddCreatureResults.IntelligenceResult::class.java)
                        .onErrorReturn (AddCreatureResults.IntelligenceResult::Failure )
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .startWith(AddCreatureResults.IntelligenceResult.Processing)
            }


    private val strengthProcessor =
            ObservableTransformer<AddCreaturesActions.StrengthAction, AddCreatureResults.StrengthResult> { actions ->
                actions
                        .map { action -> AddCreatureResults.StrengthResult.Success(AttributeStore.STRENGTH[action.strengthIndex].value ) }
                        .cast(AddCreatureResults.StrengthResult::class.java)
                        .onErrorReturn (AddCreatureResults.StrengthResult::Failure)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .startWith(AddCreatureResults.StrengthResult.Processing)
            }


    private val enduranceProcessor =
            ObservableTransformer<AddCreaturesActions.EnduranceAction, AddCreatureResults.EnduranceResult> { actions ->
                actions
                        .map { action -> AddCreatureResults.EnduranceResult.Success(AttributeStore.ENDURANCE[action.enduranceIndex].value) }
                        .cast(AddCreatureResults.EnduranceResult::class.java)
                        .onErrorReturn(AddCreatureResults.EnduranceResult::Failure)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .startWith(AddCreatureResults.EnduranceResult.Processing)
            }


    private val saveProcessor =
            ObservableTransformer<AddCreaturesActions.SaveAction,  AddCreatureResults.SaveCreatureResult> { actions ->
                actions
                        .flatMap { action ->
                            val attributes = CreatureAttributes(
                                    AttributeStore.INTELLIGENCE[action.intelligenceIndex].value,
                                    AttributeStore.STRENGTH[action.strengthIndex].value,
                                    AttributeStore.ENDURANCE[action.enduranceIndex].value
                            )

                            val creature = creatureGenerator.generateCreature(attributes, action.name, action.drawable)
                            creatureRepository.saveCreature(creature)
                                    .map { AddCreatureResults.SaveCreatureResult.Success }
                                    .cast(AddCreatureResults.SaveCreatureResult::class.java)
                                    .onErrorReturn(AddCreatureResults.SaveCreatureResult::Failure)
                                    .subscribeOn(schedulerProvider.io())
                                    .observeOn(schedulerProvider.ui())
                                    .startWith(AddCreatureResults.SaveCreatureResult.Processing)
                        }
            }


    internal val actionProcess =
            ObservableTransformer<AddCreaturesActions, AddCreatureResults> { actions ->
                actions.publish { shared ->
                    Observable.merge(
                            shared.ofType(AddCreaturesActions.AvatarAction::class.java).compose(avatarProcessor),
                            shared.ofType(AddCreaturesActions.NameAction::class.java).compose(nameProcessor),
                            shared.ofType(AddCreaturesActions.IntelligenceAction::class.java).compose(intelligenceProcessor),
                            shared.ofType(AddCreaturesActions.EnduranceAction::class.java).compose(enduranceProcessor)
                    ).mergeWith(
                            shared.ofType(AddCreaturesActions.SaveAction::class.java).compose(saveProcessor)
                    ).mergeWith(
                            // Error for not implemented actions
                    shared.filter{ action ->
                            action !is AddCreaturesActions.AvatarAction
                                    && action !is AddCreaturesActions.NameAction
                                    && action !is AddCreaturesActions.IntelligenceAction
                                    && action !is AddCreaturesActions.StrengthAction
                                    && action !is AddCreaturesActions.EnduranceAction
                    }. flatMap {
                        Observable.error<AddCreatureResults>(
                                IllegalArgumentException("Unknown Action $it")
                        )
                    })
                }
            }

}