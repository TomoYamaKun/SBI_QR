package com.sbiqr.papa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * SBI QR
 * Version: v1.00-02
 *
 * サーバー接続・アプリ設定画面。
 */
class SettingsFragment : Fragment() {

 override fun onCreateView(
 inflater: LayoutInflater,
 container: ViewGroup?,
 savedInstanceState: Bundle?
 ): View {
 return inflater.inflate(
 R.layout.fragment_settings,
 container,
 false
 )
 }
}