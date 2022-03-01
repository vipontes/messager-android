package br.com.smartcitizen.smartmessage.api.interfaces

import br.com.smartcitizen.smartmessage.model.LoginBody
import br.com.smartcitizen.smartmessage.model.RefreshTokenBody
import br.com.smartcitizen.smartmessage.model.User
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ILogin {
    @POST("user/login")
    @Headers("No-Authentication: true")
    fun login(@Body data: LoginBody): Single<User>

    @POST("refresh-token")
    @Headers("No-Authentication: true")
    fun refreshToken(@Body data: RefreshTokenBody): Call<User>
}