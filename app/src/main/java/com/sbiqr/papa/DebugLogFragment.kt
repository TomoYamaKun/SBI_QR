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
 * アプリ内部デバッグログ画面。
 */
class DebugLogFragment : Fragment() {

 override fun onCreateView(
 inflater: LayoutInflater,
 container: ViewGroup?,
 savedInstanceState: Bundle?
 ): View {
 return inflater.inflate(
 R.layout.fragment_debug_log,
 container,
 false
 )
 }
}