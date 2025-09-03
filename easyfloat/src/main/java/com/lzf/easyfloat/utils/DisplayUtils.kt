package com.lzf.easyfloat.utils

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.Surface
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.annotation.RequiresApi
import com.lzf.easyfloat.permission.rom.RomUtils

/**
 * @author: liuzhenfeng
 * @function: 屏幕显示相关工具类
 * @date: 2019-05-23  15:23
 */
object DisplayUtils {

    private const val TAG = "DisplayUtils--->"

    fun px2dp(context: Context, pxVal: Float): Int {
        val density = context.resources.displayMetrics.density
        return (pxVal / density + 0.5f).toInt()
    }

    fun dp2px(context: Context, dpVal: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dpVal * density + 0.5f).toInt()
    }
    fun px2sp(context: Context, pxValue: Float): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            px2spAndroid12(context, pxValue)
        } else {
            px2spLegacy(context, pxValue)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun px2spAndroid12(context: Context, pxValue: Float): Int {
        // 获取字体缩放比例
        val fontScale = context.resources.configuration.fontScale

        // 使用 fontScale 来转换 px 到 sp
        return (pxValue / fontScale + 0.5f).toInt()
    }

    fun px2spLegacy(context: Context, pxValue: Float): Int {
        // 获取字体缩放比例
        val fontScale = context.resources.displayMetrics.scaledDensity

        // 使用 scaledDensity 来转换 px 到 sp
        return (pxValue / fontScale + 0.5f).toInt()
    }

    fun sp2px(context: Context, spValue: Float): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            sp2pxAndroid12(context, spValue)
        } else {
            sp2pxLegacy(context, spValue)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun sp2pxAndroid12(context: Context, spValue: Float): Int {
        // 获取字体缩放比例
        val fontScale = context.resources.configuration.fontScale

        // 使用 fontScale 来转换 sp 到 px
        return (spValue * fontScale + 0.5f).toInt()
    }

    fun sp2pxLegacy(context: Context, spValue: Float): Int {
        // 获取字体缩放比例
        val fontScale = context.resources.displayMetrics.scaledDensity

        // 使用 scaledDensity 来转换 sp 到 px
        return (spValue * fontScale + 0.5f).toInt()
    }

    fun getDeviceRotation(context: Context): Int {
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display: Display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)

        return display.rotation
    }

    fun isDeviceLandscape(context: Context): Boolean {
        val rotation = getDeviceRotation(context)

        // 判断设备的旋转角度，分别为 90° 或 270° 表示横屏
        return rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270
    }

    fun isDevicePortrait(context: Context): Boolean {
        val rotation = getDeviceRotation(context)

        // 判断设备的旋转角度，0° 或 180° 表示竖屏
        return rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180
    }

    /**
     * 获取屏幕宽度（显示宽度，横屏的时候可能会小于物理像素值）
     */
    fun getScreenWidth(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getScreenWidthAndroid12(context)
        } else {
            getScreenWidthLegacy(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getScreenWidthAndroid12(context: Context): Int {
        // 使用 WindowMetrics 获取屏幕信息
        val windowMetrics: WindowMetrics = context.getSystemService(WindowManager::class.java).currentWindowMetrics

        // 获取显示区域宽度
        val bounds = windowMetrics.bounds
        val screenWidth = bounds.width()

        // 获取设备方向，如果是横屏，减去导航栏的高度
//        return if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            screenWidth
//        } else {
//            screenWidth - getNavigationBarCurrentHeight(context)
//        }
        return if (isDevicePortrait(context)) {
            screenWidth
        } else {
            screenWidth - getNavigationBarCurrentHeight(context)
        }
    }

    fun getScreenWidthLegacy(context: Context): Int {
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        manager.defaultDisplay.getRealMetrics(metrics)

        return if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            metrics.widthPixels
        } else {
            metrics.widthPixels - getNavigationBarCurrentHeight(context)
        }
    }

  //===============================================================


    /**
     * 获取屏幕高度（物理像素值的高度）
     */
    fun getScreenHeight(context: Context) = getScreenSize(context).y

    /**
     * 获取屏幕宽高
     */
    fun getScreenSize(context: Context): Point {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getScreenSizeAndroid12(context)
        } else {
            getScreenSizeLegacy(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getScreenSizeAndroid12(context: Context): Point {
        val windowMetrics: WindowMetrics = context.getSystemService(WindowManager::class.java).currentWindowMetrics
        val bounds = windowMetrics.bounds

        // 获取屏幕的宽高
        return Point(bounds.width(), bounds.height())
    }

    fun getScreenSizeLegacy(context: Context): Point {
        val windowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)

        return size
    }


    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getStatusBarHeightAndroid12(context)
        } else {
            getStatusBarHeightLegacy(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getStatusBarHeightAndroid12(context: Context): Int {
        // 获取 WindowInsets 获取状态栏高度
        val insets = context.getSystemService(WindowManager::class.java).currentWindowMetrics.windowInsets

        // 获取状态栏高度
        return insets?.isVisible(WindowInsets.Type.statusBars())?.let {
            insets.getInsets(WindowInsets.Type.statusBars()).top
        } ?: 0
    }

    fun getStatusBarHeightLegacy(context: Context): Int {
        // 使用资源方式获取状态栏高度
        var result = 0
        val resources = context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }

        Log.e("状态栏高度", "getStatusBarHeight: $result")
        return result
    }



    fun statusBarHeight(view: View) = getStatusBarHeight(view.context.applicationContext)

    /**
     * 获取导航栏真实的高度（可能未显示）
     */
    fun getNavigationBarHeight(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getNavigationBarHeightAndroid12(context)
        } else {
            getNavigationBarHeightLegacy(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getNavigationBarHeightAndroid12(context: Context): Int {
        // 获取 WindowInsets 获取导航栏高度
        val insets = context.getSystemService(WindowManager::class.java).currentWindowMetrics.windowInsets

        // 获取导航栏高度，确保其可见
        return if (insets?.isVisible(WindowInsets.Type.navigationBars()) == true) {
            insets.getInsets(WindowInsets.Type.navigationBars()).bottom
        } else {
            0
        }
    }

    fun getNavigationBarHeightLegacy(context: Context): Int {
        // 使用资源方式获取导航栏高度
        var result = 0
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 获取导航栏当前的高度
     */
    fun getNavigationBarCurrentHeight(context: Context) =
        if (hasNavigationBar(context)) getNavigationBarHeight(context) else 0



    /**
     * 判断虚拟导航栏是否显示
     *
     * @param context 上下文对象
     * @return true(显示虚拟导航栏)，false(不显示或不支持虚拟导航栏)
     */
    fun hasNavigationBar(context: Context) = when {
        getNavigationBarHeight(context) == 0 -> false
        RomUtils.checkIsHuaweiRom() && isHuaWeiHideNav(context) -> false
        RomUtils.checkIsMiuiRom() && isMiuiFullScreen(context) -> false
        RomUtils.checkIsVivoRom() && isVivoFullScreen(context) -> false
        else -> isHasNavigationBar(context)
    }

    /**
     * 不包含导航栏的有效高度（没有导航栏，或者已去除导航栏的高度）
     */
    fun rejectedNavHeight(context: Context): Int {
        val point = getScreenSize(context)
        if (point.x > point.y) return point.y
        return point.y - getNavigationBarCurrentHeight(context)
    }

    /**
     * 华为手机是否隐藏了虚拟导航栏
     * @return true 表示隐藏了，false 表示未隐藏
     */
    private fun isHuaWeiHideNav(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Settings.System.getInt(context.contentResolver, "navigationbar_is_min", 0)
        } else {
            Settings.Global.getInt(context.contentResolver, "navigationbar_is_min", 0)
        } != 0

    /**
     * 小米手机是否开启手势操作
     * @return false 表示使用的是虚拟导航键(NavigationBar)， true 表示使用的是手势， 默认是false
     */
    private fun isMiuiFullScreen(context: Context) =
        Settings.Global.getInt(context.contentResolver, "force_fsg_nav_bar", 0) != 0

    /**
     * Vivo手机是否开启手势操作
     * @return false 表示使用的是虚拟导航键(NavigationBar)， true 表示使用的是手势， 默认是false
     */
    private fun isVivoFullScreen(context: Context): Boolean =
        Settings.Secure.getInt(context.contentResolver, "navigation_gesture_on", 0) != 0

    /**
     * 其他手机根据屏幕真实高度与显示高度是否相同来判断
     */
    private fun isHasNavigationBar(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isHasNavigationBarAndroid12(context)
        } else {
            isHasNavigationBarLegacy(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun isHasNavigationBarAndroid12(context: Context): Boolean {
        // 获取 WindowInsets 获取导航栏信息
        val insets = context.getSystemService(WindowManager::class.java).currentWindowMetrics.windowInsets

        // 判断导航栏是否可见
        return insets?.isVisible(WindowInsets.Type.navigationBars()) == true
    }

    private fun isHasNavigationBarLegacy(context: Context): Boolean {
        // 获取真实显示的高度和宽度
        val windowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
        val d = windowManager.defaultDisplay

        val realDisplayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics)
        }
        val realHeight = realDisplayMetrics.heightPixels
        val realWidth = realDisplayMetrics.widthPixels

        // 获取显示区域的高度和宽度
        val displayMetrics = DisplayMetrics()
        d.getMetrics(displayMetrics)
        val displayHeight = displayMetrics.heightPixels
        val displayWidth = displayMetrics.widthPixels

        // 如果显示区域高度加上导航栏高度大于实际显示高度，则认为没有导航栏
        if (displayHeight + getNavigationBarHeight(context) > realHeight) return false

        // 判断显示区域的宽度和高度差异来确认是否有导航栏
        return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
    }

    fun isLandscape(context: Context): Boolean {
        return ((context as Activity).application.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
    }



}