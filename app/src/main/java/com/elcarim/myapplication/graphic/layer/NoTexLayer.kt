package com.elcarim.myapplication.graphic.layer

import android.graphics.Bitmap
import android.graphics.RectF
import com.elcarim.myapplication.MainActivity
import com.elcarim.myapplication.MyGLRenderer
import com.elcarim.myapplication.TouchEV
import com.elcarim.myapplication.graphic.TColor

/**
 * Created by Yanoo on 2017. 12. 28..
 */
class NoTexLayer(activity: MainActivity,bufferMax:Int): Layer(activity,bufferMax  ) {
    override fun getWidth(): Int {
        return getRenderer().getWidth()
    }

    override fun getHeight(): Int {
        return getRenderer().getHeight()
    }

    override fun getBitmap(): Bitmap? {
        return null
    }

    override fun clear() {

    }
    override fun drawRect(loc: RectF, tc: TColor): Boolean {
        this.addRect(loc, fullTx, tc)
        return true
    }
}