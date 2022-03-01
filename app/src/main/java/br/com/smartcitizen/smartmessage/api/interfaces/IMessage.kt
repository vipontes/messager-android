package br.com.smartcitizen.smartmessage.api.interfaces

import br.com.smartcitizen.smartmessage.model.Message
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IMessage {
    @GET("message/by-user/{userId}")
    fun getMessagesByUserId(@Path("userId") userId: Long): Single<MutableList<Message>>

    @POST("message")
    fun postMessage(@Body data: Message): Single<Message>
}