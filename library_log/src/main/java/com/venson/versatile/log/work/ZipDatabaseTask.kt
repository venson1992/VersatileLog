package com.venson.versatile.log.work

import androidx.annotation.FloatRange
import com.venson.versatile.log.VLog
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * 压缩数据库文件任务
 */
class ZipDatabaseTask(
    private val packageName: String,
    private val targetFilePath: String?,
    private val progressListener: VLog.OnDatabaseFileZipProgressListener
) : Runnable {

    companion object {
        private const val BUFF_SIZE = 1024 * 1024 // 1M Byte
    }

    override fun run() {
        /*
        获得数据库文件路径
         */
        val path = VLog.logDatabasePath(packageName)
        val databaseFile = try {
            File(path)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        val folderPath = databaseFile?.parent
        if (folderPath.isNullOrEmpty()) {
            onZipFailure(Throwable("数据库文件不存在"))
            return
        }
        /*
        设置目标压缩文件路径
         */
        var zipFilePath: String = if (targetFilePath.isNullOrEmpty()) {
            folderPath + "/database_${System.currentTimeMillis()}.zip"
        } else {
            targetFilePath
        }
        if (!zipFilePath.endsWith(".zip")) {
            zipFilePath = "$zipFilePath.zip"
        }
        zipFilePath = zipFilePath.replace("//", "/")
        /*
        需要压缩的数据库文件
         */
        val pathList = mutableListOf<String>()
        pathList.add(path)
        pathList.add("$path-shm")
        pathList.add("$path-wal")
        zipFiles(zipFilePath, path, "$path-shm", "$path-wal")
    }

    /**
     * 压缩目标文件
     * @param targetFilePath 压缩后文件路径
     * @param files 数据库源文件
     */
    private fun zipFiles(targetFilePath: String, vararg files: String) {
        val fileList = mutableListOf<File>()
        var totalLength = 0L
        for (index in files.indices) {
            val file = try {
                File(files[index])
            } catch (e: Exception) {
                onZipFailure(Throwable("数据库文件不存在，${e.cause}"))
                break
            }
            fileList.add(file)
            totalLength += file.length()
        }
        zipFiles(targetFilePath, fileList, totalLength)
    }

    /**
     * 压缩目标文件
     * @param targetFilePath 压缩后文件路径
     * @param files 数据库源文件
     */
    private fun zipFiles(targetFilePath: String, files: List<File>, totalLength: Long) {
        try {
            val zipFile = File(targetFilePath)
            if (zipFile.exists()) {
                zipFile.delete()
            }
            zipFile.parentFile?.let { directory ->
                if (!directory.exists()) {
                    directory.mkdirs()
                }
            }
            zipFile.createNewFile()
            val zipOutputStream = ZipOutputStream(
                BufferedOutputStream(FileOutputStream(zipFile), BUFF_SIZE)
            )
            var completeLength = 0L
            for (resFile in files) {
                val result: String = try {
                    zipFile(resFile, zipOutputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                    e.toString()
                }
                if (result.isBlank()) {
                    completeLength += resFile.length()
                    onZipProgress(completeLength.times(1F).div(totalLength))
                    continue
                }
                onZipFailure(Throwable(result))
                break
            }
            zipOutputStream.close()
            onZipSuccess(zipFile)
        } catch (e: Exception) {
            onZipFailure(e)
        }
    }

    /**
     * 将文件压缩近目标文件
     * @param resFile 源文件
     * @param zipOutputStream 目标文件输出流
     */
    private fun zipFile(resFile: File, zipOutputStream: ZipOutputStream): String {
        try {
            val buffer = ByteArray(BUFF_SIZE)
            val `in` = BufferedInputStream(
                FileInputStream(resFile),
                BUFF_SIZE
            )
            val zipEntryName = String(
                resFile.name.toByteArray(
                    charset("8859_1")
                ),
                charset("GB2312")
            )
            zipOutputStream.putNextEntry(ZipEntry(zipEntryName))
            var realLength: Int
            while (`in`.read(buffer).also { realLength = it } != -1) {
                zipOutputStream.write(buffer, 0, realLength)
            }
            `in`.close()
            zipOutputStream.flush()
            zipOutputStream.closeEntry()
            return ""
        } catch (e: FileNotFoundException) {
            return e.toString()
        } catch (e: IOException) {
            return e.toString()
        } catch (e: Exception) {
            return e.toString()
        }
    }

    private fun onZipProgress(@FloatRange(from = 0.0, to = 1.0) progress: Float) {
        DefaultExecutorSupplier.instance.forMainThreadTasks().execute {
            progressListener.onProgress(progress)
        }
    }

    private fun onZipFailure(throwable: Throwable) {
        DefaultExecutorSupplier.instance.forMainThreadTasks().execute {
            progressListener.onFailure(throwable)
        }
    }

    private fun onZipSuccess(zipFile: File) {
        DefaultExecutorSupplier.instance.forMainThreadTasks().execute {
            progressListener.onSuccess(zipFile)
        }
    }
}