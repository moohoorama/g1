package com.elcarim.myapplication

/**
 * Created by Yanoo on 2017. 12. 24
 */
import android.opengl.GLSurfaceView


class MyGLSurfaceView : GLSurfaceView {
    constructor(context: MainActivity) : super(context) {
        activity = context
        mRenderer = MyGLRenderer(context)
        setOnTouchListener(mRenderer)
        setRenderer(mRenderer)
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    private var activity: MainActivity? = null
    private val mRenderer: MyGLRenderer

    fun GetRenderer(): MyGLRenderer {
        return mRenderer
    }

    override fun onResume() {
        super.onResume()

        mRenderer.reload()
    }

}
