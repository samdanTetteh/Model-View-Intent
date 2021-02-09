package com.raywenderlich.android.creaturemon.AllCreatures

import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesViewModel
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesViewState
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.data.repository.CreatureRepository
import com.raywenderlich.android.creaturemon.util.schedulers.BaseSchedulerProvider
import io.reactivex.observers.TestObserver
import org.mockito.Mock

class AllCreaturesViewModeTest {

    @Mock
    private lateinit var createRepository: CreatureRepository
    private lateinit var scheduleProvider: BaseSchedulerProvider
    private lateinit var generator: CreatureGenerator
    private lateinit var viewModel: AllCreaturesViewModel
    private lateinit var testObserver: TestObserver<AllCreaturesViewState>
    private lateinit var creatures: List<CreatureGenerator>
}