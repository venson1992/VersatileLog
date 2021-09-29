package com.venson.versatile.log.database.dao

import androidx.room.*
import com.venson.versatile.log.database.entity.HttpLogEntity

@Dao
abstract class HttpLogDao {

    /**
     * 更新日志
     */
    fun updateLog(
        columnId: Long,
        url: String?,
        method: String?,
        request: String?,
        response: String?,
        startTime: Long,
        endTime: Long,
        duration: Long
    ) {
        if (columnId > 0L) {
            innerGetLog(columnId)?.let {
                innerUpdateLog(
                    HttpLogEntity(
                        columnId, url, method, request, response, startTime, endTime, duration
                    )
                )
                return
            }
        }
        insertLog(url, method, request, response, startTime, endTime, duration)
    }

    /**
     * 插入日志
     */
    fun insertLog(
        url: String?,
        method: String?,
        request: String?,
        response: String?,
        startTime: Long,
        endTime: Long,
        duration: Long
    ): Long {
        return innerInsertLog(
            HttpLogEntity(
                null, url, method, request, response, startTime, endTime, duration
            )
        )
    }

    @Query(value = "SELECT * FROM HTTP_LOG")
    abstract fun getAllLog(): List<HttpLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun innerInsertLog(httpLogEntity: HttpLogEntity): Long

    @Query(value = "SELECT * FROM http_log WHERE id = :columnId")
    abstract fun innerGetLog(columnId: Long): HttpLogEntity?

    @Update
    abstract fun innerUpdateLog(httpLogEntity: HttpLogEntity)

    @Query(value = "DELETE FROM log WHERE time < :time")
    abstract fun deleteOverLifeData(time: Long)

}