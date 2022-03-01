package br.com.smartcitizen.smartmessage.db.dao

import androidx.room.*
import br.com.smartcitizen.smartmessage.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM usuario LIMIT 1")
    fun getLoggedUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userList: List<User>)

    @Query("DELETE FROM usuario WHERE usuario_id = :userId")
    fun delete(userId: Long)

    @Query("DELETE FROM usuario")
    fun delete()

    @Update
    fun update(sqliteUser: User?)
}