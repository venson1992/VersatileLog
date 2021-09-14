package com.venson.versatile.log.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tencent.wcdb.database.SQLiteCipherSpec
import com.tencent.wcdb.room.db.WCDBOpenHelperFactory
import com.venson.versatile.log.LogEncryptJNI
import com.venson.versatile.log.database.dao.LogDao

abstract class LogDatabase : RoomDatabase() {

    abstract fun logDao(): LogDao

    companion object {
        private const val DATABASE_NAME = "dp_versatile_log"

        @Volatile
        private var instance: LogDatabase? = null

        @JvmStatic
        fun getInstance(applicationContext: Context) = instance ?: synchronized(this) {
            instance ?: let {
                // 指定加密方式，使用默认加密可以省略
                val cipherSpec: SQLiteCipherSpec = SQLiteCipherSpec()
                    .setPageSize(4096)
                    .setKDFIteration(64000)
                val password = LogEncryptJNI.readEncrypt(applicationContext.packageName)
                val factory = WCDBOpenHelperFactory()
                    .passphrase(password.toByteArray())// 指定加密DB密钥，非加密DB去掉此行
                    .cipherSpec(cipherSpec)// 指定加密方式，使用默认加密可以省略
                    .writeAheadLoggingEnabled(true)// 打开WAL以及读写并发，可以省略让Room决定是否要打开
                    .asyncCheckpointEnabled(true)// 打开异步Checkpoint优化，不需要可以省略
                Room.databaseBuilder(applicationContext, LogDatabase::class.java, DATABASE_NAME)
                    .openHelperFactory(factory)
                    .build()
                    .also {
                        instance = it
                    }
            }
        }

    }
}