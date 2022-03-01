package br.com.smartcitizen.smartmessage.di.module

import android.app.Application
import br.com.smartcitizen.smartmessage.api.LoginService
import br.com.smartcitizen.smartmessage.api.MessageService
import br.com.smartcitizen.smartmessage.api.UserService

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    fun providesLoginService(application: Application): LoginService {
        return LoginService(application)
    }

    @Provides
    @Singleton
    fun providesUserService(application: Application): UserService {
        return UserService(application)
    }

    @Provides
    @Singleton
    fun providesMessageService(application: Application): MessageService {
        return MessageService(application)
    }
}
