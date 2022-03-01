package br.com.smartcitizen.smartmessage.api

import android.app.Application
import br.com.smartcitizen.smartmessage.api.interfaces.IUser
import br.com.smartcitizen.smartmessage.model.User
import io.reactivex.Single

class UserService (application: Application) {

    private val api = RetrofitBuilder(application).retrofitAuth()
        .create(IUser::class.java)

    fun getAllUsersExceptMe(): Single<MutableList<User>> {
        return api.getAllUsersExceptMe()
    }
}