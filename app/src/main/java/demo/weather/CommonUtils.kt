package demo.weather

import android.util.Log


fun logit(msg: Any? = "...") {
    if (BuildConfig.DEBUG) {
        val trace: StackTraceElement? = Thread.currentThread().stackTrace[3]
        val lineNumber = trace?.lineNumber
        val methodName = trace?.methodName
        val className = trace?.fileName?.replaceAfter(".", "")?.replace(".", "")
        Log.d("Line $lineNumber", "$className::$methodName() -> $msg")
    }
}