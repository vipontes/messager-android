package br.com.smartcitizen.smartmessage.di.component

import br.com.smartcitizen.olhovivo.di.module.AppModule
import br.com.smartcitizen.olhovivo.di.module.DatabaseModule

import br.com.smartcitizen.smartmessage.MainApplication
import br.com.smartcitizen.smartmessage.api.interceptor.AuthInterceptor
import br.com.smartcitizen.smartmessage.di.module.ApiModule
import br.com.smartcitizen.smartmessage.viewmodel.HomeViewModel
import br.com.smartcitizen.smartmessage.viewmodel.LoginViewModel
import br.com.smartcitizen.smartmessage.viewmodel.MessageViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    ApiModule::class,
    DatabaseModule::class
])
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun application(app: AppModule): Builder
        fun build(): AppComponent
    }

    fun inject(app: MainApplication)

    fun inject(viewModel: LoginViewModel)
    fun inject(viewModel: HomeViewModel)
    fun inject(viewModel: MessageViewModel)

    fun inject(interceptor: AuthInterceptor)
}
