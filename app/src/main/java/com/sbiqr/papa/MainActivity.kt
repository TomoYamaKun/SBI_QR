package com.sbiqr.papa

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout

/**
 * SBI QR
 * Version: v1.00-02
 *
 * メイン画面と5つのタブを管理する。
 * 起動時にクラッシュログがあれば緊急画面を表示する。
 */
class MainActivity : AppCompatActivity() {

 companion object {
 private const val KEY_SELECTED_TAB = "selected_tab"
 }

 private var selectedTab = 0

 override fun onCreate(savedInstanceState: Bundle?) {
 super.onCreate(savedInstanceState)

 // 前回クラッシュしていた場合は緊急画面を優先する
 if (CrashHandler.hasCrashLog(this)) {
 showCrashScreen()
 return
 }

 selectedTab = savedInstanceState?.getInt(KEY_SELECTED_TAB) ?: 0
 showMainScreen(selectedTab)
 }

 override fun onSaveInstanceState(outState: Bundle) {
 outState.putInt(KEY_SELECTED_TAB, selectedTab)
 super.onSaveInstanceState(outState)
 }

 /**
 * 通常の5タブ画面を表示する。
 */
 private fun showMainScreen(initialTab: Int) {
 setContentView(R.layout.activity_main)

 val tabLayout = findViewById<TabLayout>(R.id.main_tabs)

 val tabNames = listOf(
 "QR",
 "QR履歴",
 "通信ログ",
 "デバッグ",
 "設定"
 )

 tabNames.forEach { name ->
 tabLayout.addTab(
 tabLayout.newTab().setText(name),
 false
 )
 }

 tabLayout.addOnTabSelectedListener(
 object : TabLayout.OnTabSelectedListener {

 override fun onTabSelected(tab: TabLayout.Tab) {
 selectedTab = tab.position
 showFragment(selectedTab)
 }

 override fun onTabUnselected(tab: TabLayout.Tab) {
 }

 override fun onTabReselected(tab: TabLayout.Tab) {
 selectedTab = tab.position
 showFragment(selectedTab)
 }
 }
 )

 val safeIndex = initialTab.coerceIn(0, tabNames.lastIndex)
 tabLayout.selectTab(tabLayout.getTabAt(safeIndex))
 }

 /**
 * 選択されたタブのFragmentを表示する。
 */
 private fun showFragment(index: Int) {
 val fragment: Fragment = when (index) {
 0 -> QRFragment()
 1 -> HistoryFragment()
 2 -> CommunicationLogFragment()
 3 -> DebugLogFragment()
 4 -> SettingsFragment()
 else -> QRFragment()
 }

 supportFragmentManager.beginTransaction()
 .replace(R.id.fragment_container, fragment)
 .commit()
 }

 /**
 * XMLレイアウトに依存しない緊急画面を表示する。
 */
 private fun showCrashScreen() {
 val root = LinearLayout(this).apply {
 orientation = LinearLayout.VERTICAL
 setPadding(dp(20), dp(20), dp(20), dp(20))
 setBackgroundColor(0xFFFFF3F3.toInt())
 }

 val title = TextView(this).apply {
 text = "SBI QR - 緊急画面"
 textSize = 24f
 typeface = Typeface.DEFAULT_BOLD
 setTextColor(0xFFB00020.toInt())
 }

 val description = TextView(this).apply {
 text = "前回の起動時にアプリの異常終了を検出しました。"
 textSize = 16f
 setPadding(0, dp(12), 0, dp(12))
 }

 val logView = TextView(this).apply {
 text = CrashHandler.readCrashLog(this@MainActivity)
 textSize = 12f
 setTextIsSelectable(true)
 setPadding(dp(10), dp(10), dp(10), dp(10))
 setBackgroundColor(0xFFFFFFFF.toInt())
 }

 val scrollView = ScrollView(this).apply {
 addView(
 logView,
 ScrollView.LayoutParams(
 ViewGroup.LayoutParams.MATCH_PARENT,
 ViewGroup.LayoutParams.WRAP_CONTENT
 )
 )
 }

 val copyButton = Button(this).apply {
 text = "クラッシュログをコピー"
 setOnClickListener {
 val clipboard = getSystemService(
 Context.CLIPBOARD_SERVICE
 ) as ClipboardManager

 clipboard.setPrimaryClip(
 ClipData.newPlainText(
 "SBI QR Crash Log",
 logView.text
 )
 )

 text = "コピーしました"
 }
 }

 val clearButton = Button(this).apply {
 text = "クラッシュログを消去"
 setOnClickListener {
 val cleared = CrashHandler.clearCrashLog(this@MainActivity)

 logView.text = if (cleared) {
 "クラッシュログは消去されました。"
 } else {
 "クラッシュログを消去できませんでした。"
 }

 copyButton.isEnabled = !cleared
 }
 }

 val normalButton = Button(this).apply {
 text = "通常画面へ復帰"
 setOnClickListener {
 CrashHandler.clearCrashLog(this@MainActivity)
 selectedTab = 0
 showMainScreen(0)
 }
 }

 root.addView(title)
 root.addView(description)

 root.addView(
 scrollView,
 LinearLayout.LayoutParams(
 ViewGroup.LayoutParams.MATCH_PARENT,
 0,
 1f
 )
 )

 root.addView(copyButton)
 root.addView(clearButton)
 root.addView(normalButton)

 setContentView(root)
 }

 private fun dp(value: Int): Int {
 return (value * resources.displayMetrics.density).toInt()
 }
}