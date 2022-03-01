package br.com.smartcitizen.smartmessage.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.smartcitizen.smartmessage.MainApplication
import br.com.smartcitizen.smartmessage.R
import br.com.smartcitizen.smartmessage.api.UserService
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
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class HomeViewModel(application: Application) : AndroidViewModel(application), ILocalUser {
    private val disposable = CompositeDisposable()

    private lateinit var socket: Socket

    private val tag = HomeViewModel::class.java.simpleName

    var userData = MutableLiveData<User>()
    val userList by lazy { MutableLiveData<MutableList<User>>() }
    val toastMessage by lazy { MutableLiveData<Response>() }

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var userService: UserService

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
        getLoggedUser()
    }

    override fun getLoggedUser() {
        val user = database.userDao().getLoggedUser()
        userData.value = user!!
    }

    private fun initSocket(user: User?) {
        user?.let { usr ->
            SocketHandler.initSocket(usr)
            SocketHandler.establishConnection()
            socket = SocketHandler.getSocket()

            socket.on("private message") { args ->
                args[0].let {
                    val obj = it as JSONObject

                    val incomingMessage = Message(
                        obj["mensagem_id"].toString().toLong(),
                        obj["usuario_emissor_id"].toString().toLong(),
                        obj["usuario_receptor_id"].toString().toLong(),
                        obj["mensagem_conteudo"].toString(),
                        obj["mensagem_data"].toString()
                    )

                    userList.value?.let {
                        val list = userList.value!!

                        var i = 0;
                        list.forEach { user ->
                            if (user.usuario_id == incomingMessage.usuario_emissor_id) {
                                user.highlighted = 1
                            } else {
                                user.highlighted = 0
                            }

                            list[i] = user

                            i++
                        }

                        userList.postValue(list)
                    }
                }
            }

            socket.on("users") { args ->
                args[0].let {
                    val obj = it as JSONArray
                    for (i in 0 until obj.length()) {
                        val userFromJson = obj.getJSONObject(i)
                        val existingUser = userFromJson.get("userId").toString().toLong()
                        userList.value?.let {
                            val list = userList.value!!

                            var i = 0;
                            list.forEach { user ->
                                if (user.usuario_id == existingUser) {
                                    user.connected = 1
                                }

                                list[i] = user
                                i++
                            }

                            userList.postValue(list)
                        }
                    }
                }
            }

            socket.on("user connected") { args ->
                args[0].let {
                    val obj = it as JSONObject
                    val userId = obj.get("userId").toString().toLong()

                    userList.value?.let {
                        val list = userList.value!!

                        var i = 0;
                        list.forEach { user ->
                            if (user.usuario_id == userId) {
                                user.connected = 1
                                list[i] = user
                            }
                            i++
                        }

                        userList.postValue(list)
                    }
                }
            }

            socket.on("user disconnected") { args ->
                args[0].let {
                    val obj = it as JSONObject
                    val userId = obj.get("userId").toString().toLong()

                    userList.value?.let {
                        val list = userList.value!!

                        var i = 0;
                        list.forEach { user ->
                            if (user.usuario_id == userId) {
                                user.connected = 0
                                list[i] = user
                            }
                            i++
                        }

                        userList.postValue(list)
                    }
                }
            }

            socket.on("typing") { args ->
                args[0].let {
                    val obj = it as JSONObject
                    val userId = obj.get("userId").toString().toLong()

                    userList.value?.let {
                        val list = userList.value!!

                        var i = 0;
                        list.forEach { user ->
                            if (user.usuario_id == userId) {
                                user.typing = 1
                                list[i] = user
                            }
                            i++
                        }

                        userList.postValue(list)
                    }
                }
            }

            socket.on("stop typing") { args ->
                args[0].let {
                    val obj = it as JSONObject
                    val userId = obj.get("userId").toString().toLong()

                    userList.value?.let {
                        val list = userList.value!!

                        var i = 0;
                        list.forEach { user ->
                            if (user.usuario_id == userId) {
                                user.typing = 0
                                list[i] = user
                            }
                            i++
                        }

                        userList.postValue(list)
                    }
                }
            }
        }
    }

    fun getAllUsersExceptMe() {
        disposable.add(
            userService.getAllUsersExceptMe()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MutableList<User>>() {
                    override fun onSuccess(res: MutableList<User>) {
                        userList.value = res
                        initSocket(userData.value)
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


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
        SocketHandler.closeConnection()
    }
}