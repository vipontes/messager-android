package easify.mess.api

import android.app.Application
import easify.mess.api.interfaces.ILogin
import easify.mess.model.LoginBody
import easify.mess.model.User
import io.reactivex.Single

class LoginService (application: Application) {

    private val api = RetrofitBuilder(application).retrofit()
        .create(ILogin::class.java)

    fun login(phoneNumber: String, userPass: String): Single<User> {
        val body = LoginBody(phoneNumber, userPass)
        return api.login(body)
    }
}