package com.elcarim.myapplication

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.elcarim.myapplication.graphic.layer.Layer

import com.elcarim.myapplication.util.Stopwatch

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by Yanoo on 2017. 12. 24
 */
class TouchEV(val width:Float, val height:Float, val x:Float, val y:Float, val action:Int) {
    fun distance(x:Float, y:Float): Float {
        return Math.hypot((x-this.x).toDouble(),(y-this.y).toDouble()).toFloat()
     }
}

class MyGLRenderer(private val activity: MainActivity) : GLSurfaceView.Renderer, View.OnTouchListener {
    private var glTexture:IntArray = intArrayOf()

    private val fps = 60

    private var touchEV=TouchEV(1f,1f,0f,0f,-1)
    private var width = 1
    private var height = 1
    private var clock: Long = 0
    private val startTS = System.currentTimeMillis()

    fun getWidth()=width
    fun getHeight()=height

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        gl.glShadeModel(GL10.GL_SMOOTH)
        gl.glClearDepthf(1.0f)
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glDepthFunc(GL10.GL_LEQUAL)

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glEnable(GL10.GL_BLEND)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        gl.glFrontFace(GL10.GL_CW)     // 시계방향 그리기 설정
    }

    private fun resetBitmap(gl: GL10, layer: Array<Layer>) {
        if (glTexture.size != layer.size) {
            Log.i("LoadBitmap", "Remake")
            if (glTexture.isNotEmpty()) {
                gl.glDeleteTextures(glTexture.size, glTexture, 0)
            }

            glTexture = IntArray(layer.size)
            gl.glGenTextures(
                    glTexture.size,
                    glTexture,
                    0
            )
        }
        for (idx in layer.indices) {
            layer[idx].loadBitmap(gl,glTexture[idx])
        }
    }

    override fun onSurfaceChanged(gl: GL10, w: Int, h: Int) {
        width = w
        height = h
        if (width==0){
            width = 1
        }
        if (height == 0) {
            height = 1
        }
        gl.glViewport(0, 0, w, h)         // ViewPort 리셋
        gl.glMatrixMode(GL10.GL_PROJECTION)        // MatrixMode를 Project Mode로
        gl.glLoadIdentity()                        // Matrix 리셋
        gl.glOrthof(0f, w.toFloat(), h.toFloat(), 0f, 1f, -1f)
        gl.glMatrixMode(GL10.GL_MODELVIEW)         // Matrix를 ModelView Mode로 변환
        gl.glLoadIdentity()                        // Matrix 리셋
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        touchEV = TouchEV(width.toFloat(),height.toFloat(),p1!!.x,p1.y,p1.action)
        return true
    }

    fun reload() {
        for (layer in activity.game.getLayers()) {
            layer.reload()
        }
    }

    override fun onDrawFrame(gl: GL10) {
        val sw = Stopwatch()
        val curClock = (System.currentTimeMillis() - startTS) * fps / 1000

        for (layer in activity.game.getLayers()) {
            layer.clear()
        }

        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        sw.event("clearcolor")
//        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
        sw.event("clear_gl")
        gl.glLoadIdentity()

        sw.event("clear")

        while (clock < curClock) {
            for (layer in activity.game.getLayers()) {
                layer.act(clock, touchEV)
            }
            activity.game.act(clock, touchEV)

            clock++
            if (touchEV.action == MotionEvent.ACTION_UP) {
                touchEV = TouchEV(touchEV.width,touchEV.height,touchEV.x,touchEV.y, -1)
            }
        }
        sw.event("act")

        activity.game.draw(clock)
        sw.event("draw_by_game")
        for (layer in activity.game.getLayers()) {
            layer.draw(clock)
        }
        sw.event("draw_by_layer")

        resetBitmap(gl, activity.game.getLayers())
        sw.event("loadbitmap")

        for (layer in activity.game.getLayers()) {
            layer.render(gl)
        }
        sw.event("draw_to_surface")
        gl.glFinish()
        sw.event("flush")
        //Log.d("draw", sw.toString())
    }
}
