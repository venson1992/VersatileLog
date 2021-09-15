package com.venson.versatile.log.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.venson.versatile.log.BaseLog
import com.venson.versatile.log.database.LogEntity
import com.venson.versatile.log.work.DefaultExecutorSupplier

@Dao
abstract class LogDao {

    fun insertLog(tag: String?, level: Int, msg: String?) {
        DefaultExecutorSupplier.instance.forBackgroundTasks().execute {
            innerInsertLog(
                LogEntity(
                    null,
                    System.currentTimeMillis(),
                    tag,
                    level.toString(),
                    when (level) {
                        BaseLog.JSON -> {
                            "json"
                        }
                        BaseLog.XML -> {
                            "xml"
                        }
                        else -> {
                            ""
                        }
                    },
                    msg
                )
            )
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun innerInsertLog(logEntity: LogEntity)

    @Query(value = "SELECT * FROM log")
    abstract fun allList(): List<LogEntity>
}