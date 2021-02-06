package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.data.repository.CreatureRepository
import com.raywenderlich.android.creaturemon.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

class AllCreaturesProcessorHolder(
        private val creatureRepository: CreatureRepository,
        private val schedulerProvider: BaseSchedulerProvider
) {

    private val loadAllCreaturesProcessor =
            ObservableTransformer<AllCreaturesActions, AllCreaturesResults> { actions ->
                actions.flatMap {
                    creatureRepository.getAllCreatures()
                            .map { creatures -> AllCreaturesResults.LoadAllCreaturesResult.Success(creatures) }
                            .cast(AllCreaturesResults.LoadAllCreaturesResult::class.java)
                            .onErrorReturn(AllCreaturesResults.LoadAllCreaturesResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            .startWith(AllCreaturesResults.LoadAllCreaturesResult.Loading)
                }
            }

    private val clearAllCreaturesProcessor =
            ObservableTransformer<AllCreaturesActions, AllCreaturesResults> { actions ->
                actions.flatMap {
                    creatureRepository.clearAllCreatures()
                            .map { AllCreaturesResults.ClearAllCreaturesResult.Success }
                            .cast(AllCreaturesResults.ClearAllCreaturesResult::class.java)
                            .onErrorReturn(AllCreaturesResults.ClearAllCreaturesResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            .startWith(AllCreaturesResults.ClearAllCreaturesResult.Clearing)
                }
            }


    internal var actionProcessor =
            ObservableTransformer<AllCreaturesActions, AllCreaturesResults> { actions ->
                actions.publish { shared ->
                    Observable.merge(
                            shared.ofType(AllCreaturesActions.LoadAllCreaturesAction::class.java).compose(loadAllCreaturesProcessor),
                            shared.ofType(AllCreaturesActions.ClearAllCreaturesAction::class.java).compose(clearAllCreaturesProcessor)
                                    .mergeWith(
                                            //Error for not implemented actions
                                            shared.filter { v ->
                                                v !is AllCreaturesActions.LoadAllCreaturesAction && v !is AllCreaturesActions.ClearAllCreaturesAction
                                            }.flatMap { w ->
                                                Observable.error<AllCreaturesResults>(
                                                        IllegalArgumentException("Unknown Action type: $w")
                                                )
                                            }

                                    )
                    )
                }
            }

}