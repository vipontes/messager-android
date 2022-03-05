package easify.mess.api.interfaces

import easify.mess.model.User
import io.reactivex.Single
import retrofit2.http.GET

interface IUser {
    @GET("user/all-except-me")
    fun getAllUsersExceptMe(): Single<MutableList<User>>
}