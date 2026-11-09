package com.sbiqr.papa

import android.app.Application

/**
 * SBI QR
 * Version: v1.00-02
 *
 * アプリ全体の初期化を行うApplicationクラス。
 */
class MainApplication : Application() {

 override fun onCreate() {
 super.onCreate()

 // クラッシュ記録機能を登録
 CrashHandler.install(this)
 }
}