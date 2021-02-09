package com.raywenderlich.android.creaturemon.app

import android.content.Context
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.data.repository.CreatureRepository
import com.raywenderlich.android.creaturemon.data.repository.room.RoomRepository
import com.raywenderlich.android.creaturemon.util.schedulers.BaseSchedulerProvider
import com.raywenderlich.android.creaturemon.util.schedulers.SchedulerProvider

object Injection {

    fun providerCreatureRepository(context: Context): CreatureRepository {
        return RoomRepository()
    }

    fun providerCreatureGenerator(): CreatureGenerator {
        return CreatureGenerator()
    }

    fun providerSchedulerProvider(): BaseSchedulerProvider = SchedulerProvider

}