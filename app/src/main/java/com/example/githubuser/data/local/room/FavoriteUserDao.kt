package com.example.githubuser.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.githubuser.data.local.entity.FavoriteUser

@Dao
interface FavoriteUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(fUser: FavoriteUser)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertList(news: List<FavoriteUser>)

    @Update
    fun update(fUser: FavoriteUser)

    @Delete
    fun delete(fUser: FavoriteUser)

    @Query("SELECT * FROM FavoriteUser")
    fun getAllFavoriteUser(): LiveData<List<FavoriteUser>>

    @Query("SELECT * FROM FavoriteUser WHERE username LIKE '%'|| :username ||'%'")
    fun getUserByUsername(username: String): LiveData<List<FavoriteUser>>

    @Query("SELECT EXISTS(SELECT * FROM FavoriteUser WHERE username = :username)")
    fun isfUserFavorite(username: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM FavoriteUser WHERE username = :username)")
    fun searchFavoriteUserByUsername(username: String): LiveData<Boolean>

    @Query("DELETE FROM FavoriteUser")
    fun deleteAll()
}