package easify.mess.utils

import android.util.Log
import easify.mess.model.User
import io.socket.client.IO
import io.socket.client.Socket

object SocketHandler {

    private lateinit var socketConnection: Socket
    private val tag = SocketHandler::class.java.simpleName

    @Synchronized
    fun initSocket(user: User) {
        try {
            val options = IO.Options()
            options.reconnection = true
            options.forceNew = true

            val authMap = mapOf(
                "userId" to user.usuario_id.toString() )
            options.auth = authMap

            socketConnection = IO.socket(Constants.wssUrl, options)

            socketConnection.on(Socket.EVENT_CONNECT) {
                Log.d(tag, "==============================CONNECTED")
            }?.on(Socket.EVENT_DISCONNECT) {
                Log.d(tag, "==============================OFF")
            }
        } catch (e: Exception) {
            Log.d(tag, e.message.toString())
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return socketConnection
    }

    @Synchronized
    fun establishConnection() {
        socketConnection.connect()
    }

    @Synchronized
    fun closeConnection() {
        socketConnection.disconnect()
    }
}
