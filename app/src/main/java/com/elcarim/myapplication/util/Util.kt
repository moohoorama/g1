package com.elcarim.myapplication.util

import com.elcarim.myapplication.MainActivity

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Created by Yanoo on 2016. 5. 29
 */
object Util {
    val Q_LEN = 32

    var CubeLen = 32

    /*
    (x - y)  = xx / (Q_LEN * 4);
    (x + y)  = yy / (Q_LEN * 2);
    x = xx / (Q_LEN * 2) + yy/(Q_LEN)
    y = yy / (Q_LEN) - xx / (Q_LEN*2)
    */
    fun getIsoX(x: Int, y: Int): Int {
        return (x / 2 + y) / (Q_LEN * 4)
    }

    fun getIsoY(x: Int, y: Int): Int {
        return (-x / 2 + y) / (Q_LEN * 4)
    }

    fun get2dx(x: Int, y: Int, z: Int): Int {
        return (x - y) * Q_LEN * 4
    }

    fun get2dy(x: Int, y: Int, z: Int): Int {
        return ((x + y) * 2 + z * 4) * Q_LEN
    }

    fun setFloatBuffer(src: FloatArray): FloatBuffer {
        val dst: FloatBuffer
        val byteBuf = ByteBuffer.allocateDirect(src.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        dst = byteBuf.asFloatBuffer()
        dst.put(src)
        dst.position(0)

        return dst
    }

    fun setFloatBufferFromList(src: List<FloatArray>): FloatBuffer {
        var size = 0
        for (`val` in src) {
            size += `val`.size
        }
        val med = FloatArray(size)
        var idx = 0
        for (arr in src) {
            System.arraycopy(arr, 0, med, idx, arr.size)
            idx += arr.size
        }

        val dst: FloatBuffer
        val byteBuf = ByteBuffer.allocateDirect(med.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        dst = byteBuf.asFloatBuffer()
        dst.put(med)
        dst.position(0)

        return dst
    }

    fun getRoundX(edge: Int, idx: Int, delication: Int): Float {
        val baseArrow = floatArrayOf(-1f, -1f, 1f, -1f, 1f, 1f, -1f, 1f)
        return -baseArrow[edge * 2] - Math.cos(edge * Math.PI / 2.0f + idx * Math.PI / (2.0f * (delication - 1))).toFloat()
    }

    fun getRoundY(edge: Int, idx: Int, delication: Int): Float {
        val baseArrow = floatArrayOf(-1f, -1f, 1f, -1f, 1f, 1f, -1f, 1f)
        return -baseArrow[edge * 2 + 1] - Math.sin(edge * Math.PI / 2.0f + idx * Math.PI / (2.0f * (delication - 1))).toFloat()
    }

    fun writeFile(activity: MainActivity, path: String, contents: ByteArray): Boolean {
        var file: File? = null
        var isSuccess = true

        file = File(activity.getBasePath() + path)
        if (file == null) {
            return false
        }
        if (!file.exists()) {
            try {
                isSuccess = file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                isSuccess = false
            }

            if (!isSuccess) {
                return false
            }
        }

        val fos: FileOutputStream
        try {
            fos = FileOutputStream(file)
            fos.write(contents)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
            isSuccess = false
        }

        return isSuccess
    }

    fun readFile(activity: MainActivity, path: String): ByteArray? {
        var file: File? = null

        file = File(activity.getBasePath() + path)
        if (file == null || !file.exists()) {
            return null
        }

        val fis: FileInputStream
        try {
            fis = FileInputStream(file)
            val readSize = file.length().toInt()
            val buffer = ByteArray(readSize)
            fis.read(buffer)
            fis.close()
            return buffer
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    /*
    (x - y)  = xx / (Q_LEN * 4);
    (x + y)  = yy / (Q_LEN * 2);
    x = xx / (Q_LEN * 2) + yy/(Q_LEN)
    y = yy / (Q_LEN) - xx / (Q_LEN*2)
    */
    fun ViewToIsoX(x: Int, y: Int): Int {
        return (x * 2 + y) / CubeLen
    }

    fun ViewToIsoY(x: Int, y: Int): Int {
        return (-x * 2 + y) / CubeLen
    }

    fun IsoToViewX(x: Int, y: Int, z: Int): Int {
        return (x - y) * CubeLen
    }

    fun IsoToViewY(x: Int, y: Int, z: Int): Int {
        return ((x + y) / 2 + z) * CubeLen
    }
}
