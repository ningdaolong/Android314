package cn.ning.demo

import android.app.Application
import cn.ning.screenAdaptation.ScreenAdapterTools

/**
 * Created by MrNing
 * on 2018/7/26
 * Email Mr_Ning314@163.com
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ScreenAdapterTools.init(this)
    }
}