package com.raywenderlich.android.creaturemon.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesProcessorHolder
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesViewModel
import com.raywenderlich.android.creaturemon.app.Injection
import java.lang.IllegalArgumentException

class CreaturemonViewModelFactory private constructor(
        private val appContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == AllCreaturesViewModel::class.java) {
            return AllCreaturesViewModel (
                    AllCreaturesProcessorHolder(
                            Injection.providerCreatureRepository(appContext),
                            Injection.providerSchedulerProvider())
                    ) as T
        }
        throw IllegalArgumentException("unknown model class $modelClass")
    }


    companion object : SingletonHolderSingleArg<CreaturemonViewModelFactory, Context>
    (::CreaturemonViewModelFactory)


}