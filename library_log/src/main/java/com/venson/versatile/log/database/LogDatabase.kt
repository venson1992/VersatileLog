package com.venson.versatile.log.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tencent.wcdb.database.SQLiteCipherSpec
import com.tencent.wcdb.room.db.WCDBOpenHelperFactory
import com.venson.versatile.log.LogEncryptJNI
import com.venson.versatile.log.VLog
import com.venson.versatile.log.database.dao.HttpLogDao
import com.venson.versatile.log.database.dao.LogDao
import com.venson.versatile.log.database.entity.HttpLogEntity
import com.venson.versatile.log.database.entity.LogEntity
import com.venson.versatile.log.work.DefaultExecutorSupplier

@Database(
    entities = [LogEntity::class, HttpLogEntity::class],
    version = 4
)
abstract class LogDatabase : RoomDatabase() {

    abstract fun logDao(): LogDao

    abstract fun httpLogDao(): HttpLogDao

    companion object {
        private const val DATABASE_NAME = "db_versatile_log"

        @Volatile
        private var instance: LogDatabase? = null

        @Volatile
        private var externalInstance: LogDatabase? = null

        @JvmStatic
        fun getInstance(applicationContext: Context) = instance ?: synchronized(this) {
            instance ?: let {
                // 指定加密方式，使用默认加密可以省略
                val cipherSpec: SQLiteCipherSpec = SQLiteCipherSpec()
                    .setPageSize(4096)
                    .setKDFIteration(64000)
                val password = LogEncryptJNI.readEncrypt(
                    VLog.encryptedKey() ?: applicationContext.packageName
                )
                val factory = WCDBOpenHelperFactory()
                    .passphrase(password.toByteArray())// 指定加密DB密钥，非加密DB去掉此行
                    .cipherSpec(cipherSpec)// 指定加密方式，使用默认加密可以省略
                    .writeAheadLoggingEnabled(true)// 打开WAL以及读写并发，可以省略让Room决定是否要打开
                    .asyncCheckpointEnabled(true)// 打开异步Checkpoint优化，不需要可以省略
                val builder = Room.databaseBuilder(
                    applicationContext,
                    LogDatabase::class.java,
                    getDatabasePath(applicationContext)
                )
                builder.openHelperFactory(factory)
                    .addMigrations(mMigration1_2, mMigration2_3, mMigration3_4)
                    .build()
                    .also {
                        instance = it
                        /*
                        删除本地化时效外的数据
                         */
                        DefaultExecutorSupplier.instance.forBackgroundTasks().execute {
                            val time = VLog.logStorageLifeInDay() * 24 * 60 * 60 * 1000L
                            val current = System.currentTimeMillis()
                            it.logDao().deleteOverLifeData(current - time)
                            it.httpLogDao().deleteOverLifeData(current - time)
                        }
                    }
            }
        }

        @JvmStatic
        fun getExternalInstance(
            context: Context,
            packageName: String,
            filePath: String
        ): LogDatabase = externalInstance ?: synchronized(this) {
            externalInstance ?: let {
                // 指定加密方式，使用默认加密可以省略
                val cipherSpec: SQLiteCipherSpec = SQLiteCipherSpec()
                    .setPageSize(4096)
                    .setKDFIteration(64000)
                val password = LogEncryptJNI.readEncrypt(
                    VLog.encryptedKey() ?: packageName
                )
                val factory = WCDBOpenHelperFactory()
                    .passphrase(password.toByteArray())// 指定加密DB密钥，非加密DB去掉此行
                    .cipherSpec(cipherSpec)// 指定加密方式，使用默认加密可以省略
                    .writeAheadLoggingEnabled(true)// 打开WAL以及读写并发，可以省略让Room决定是否要打开
                    .asyncCheckpointEnabled(true)// 打开异步Checkpoint优化，不需要可以省略
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    LogDatabase::class.java,
                    filePath
                )
                builder.openHelperFactory(factory)
                    .addMigrations(mMigration1_2, mMigration2_3, mMigration3_4)
                    .build()
                    .also {
                        externalInstance = it
                        /*
                        删除本地化时效外的数据
                         */
                        DefaultExecutorSupplier.instance.forBackgroundTasks().execute {
                            val time = VLog.logStorageLifeInDay() * 24 * 60 * 60 * 1000L
                            val current = System.currentTimeMillis()
                            it.logDao().deleteOverLifeData(current - time)
                            it.httpLogDao().deleteOverLifeData(current - time)
                        }
                    }
            }
        }

        /**
         * 获取数据库路径
         * @param applicationContext 上下文
         * @param packageName 查看指定应用的数据库路径，默认当前应用的包名
         */
        @JvmStatic
        fun getDatabasePath(applicationContext: Context, packageName: String? = null): String {
            var path: String = (applicationContext.externalCacheDir?.path
                ?: applicationContext.cacheDir.path).let {
                applicationContext.packageName.let { packageName ->
                    it.substring(0, it.indexOf(packageName) + packageName.length)
                }
            }
            /*
            替换成需要查看的应用数据库路径
             */
            if (!packageName.isNullOrEmpty()) {
                path = path.replace(applicationContext.packageName, packageName)
            }
            return "$path/database/$DATABASE_NAME"
        }

        private val mMigration1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `http_log` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "`request` TEXT, " +
                            "`response` TEXT, " +
                            "`startTime` INTEGER NOT NULL, " +
                            "`endTime` INTEGER NOT NULL, " +
                            "`duration` INTEGER NOT NULL" +
                            ")"
                )
            }
        }

        private val mMigration2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE http_log ADD COLUMN url TEXT")
                database.execSQL("ALTER TABLE http_log ADD COLUMN method TEXT")
            }
        }

        private val mMigration3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE http_log ADD COLUMN contentType TEXT")
            }

        }
    }
}