package com.gialen.sentrydemo

import android.content.Context
import android.os.Process
import io.sentry.Sentry
import java.util.*

class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null
    private var mContext: Context? = null
    // 保存手机信息和异常信息
    private val mMessage: Map<String, String> = HashMap()

    /**
     * 初始化默认异常捕获
     *
     * @param context context
     */
    fun init(context: Context?) {
        mContext = context
        // 获取默认异常处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        // 将此类设为默认异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        if (!handleException(e)) { // 未经过人为处理,则调用系统默认处理异常,弹出系统强制关闭的对话框
            if (mDefaultHandler != null) {
                mDefaultHandler!!.uncaughtException(t, e)
            }
        } else { // 已经人为处理,系统自己退出
            try {
                Thread.sleep(1000)
            } catch (e1: InterruptedException) {
                e1.printStackTrace()
            }
            //退出程序
//退出JVM(java虚拟机),释放所占内存资源,0表示正常退出(非0的都为异常退出)
            System.exit(0)
            //从操作系统中结束掉当前程序的进程
            Process.killProcess(Process.myPid())
        }
    }

    /**
     * 是否人为捕获异常
     *
     * @param e Throwable
     * @return true:已处理 false:未处理
     */
    private fun handleException(e: Throwable?): Boolean {
        if (e == null) { // 异常是否为空
            return false
        }
        Sentry.capture(e)
        return false
    }

    companion object {
        private var sInstance: CrashHandler? = null
        val instance: CrashHandler?
            get() {
                if (sInstance == null) {
                    synchronized(CrashHandler::class.java) {
                        if (sInstance == null) {
                            synchronized(CrashHandler::class.java) { sInstance = CrashHandler() }
                        }
                    }
                }
                return sInstance
            }
    }
}