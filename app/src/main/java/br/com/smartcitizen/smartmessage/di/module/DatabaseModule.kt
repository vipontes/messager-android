package br.com.smartcitizen.olhovivo.di.module

import android.app.Application
import br.com.smartcitizen.smartmessage.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun providesAppDatabase(application: Application): AppDatabase {
        return AppDatabase.getAppDataBase(application)
    }
}