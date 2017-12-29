package com.elcarim.myapplication.graphic.layer

import android.graphics.*
import com.elcarim.myapplication.MainActivity
import com.elcarim.myapplication.MyGLRenderer
import com.elcarim.myapplication.TouchEV
import com.elcarim.myapplication.graphic.TColor

/**
 * Created by Yanoo on 2017. 12. 28
 */
class PaintLayer(val blinkSize:Int, activity: MainActivity, bitmapId:Int): Layer(activity,1  ) {
    private var bitmap:Bitmap=if (bitmapId != -1) {
            BitmapFactory.decodeResource(activity.resources, bitmapId)
        } else {
            Bitmap.createBitmap(blinkSize, blinkSize, Bitmap.Config.ARGB_4444)
        }
    private var canvas:Canvas=Canvas(bitmap)


    override fun getWidth(): Int {
        return blinkSize
    }

    override fun getHeight(): Int {
        return blinkSize
    }

    override fun clear() {
        bitmap.eraseColor(Color.TRANSPARENT)

        val renderer=activity.mGLView.GetRenderer()
        addRect(RectF(0.0f,0.0f,renderer.getWidth().toFloat(),renderer.getHeight().toFloat()), fullTx, TColor.WHITE)
    }
    override fun getBitmap(): Bitmap? {
        return bitmap
    }

    override fun drawRect(loc: RectF, tc: TColor): Boolean {
        val paint= Paint()
        tc.setPaint(paint)
        canvas.drawRect(loc, paint)

        setDirty()
        return true
    }
}