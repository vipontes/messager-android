package br.com.smartcitizen.smartmessage.api

import android.app.Application
import br.com.smartcitizen.smartmessage.api.interfaces.IMessage
import br.com.smartcitizen.smartmessage.model.Message

import io.reactivex.Single

class MessageService (application: Application) {

    private val api = RetrofitBuilder(application).retrofitAuth()
        .create(IMessage::class.java)

    fun getMessagesByUserId(userId: Long): Single<MutableList<Message>> {
        return api.getMessagesByUserId(userId)
    }

    fun postMessage(message: Message): Single<Message> {
        return api.postMessage(message)
    }
}