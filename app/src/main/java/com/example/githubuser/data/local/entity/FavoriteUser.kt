package com.example.githubuser.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class FavoriteUser(
    @PrimaryKey(autoGenerate = false)
    @field:ColumnInfo(name = "username")
    var username: String = "",

    @field:ColumnInfo(name = "avatarUrl")
    var avatarUrl: String? = null
)