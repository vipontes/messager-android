package easify.mess

import android.app.Application
import br.com.mess.olhovivo.di.module.AppModule
import easify.mess.di.component.AppComponent
import easify.mess.di.component.DaggerAppComponent

class MainApplication : Application() {

    private lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()

        this.appComponent = DaggerAppComponent.builder()
            .application(AppModule(this))
            .build()
    }

    fun getAppComponent(): AppComponent? {
        return appComponent
    }
}