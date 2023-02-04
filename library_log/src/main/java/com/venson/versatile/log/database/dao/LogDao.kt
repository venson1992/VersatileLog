package com.venson.versatile.log.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.venson.versatile.log.VLog
import com.venson.versatile.log.database.entity.LogEntity
import com.venson.versatile.log.work.DefaultExecutorSupplier
import java.util.*

@Dao
abstract class LogDao {

    /**
     * 插入日志
     */
    fun insertLog(tag: String?, level: Int, head: String?, msg: String?) {
        DefaultExecutorSupplier.instance.forBackgroundTasks().execute {
            try {
                innerInsertLog(
                    LogEntity(
                        null,
                        System.currentTimeMillis(),
                        tag,
                        level.toString(),
                        when (level) {
                            VLog.JSON -> {
                                "json"
                            }
                            VLog.XML -> {
                                "xml"
                            }
                            else -> {
                                ""
                            }
                        },
                        head,
                        msg
                    )
                )
            } catch (e: Exception) {
                VLog.callException(e)
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun innerInsertLog(logEntity: LogEntity)

    /**
     * 根据tag查询
     */
    fun logList(
        tag: String?,
        isIgnoreCase: Boolean = true,
        level: String,
        type: String,
        time: Long
    ): List<LogEntity>? {
        val leveArray = level.split("|").toTypedArray()
        val typeArray = type.split("|").toTypedArray()
        if (tag.isNullOrEmpty()) {
            return innerLogList(leveArray, typeArray, time)
        }
        if (isIgnoreCase) {
            return innerLogListByTagWithIgnoreCase(
                "%$tag%",
                "%${tag.lowercase(Locale.getDefault())}%",
                "%${tag.uppercase(Locale.getDefault())}%",
                leveArray,
                typeArray,
                time
            )
        }
        return innerLogListByTag("%$tag%", leveArray, typeArray, time)
    }

    @Query(value = "SELECT * FROM log WHERE level IN (:level) AND type IN (:type) AND time >= :time")
    abstract fun innerLogList(
        level: Array<out String>,
        type: Array<out String>,
        time: Long
    ): List<LogEntity>?

    @Query(value = "SELECT * FROM log WHERE tag LIKE :tag AND level IN (:level) AND type IN (:type) AND time >= :time")
    abstract fun innerLogListByTag(
        tag: String,
        level: Array<out String>,
        type: Array<out String>,
        time: Long
    ): List<LogEntity>?

    @Query(value = "SELECT * FROM log WHERE (tag LIKE :tag OR tag LIKE :tagLowerCase OR tag LIKE :tagUpperCase) AND level IN (:level) AND type IN (:type) AND time >= :time")
    abstract fun innerLogListByTagWithIgnoreCase(
        tag: String,
        tagLowerCase: String,
        tagUpperCase: String,
        level: Array<out String>,
        type: Array<out String>,
        time: Long
    ): List<LogEntity>?

    @Query(value = "DELETE FROM http_log WHERE startTime < :time")
    abstract fun deleteOverLifeData(time: Long)

}