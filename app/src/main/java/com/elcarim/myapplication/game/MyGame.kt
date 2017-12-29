package com.elcarim.myapplication.game

import com.elcarim.myapplication.TouchEV
import com.elcarim.myapplication.graphic.layer.Layer

/**
 * Created by Yanoo on 2017. 12. 25
 */
interface MyGame {
    fun getLayers():Array<Layer>
    fun act(clock: Long, touchEV: TouchEV)
    fun draw(clock: Long)
}
