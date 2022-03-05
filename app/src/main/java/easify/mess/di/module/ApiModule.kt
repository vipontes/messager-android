package easify.mess.di.module

import android.app.Application
import easify.mess.api.LoginService
import easify.mess.api.MessageService
import easify.mess.api.UserService

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
