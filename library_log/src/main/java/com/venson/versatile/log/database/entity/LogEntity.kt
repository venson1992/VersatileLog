package com.venson.versatile.log.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log")
data class LogEntity(

    //自增主键，构造函数赋值为null
    @PrimaryKey(autoGenerate = true) var id: Long?,

    var time: Long,

    var tag: String?,

    var level: String,

    var type: String?,

    var head: String?,

    var msg: String?
)