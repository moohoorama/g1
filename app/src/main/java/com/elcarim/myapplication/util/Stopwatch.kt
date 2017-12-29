package com.elcarim.myapplication.util

/**
 * Created by Yanoo on 2016. 6. 4
 */
class Stopwatch {
    private val beginTime: Long
    private var prevTime: Long = 0
    internal var ret = StringBuffer()

    init {
        prevTime = System.currentTimeMillis()
        beginTime = prevTime
    }

    fun event(msg: String) {
        val curTime = System.currentTimeMillis()
        ret.append(String.format("|%d|%s", curTime - prevTime, msg))
        prevTime = curTime
    }

    override fun toString(): String {
        val curTime = System.currentTimeMillis()
        ret.append(String.format("|%d|%s(%d)", curTime - prevTime, "done", curTime - beginTime))
        return ret.toString()
    }
}
