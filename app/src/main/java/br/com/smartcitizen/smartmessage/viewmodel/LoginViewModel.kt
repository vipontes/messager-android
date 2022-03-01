package br.com.smartcitizen.smartmessage.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.com.smartcitizen.smartmessage.MainApplication
import br.com.smartcitizen.smartmessage.R
import br.com.smartcitizen.smartmessage.api.LoginService
import br.com.smartcitizen.smartmessage.db.AppDatabase
import br.com.smartcitizen.smartmessage.interfaces.ILocalUser
import br.com.smartcitizen.smartmessage.model.LoginBody
import br.com.smartcitizen.smartmessage.model.Response
import br.com.smartcitizen.smartmessage.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

class LoginViewModel (application: Application) : AndroidViewModel(application), ILocalUser {
    private val disposable = CompositeDisposable()

    var loginBody = MutableLiveData<LoginBody>()
    var userData = MutableLiveData<User>()
    var errorResponse = MutableLiveData<Response>()

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var loginService: LoginService

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
        loginBody.value = LoginBody("", "")
        getLoggedUser()
    }

    fun login() {
        loginBody.value?.let {
            val phone: String = it.usuario_telefone.toString()
            val password: String = it.usuario_senha.toString()

            checkLogin(phone, password)
        }
    }

    private fun checkLogin(phone: String, password: String) {
        disposable.add(
            loginService.login(phone, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<User>() {
                    override fun onSuccess(res: User) {
                        database.userDao().insert(res)
                        userData.value = res
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()

                        if (e is HttpException) {
                            if (e.code() == 400) {
                                errorResponse.value =
                                    Response(
                                        false,
                                        (getApplication() as MainApplication).getString(
                                            R.string.unauthorized
                                        )
                                    )
                            } else {
                                errorResponse.value =
                                    Response(
                                        false,
                                        (getApplication() as MainApplication).getString(
                                            R.string.internal_error
                                        )
                                    )
                            }
                        } else {
                            errorResponse.value =
                                Response(
                                    false,
                                    (getApplication() as MainApplication).getString(
                                        R.string.internal_error
                                    )
                                )
                        }
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    override fun getLoggedUser() {
        userData.value = database.userDao().getLoggedUser()
    }

    fun validate(): Boolean {

        loginBody.value?.let {
            val phone: String = it.usuario_telefone.toString()
            val password: String = it.usuario_senha.toString()

            if (phone.isEmpty()) {
                errorResponse.value =
                    Response((getApplication() as MainApplication).getString(R.string.phone_error))
                return false
            }

            if (password.isEmpty()) {
                errorResponse.value =
                    Response((getApplication() as MainApplication).getString(R.string.password_error))
                return false
            }

            return true
        }

        return false
    }
}