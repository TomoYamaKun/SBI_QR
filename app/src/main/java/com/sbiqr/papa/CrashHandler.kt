package com.sbiqr.papa

import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * SBI QR
 * Version: v1.00-02
 *
 * アプリの未処理例外を記録する。
 *
 * 保存先：
 * アプリ内部ストレージ/last_crash_log.txt
 *
 * パスワードや認証トークンなどの秘密情報は
 * このクラスから意図的に記録しない。
 */
object CrashHandler : Thread.UncaughtExceptionHandler {

 private const val CRASH_FILE_NAME = "last_crash_log.txt"

 private lateinit var appContext: Context

 private var previousHandler: Thread.UncaughtExceptionHandler? = null

 @Volatile
 private var handlingCrash = false

 /**
 * クラッシュハンドラーを登録する。
 */
 fun install(context: Context) {
 appContext = context.applicationContext

 previousHandler = Thread.getDefaultUncaughtExceptionHandler()

 Thread.setDefaultUncaughtExceptionHandler(this)
 }

 /**
 * 未処理例外が発生したときに呼ばれる。
 */
 override fun uncaughtException(
 thread: Thread,
 throwable: Throwable
 ) {
 if (!handlingCrash) {
 handlingCrash = true

 try {
 saveCrashLog(thread, throwable)
 } catch (_: Throwable) {
 // クラッシュ記録中の二次例外は外へ出さない
 }
 }

 // Android標準の終了処理へ引き継ぐ
 val handler = previousHandler

 if (handler != null && handler !== this) {
 handler.uncaughtException(thread, throwable)
 }
 }

 /**
 * クラッシュ情報をファイルに保存する。
 */
 @Synchronized
 private fun saveCrashLog(
 thread: Thread,
 throwable: Throwable
 ) {
 val file = File(appContext.filesDir, CRASH_FILE_NAME)

 val stackWriter = StringWriter()
 val printWriter = PrintWriter(stackWriter)

 throwable.printStackTrace(printWriter)
 printWriter.flush()

 val timeFormat = SimpleDateFormat(
 "yyyy-MM-dd HH:mm:ss.SSS",
 Locale.JAPAN
 )

 timeFormat.timeZone = TimeZone.getTimeZone("Asia/Tokyo")

 val crashText = buildString {
 appendLine("SBI QR Crash Report")
 appendLine("Version: ${AppVersion.VERSION_NAME}")
 appendLine("Time: ${timeFormat.format(Date())}")
 appendLine("Thread: ${thread.name}")
 appendLine()
 appendLine("Exception:")
 appendLine(stackWriter.toString())
 }

 file.writeText(crashText, Charsets.UTF_8)
 }

 /**
 * 保存済みクラッシュログがあるか確認する。
 */
 fun hasCrashLog(context: Context): Boolean {
 return File(
 context.applicationContext.filesDir,
 CRASH_FILE_NAME
 ).exists()
 }

 /**
 * 保存済みクラッシュログを読み込む。
 */
 fun readCrashLog(context: Context): String {
 val file = File(
 context.applicationContext.filesDir,
 CRASH_FILE_NAME
 )

 return try {
 if (file.exists()) {
 file.readText(Charsets.UTF_8)
 } else {
 ""
 }
 } catch (_: Exception) {
 "クラッシュログを読み込めませんでした。"
 }
 }

 /**
 * 保存済みクラッシュログを削除する。
 */
 fun clearCrashLog(context: Context): Boolean {
 val file = File(
 context.applicationContext.filesDir,
 CRASH_FILE_NAME
 )

 return try {
 !file.exists() || file.delete()
 } catch (_: Exception) {
 false
 }
 }
}