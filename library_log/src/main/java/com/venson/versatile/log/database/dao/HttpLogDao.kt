package com.venson.versatile.log.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.venson.versatile.log.database.entity.HttpLogEntity
import com.venson.versatile.log.work.DefaultExecutorSupplier

@Dao
abstract class HttpLogDao {

    /**
     * 插入日志
     */
    fun insertLog(
        request: String?,
        response: String?,
        startTime: Long,
        endTime: Long,
        duration: Long
    ) {
        DefaultExecutorSupplier.instance.forBackgroundTasks().execute {
            innerInsertLog(
                HttpLogEntity(
                    null, request, response, startTime, endTime, duration
                )
            )
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun innerInsertLog(httpLogEntity: HttpLogEntity)

    @Query(value = "DELETE FROM log WHERE time < :time")
    abstract fun deleteOverLifeData(time: Long)

}