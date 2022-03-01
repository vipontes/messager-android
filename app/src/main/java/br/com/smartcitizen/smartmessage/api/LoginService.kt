package br.com.smartcitizen.smartmessage.api

import android.app.Application
import br.com.smartcitizen.smartmessage.api.interfaces.ILogin
import br.com.smartcitizen.smartmessage.model.LoginBody
import br.com.smartcitizen.smartmessage.model.User
import io.reactivex.Single

class LoginService (application: Application) {

    private val api = RetrofitBuilder(application).retrofit()
        .create(ILogin::class.java)

    fun login(phoneNumber: String, userPass: String): Single<User> {
        val body = LoginBody(phoneNumber, userPass)
        return api.login(body)
    }
}