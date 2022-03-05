package easify.mess.api.interfaces

import easify.mess.model.Message
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IMessage {
    @GET("message/by-user/{fromUser}/{toUser}")
    fun getMessagesByUserId(
        @Path("fromUser") fromUser: Long,
        @Path("toUser") toUser: Long
    ): Single<MutableList<Message>>

    @POST("message")
    fun postMessage(@Body data: Message): Single<Message>
}