package easify.mess.model

import android.os.Parcel
import android.os.Parcelable

data class LoginBody (
    var usuario_telefone: String?,
    var usuario_senha: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(usuario_telefone)
        parcel.writeString(usuario_senha)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginBody> {
        override fun createFromParcel(parcel: Parcel): LoginBody {
            return LoginBody(parcel)
        }

        override fun newArray(size: Int): Array<LoginBody?> {
            return arrayOfNulls(size)
        }
    }
}