package br.com.smartcitizen.smartmessage

import android.app.Application
import br.com.smartcitizen.olhovivo.di.module.AppModule
import br.com.smartcitizen.smartmessage.di.component.AppComponent
import br.com.smartcitizen.smartmessage.di.component.DaggerAppComponent

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