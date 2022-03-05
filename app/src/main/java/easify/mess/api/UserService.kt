package easify.mess.api

import android.app.Application
import easify.mess.api.interfaces.IUser
import easify.mess.model.User
import io.reactivex.Single

class UserService (application: Application) {

    private val api = RetrofitBuilder(application).retrofitAuth()
        .create(IUser::class.java)

    fun getAllUsersExceptMe(): Single<MutableList<User>> {
        return api.getAllUsersExceptMe()
    }
}