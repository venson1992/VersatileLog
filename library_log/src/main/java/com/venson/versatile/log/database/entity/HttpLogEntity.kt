package com.venson.versatile.log.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "http_log")
data class HttpLogEntity(
    //自增主键，构造函数赋值为null
    @PrimaryKey(autoGenerate = true) var id: Long?,

    var url: String?,

    var method: String?,

    var request: String?,

    var response: String?,

    var startTime: Long,

    var endTime: Long,

    var duration: Long
)