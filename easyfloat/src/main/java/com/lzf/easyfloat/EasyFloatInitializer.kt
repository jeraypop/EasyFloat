package com.lzf.easyfloat

import android.app.Application
import android.content.BroadcastReceiver
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.database.Cursor
import android.net.Uri
import com.lzf.easyfloat.core.TouchUtils
import com.lzf.easyfloat.utils.DisplayUtils
import com.lzf.easyfloat.utils.LifecycleUtils

/**
 * @author: liuzhenfeng
 * @github：https://github.com/princekin-f
 * @function: 通过内容提供者的上下文，进行生命周期回调的初始化
 * @date: 2020/10/23  13:41
 */
class EasyFloatInitializer : ContentProvider() {
    private var orientationReceiver: OrientationReceiver? = null

    override fun onCreate(): Boolean {
        // 注册屏幕方向监听广播
        registerOrientationReceiver(context!!.applicationContext)
        LifecycleUtils.setLifecycleCallbacks(context!!.applicationContext as Application)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun shutdown() {
        super.shutdown()
        context?.let {
            unregisterOrientationReceiver(it)
        }
    }
    private fun registerOrientationReceiver(context: Context) {
        orientationReceiver = OrientationReceiver()
        val filter = IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        context.registerReceiver(orientationReceiver, filter)
    }

    private fun unregisterOrientationReceiver(context: Context) {
        orientationReceiver?.let {
            context.unregisterReceiver(it)
            orientationReceiver = null
        }
    }

    inner class OrientationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (Intent.ACTION_CONFIGURATION_CHANGED == intent?.action) {
                // 获取当前屏幕方向
                val orientation = context?.resources?.configuration?.orientation
                when (orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        // 横屏状态
                        // 在这里处理横屏逻辑
                        TouchUtils.parentWidth = DisplayUtils.getScreenWidth(context)
                        TouchUtils.parentHeight = DisplayUtils.getScreenHeight(context)
                    }
                    Configuration.ORIENTATION_PORTRAIT -> {
                        // 竖屏状态
                        // 在这里处理竖屏逻辑
                        TouchUtils.parentWidth = DisplayUtils.getScreenWidth(context)
                        TouchUtils.parentHeight = DisplayUtils.getScreenHeight(context)
                    }
                    Configuration.ORIENTATION_UNDEFINED -> {
                        // 未定义方向
                    }
                }
            }
        }
    }

}