package br.com.smartcitizen.smartmessage.api.interfaces

import br.com.smartcitizen.smartmessage.model.User
import io.reactivex.Single
import retrofit2.http.GET

interface IUser {
    @GET("user/all-except-me")
    fun getAllUsersExceptMe(): Single<MutableList<User>>
}