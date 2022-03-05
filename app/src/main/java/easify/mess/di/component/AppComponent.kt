package easify.mess.di.component

import br.com.mess.olhovivo.di.module.AppModule
import br.com.mess.olhovivo.di.module.DatabaseModule

import easify.mess.MainApplication
import easify.mess.api.interceptor.AuthInterceptor
import easify.mess.di.module.ApiModule
import easify.mess.viewmodel.HomeViewModel
import easify.mess.viewmodel.LoginViewModel
import easify.mess.viewmodel.MessageViewModel
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
