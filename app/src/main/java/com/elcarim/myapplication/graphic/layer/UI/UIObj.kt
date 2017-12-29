package com.elcarim.myapplication.graphic.layer.UI

import com.elcarim.myapplication.MyGLRenderer
import com.elcarim.myapplication.TouchEV
import com.elcarim.myapplication.graphic.layer.UILayer

/**
 * Created by Yanoo on 2017. 12. 29
 */
abstract class UIObj {
    abstract fun act(clock: Long, touchEV: TouchEV)
    abstract fun draw(layer:UILayer, clock: Long)

    fun attach(layer:UILayer) : UIObj {
        layer.addUI(this)
        return this
    }
}