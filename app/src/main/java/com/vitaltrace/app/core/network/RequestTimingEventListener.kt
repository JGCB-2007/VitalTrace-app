package com.vitaltrace.app.core.network

import android.util.Log
import okhttp3.Call
import okhttp3.EventListener
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class RequestTimingEventListener : EventListener() {
    private val startedAt = ConcurrentHashMap<Call, Long>()

    override fun callStart(call: Call) {
        startedAt[call] = System.nanoTime()
    }

    override fun callEnd(call: Call) = logDuration(call, "completed")

    override fun callFailed(call: Call, ioe: IOException) = logDuration(call, "failed")

    private fun logDuration(call: Call, result: String) {
        val start = startedAt.remove(call) ?: return
        val elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
        val request = call.request()
        Log.d("VitalTraceNetwork", "${request.method} ${request.url.encodedPath} $result in ${elapsed}ms")
    }
}
