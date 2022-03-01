package br.com.smartcitizen.smartmessage.api.interceptor

import android.app.Application
import br.com.smartcitizen.smartmessage.MainApplication
import br.com.smartcitizen.smartmessage.R
import br.com.smartcitizen.smartmessage.api.RetrofitBuilder
import br.com.smartcitizen.smartmessage.api.interfaces.ILogin
import br.com.smartcitizen.smartmessage.db.AppDatabase
import br.com.smartcitizen.smartmessage.model.RefreshTokenBody
import br.com.smartcitizen.smartmessage.model.User


import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import kotlin.jvm.Throws

class AuthInterceptor @Inject constructor(
    var application: Application,
    private val retrofitBuilder: RetrofitBuilder
) : Interceptor {

    @Inject
    lateinit var database: AppDatabase

    private var currentUser = User(0,"","","",0,"","","", 0, 0, 0)

    init {
        (application as MainApplication).getAppComponent()?.inject(this)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        synchronized(this) {
            val originalRequest: Request = chain.request()

            getUser()

            val requestBuilder: Request.Builder = originalRequest.newBuilder()

            if (originalRequest.header("No-Authentication") == null) {
                if (currentUser.usuario_token!!.isEmpty()) {
                    throw java.lang.RuntimeException(
                        application.getString(R.string.token_not_found))
                } else {
                    requestBuilder.addHeader(
                        "Authorization",
                        "Bearer ${currentUser.usuario_token}"
                    )
                    val initialResponse = chain.proceed(requestBuilder.build())
                    when {
                        initialResponse.code() == 401 -> {
                            val responseNewTokenLoginModel = runBlocking {
                                val body =
                                    RefreshTokenBody(currentUser.usuario_refresh_token)
                                retrofitBuilder.retrofit()
                                    .create(ILogin::class.java)
                                    .refreshToken(body).execute()
                            }

                            return when {
                                (responseNewTokenLoginModel == null ||
                                        responseNewTokenLoginModel.code() != 200) -> {
                                    database.userDao().delete()
                                    return initialResponse
                                }
                                else -> {
                                    responseNewTokenLoginModel.body()?.let {
                                        updateUser(it)
                                    }

                                    val newAuthenticationRequest =
                                        originalRequest.newBuilder()
                                            .addHeader(
                                                "Authorization",
                                                "Bearer ${currentUser.usuario_token}"
                                            )
                                            .build()

                                    chain.proceed(newAuthenticationRequest)
                                }
                            }
                        }
                        else -> {
                            return initialResponse
                        }
                    }
                }
            }

            return chain.proceed(requestBuilder.build())
        }
    }

    private fun updateUser(user: User) {
        this.currentUser = user
        database.userDao().delete()
        database.userDao().insert(this.currentUser)
    }

    private fun getUser() {
        val dbToken = database.userDao().getLoggedUser()
        dbToken?.let {
            currentUser = it
        }
    }
}
