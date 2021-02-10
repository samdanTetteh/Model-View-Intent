package com.raywenderlich.android.creaturemon.AllCreatures

import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesIntents
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesProcessorHolder
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesViewModel
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesViewState
import com.raywenderlich.android.creaturemon.data.model.Creature
import com.raywenderlich.android.creaturemon.data.model.CreatureAttributes
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.data.repository.CreatureRepository
import com.raywenderlich.android.creaturemon.util.schedulers.BaseSchedulerProvider
import com.raywenderlich.android.creaturemon.util.schedulers.ImmediateSchedulerProvider
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.lang.Exception

class AllCreaturesViewModeTest {

    @Mock
    private lateinit var createRepository: CreatureRepository
    private lateinit var scheduleProvider: BaseSchedulerProvider
    private lateinit var generator: CreatureGenerator
    private lateinit var viewModel: AllCreaturesViewModel
    private lateinit var testObserver: TestObserver<AllCreaturesViewState>
    private lateinit var creatures: List<Creature>



    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        scheduleProvider = ImmediateSchedulerProvider()

        generator = CreatureGenerator()

        viewModel = AllCreaturesViewModel(AllCreaturesProcessorHolder(createRepository, scheduleProvider))

        creatures = listOf(
                generator.generateCreature(CreatureAttributes(3, 7, 10), "Dino", 5),
                generator.generateCreature(CreatureAttributes(1, 1, 12), "Slepping Bear", 4),
                generator.generateCreature(CreatureAttributes(2, 3, 1), "Dino", 10)
        )

        testObserver = viewModel.states().test()
    }


    @Test
    fun `load_all_creatures_from_repository_and_load_into_view`(){
        `when` (createRepository.getAllCreatures()).thenReturn(Observable.just(creatures))

        viewModel.processIntents(Observable.just(AllCreaturesIntents.LoadAllCreaturesIntent))

        testObserver.assertValueAt(1, AllCreaturesViewState::isLoading)
        testObserver.assertValueAt(2) { allCreaturesViewState: AllCreaturesViewState ->
            !allCreaturesViewState.isLoading
        }
    }


    @Test
    fun `error_loading_creatures_from_repository`(){
        `when` (createRepository.getAllCreatures()).thenReturn(Observable.error(Exception()))

        viewModel.processIntents(Observable.just(AllCreaturesIntents.LoadAllCreaturesIntent))

        testObserver.assertValueAt(2) {state ->
            state.error != null
        }
    }

}