package com.example.githubuser.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.githubuser.data.local.entity.FavoriteUser

@Dao
interface FavoriteUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(fUser:FavoriteUser)

    @Update
    fun update(fUser: FavoriteUser)

    @Delete
    fun delete(fUser:FavoriteUser)

    @Query("SELECT * FROM FavoriteUser ORDER BY username ASC")
    fun getAllFavoriteUser(): LiveData<List<FavoriteUser>>

    @Query("SELECT * FROM FavoriteUser WHERE username = :username")
    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUser>

    @Query("SELECT EXISTS(SELECT * FROM FavoriteUser WHERE username = :username AND bookmarked = 1)")
    fun isfUserBookmarked(username: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavorite(news: List<FavoriteUser>)

    @Query("DELETE FROM FavoriteUser WHERE bookmarked = 0")
    fun deleteAll()
}