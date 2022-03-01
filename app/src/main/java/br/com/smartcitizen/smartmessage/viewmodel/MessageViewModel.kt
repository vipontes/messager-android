package br.com.smartcitizen.smartmessage.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.smartcitizen.smartmessage.MainApplication
import br.com.smartcitizen.smartmessage.R
import br.com.smartcitizen.smartmessage.api.MessageService
import br.com.smartcitizen.smartmessage.db.AppDatabase
import br.com.smartcitizen.smartmessage.interfaces.ILocalUser
import br.com.smartcitizen.smartmessage.model.Message
import br.com.smartcitizen.smartmessage.model.Response
import br.com.smartcitizen.smartmessage.model.User
import br.com.smartcitizen.smartmessage.utils.SocketHandler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class MessageViewModel(application: Application) : AndroidViewModel(application), ILocalUser {
    private val disposable = CompositeDisposable()

    val messages by lazy { MutableLiveData<MutableList<Message>>() }
    private val tag = MessageViewModel::class.java.simpleName

    val user by lazy { MutableLiveData<User>() }
    val message by lazy { MutableLiveData<Message>() }
    val toastMessage by lazy { MutableLiveData<Response>() }

    private val socket = SocketHandler.getSocket()

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var messageService: MessageService

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
        getLoggedUser()
        message.value = user.value?.usuario_id?.let { Message(0, it, 0, "", "") }


        socket.on("private message") { args ->
            args[0].let {
                val obj = it as JSONObject

                messages.value?.let {
                    val incomingMessage = Message(
                        obj["mensagem_id"].toString().toLong(),
                        obj["usuario_emissor_id"].toString().toLong(),
                        obj["usuario_receptor_id"].toString().toLong(),
                        obj["mensagem_conteudo"].toString(),
                        obj["mensagem_data"].toString()
                    )

                    val list = messages.value!!
                    list.add(incomingMessage)
                    messages.postValue(list)
                }
            }
        }
    }

    override fun getLoggedUser() {
        val user = database.userDao().getLoggedUser()
        this.user.value = user!!
    }

    private fun sendMessageToWss(outgoingMessage: Message) {
        val contentMessage = JSONObject()
        contentMessage.put("mensagem_id", outgoingMessage.mensagem_id)
        contentMessage.put("usuario_emissor_id", outgoingMessage.usuario_emissor_id.toString())
        contentMessage.put("usuario_receptor_id", outgoingMessage.usuario_receptor_id.toString())
        contentMessage.put("mensagem_conteudo", outgoingMessage.mensagem_conteudo)
        contentMessage.put("mensagem_data", outgoingMessage.mensagem_data)
        socket.emit("private message", contentMessage)
    }

    fun sendMessageToApi() {
        message.value?.let {
            if (it.mensagem_conteudo.isNotEmpty()) {
                disposable.add(
                    messageService.postMessage(it)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<Message>() {
                            override fun onSuccess(res: Message) {
                                sendMessageToWss(res)
                                loadChat()

                                it.mensagem_conteudo = ""
                                message.value = it
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()

                                if (e is HttpException) {
                                    if (e.code() == 401) {
                                        toastMessage.value =
                                            Response(
                                                (getApplication() as MainApplication).getString(
                                                    R.string.unauthorized
                                                )
                                            )
                                    } else {
                                        toastMessage.value = Response(false, e.message())
                                    }
                                } else {
                                    toastMessage.value = Response(false, e.toString())
                                }
                            }
                        })
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun setToUser(to: User) {
        message.value?.let {
            message.value!!.usuario_receptor_id = to.usuario_id!!
        }
    }

    fun loadChat() {
        val userId = user.value?.usuario_id
        userId?.let { id ->
            disposable.add(
                messageService.getMessagesByUserId(id)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<MutableList<Message>>() {
                        override fun onSuccess(res: MutableList<Message>) {
                            messages.value = res
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()

                            if (e is HttpException) {
                                if (e.code() == 401) {
                                    toastMessage.value =
                                        Response(
                                            (getApplication() as MainApplication).getString(
                                                R.string.unauthorized
                                            )
                                        )
                                } else {
                                    toastMessage.value = Response(false, e.message())
                                }
                            } else {
                                toastMessage.value = Response(false, e.toString())
                            }
                        }
                    })
            )
        }
    }

    fun startedTyping() {
        val contentMessage = JSONObject()
        socket.emit("typing", contentMessage)
    }

    fun finishedTyping() {
        val contentMessage = JSONObject()
        socket.emit("stop typing", contentMessage)
    }
}