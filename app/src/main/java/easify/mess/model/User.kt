package easify.mess.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class User(
    @PrimaryKey
    var usuario_id: Long?,
    var usuario_nome: String?,
    var usuario_telefone: String?,
    var usuario_senha: String?,
    var usuario_ativo: Int,
    var usuario_data_criacao: String?,
    var usuario_token: String?,
    var usuario_refresh_token: String?,
    var highlighted: Int?,
    var connected: Int?,
    var typing: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(usuario_id)
        parcel.writeString(usuario_nome)
        parcel.writeString(usuario_telefone)
        parcel.writeString(usuario_senha)
        parcel.writeInt(usuario_ativo)
        parcel.writeString(usuario_data_criacao)
        parcel.writeString(usuario_token)
        parcel.writeString(usuario_refresh_token)
        parcel.writeValue(highlighted)
        parcel.writeValue(connected)
        parcel.writeValue(typing)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}