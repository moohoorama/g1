package com.elcarim.myapplication.game

import android.graphics.RectF
import com.elcarim.myapplication.MainActivity
import com.elcarim.myapplication.MyGLRenderer
import com.elcarim.myapplication.TouchEV
import com.elcarim.myapplication.graphic.TColor
import com.elcarim.myapplication.graphic.layer.Layer
import com.elcarim.myapplication.graphic.layer.NoTexLayer
import com.elcarim.myapplication.graphic.layer.UI.ButtonObj
import com.elcarim.myapplication.graphic.layer.UILayer
import com.google.android.gms.internal.zzahn.runOnUiThread
import java.util.*

/**
 * Created by Yanoo on 2017. 12. 25
 */
class MyTetris(private var activity: MainActivity) : MyGame {
    private val textLayer= UILayer(activity,1024,96)
    private val layers:Array<Layer> = arrayOf(NoTexLayer(activity, 65536),textLayer)

    private var lbut= ButtonObj(250f, 950f,"Left",TColor.RED)
    private var rbut= ButtonObj(550f, 950f,"Right",TColor.BLUE)
    private var ubut= ButtonObj(400f, 950f,"Change",TColor.GREEN).setInterval(30)
    private var dbut= ButtonObj(400f,1100f,"Down",TColor.YELLOW)

    private val width = 15
    private val height = 20
    private val blinkClock = 30
    private val deadClock = 60
    private val downClock = 30L

    private var blockWidth = 1f
    private var blockHeight = 1f
    private var blinkLines=ArrayList<Int>()

    enum class Status(val value: Int) {
        Game(1),
        Pause(2),
        Dead(3);

        companion object {
            fun from(findValue: Int): Status = Status.values().first { it.value == findValue }
        }
    }
    private var status:Status = Status.Game
    private var statusClock:Long = 0

    private val angleFlow = intArrayOf(
            0, 0,
            1, 0,
            0, 1,

            1, 0,
            0, 1,
           -1, 0,

            1, 1,
           -1, 0,
            0,-1,

            0, 1,
            0,-1,
            1, 0)

    private var rand=Random()
    private var angle=0
    private var nextShape = 0
    private var curShape = 0
    private val shapes = intArrayOf(
            0,0,1,0,
            0,0,1,0,
            0,0,1,0,
            0,0,1,0,

            0,0,0,0,
            0,1,1,0,
            0,1,1,0,
            0,0,0,0,

            0,0,1,0,
            0,1,1,0,
            0,0,1,0,
            0,0,0,0,

            0,0,1,0,
            0,1,1,0,
            0,1,0,0,
            0,0,0,0,

            0,1,0,0,
            0,1,1,0,
            0,0,1,0,
            0,0,0,0,

            0,1,0,0,
            0,1,0,0,
            0,1,1,0,
            0,0,0,0,

            0,0,1,0,
            0,0,1,0,
            0,1,1,0,
            0,0,0,0)


    private var blocks = IntArray(width*height)
    private var x=width/2
    private var y=0
    private var score=0
    private var scores=""

    private fun getBX(x:Int) = (x+5)*blockWidth
    private fun getBY(y:Int) = (y+2)*blockHeight
    private fun getRect(x:Int, y:Int) = RectF(getBX(x), getBY(y), getBX(x+1)-1, getBY(y+1)-1)
    private fun getAddress(x:Int, y:Int)=x+y*width

    init {
        textLayer.addUI(ubut).addUI(lbut).addUI(rbut).addUI(dbut)
        init()
    }
    private fun init() {
        score = 0
        if (activity.dbHelper != null) {
            scores = activity.dbHelper!!.select()
        }
        angle = 0
        curShape = nextShape()
        nextShape = nextShape()
        for (i in 0 until blocks.size) {
            blocks[i] = 0
        }
        for (i in 0 until width) {
            blocks[getAddress(i,height-1)] = 1
        }
        for (i in 0 until height) {
            blocks[getAddress(0,i)] = 1
            blocks[getAddress(width-1,i)] = 1
        }
    }

    override fun getLayers(): Array<Layer> {
        return layers
    }
    override fun act(clock: Long, touchEV: TouchEV) {
        when (status) {
            Status.Game -> game(clock,touchEV)
            Status.Pause -> {}
            Status.Dead -> if (clock-statusClock > deadClock) {init();setStatus(Status.Game,clock)}
        }
    }
    private fun game(clock: Long,touchEV: TouchEV) {
        if (blinkLines.size  > 0) {
            if (clock - statusClock > blinkClock) {
                for (line in blinkLines) {
                    eraseLine(line)
                }
                score += blinkLines.size*blinkLines.size
                blinkLines.clear()
            }
        } else {
            if (ubut.press()) {
                var i=0
                for (i in 0 until 4) {
                    angle = (angle + 1) % 4
                    if (!blockCheck()) break

                    x--
                    if (!blockCheck()) break
                    x+=2
                    if (!blockCheck()) break
                    x--
                }
            }
            if (lbut.press()) {
                x--
                if (blockCheck()) {
                    x++
                }
            }
            if (rbut.press()) {
                x++
                if (blockCheck()) {
                    x--
                }
            }
            if (dbut.press()) {
                goDown(clock)
            }
            if ((clock - statusClock) % downClock == downClock -1) {
                goDown(clock)
            }
        }
    }
    private fun setStatus(status:Status, clock:Long) {
        this.status = status
        statusClock = clock
    }
    private fun goDown(clock:Long) {
        y++
        if (blockCheck()) {
            y--
            blockSet(clock)
            x = width/2
            y = 0
            curShape = nextShape
            nextShape = nextShape()
            if (blockCheck()) {
                setStatus(Status.Pause, clock)
                runOnUiThread( Runnable() {
                    activity.readText(fun(v:String) {
                        if (v!="") {
                            activity.dbHelper!!.insert(v, score)
                        }
                        setStatus(Status.Dead, clock)
                    })
                })
            }
        }
        scores = activity.dbHelper!!.select()
        statusClock = clock
    }
    private fun nextShape():Int {
        return rand.nextInt(shapes.size/(4*4))
    }
    private fun calcI(x:Int, y:Int, angle:Int):Int {
        return angleFlow[angle*6+0]*3 + x* angleFlow[angle*6+2] + y* angleFlow[angle*6+4]
    }
    private fun calcJ(x:Int, y:Int, angle:Int):Int {
        return angleFlow[angle*6+1]*3 + x* angleFlow[angle*6+3] + y* angleFlow[angle*6+5]
    }
    private fun blockSet(clock: Long) {
        for (jj in 0..3) {
            for (ii in 0..3) {
                val i = calcI(ii,jj,angle)
                val j = calcJ(ii,jj,angle)

                val cur= shapes[curShape*4*4 + i + j*4]
                if (cur > 0) {
                    blocks[getAddress(x+ii,y+jj)] = curShape + 2
                }
            }
        }

        var erase=true
        while(erase) {
            erase = false
            for (j in 1..height - 2) {
                var empty=false
                for (i in 1..width - 2) {
                    empty =empty or  (blocks[getAddress(i,j)] == 0)
                }
                if (!empty) {
                    blinkLines.add(j)
                }
            }
        }
        statusClock = clock
    }

    private fun eraseLine(j:Int) {
        for (jj in j downTo 1) {
            for (i in 1..width - 2) {
                blocks[getAddress(i,jj)] = blocks[getAddress(i,jj-1)]
            }
        }
    }

    private fun blockCheck():Boolean {
        for (jj in 0..3) {
            for (ii in 0..3) {
                val i = calcI(ii,jj,angle)
                val j = calcJ(ii,jj,angle)

                val cur= shapes[curShape*4*4 + i + j*4]
                if (cur > 0 && blocks[getAddress(x+ii,y+jj)] > 0) {
                    return true
                }
            }
        }
        return false
    }


    private fun drawBlock(x:Int, y:Int, tc:TColor) {
        layers[0].drawRect(getRect(x,y),tc)
    }
    private fun drawShape(x:Int, y:Int, shapeIdx:Int, angle:Int, shadow:Boolean) {
        for (jj in 0..3) {
            for (ii in 0..3) {
                val i = calcI(ii,jj,angle)
                val j = calcJ(ii,jj,angle)

                val cur= shapes[shapeIdx*4*4 + i + j*4]
                if (cur > 0) {
                    if (shadow) {
                        layers[0].drawRect(
                                RectF(getBX(x+ii), getBY(y+jj), getBX(x+ii+1)-1, getBY(height-1)-1),
                                TColor.GRAY.clone().multyplyRGB(0.4f))
                    }
                    drawBlock(x+ii,y+jj,TColor.IDX[shapeIdx+2])
                }
            }
        }
    }
    override fun draw(clock: Long) {
        // update blockWidth
        blockWidth = layers[0].getWidth()/(width+10).toFloat()
        blockHeight = layers[0].getHeight()/(height+10).toFloat()

        drawShape(-4,1,nextShape,0,false)
        drawShape(x,y,curShape,angle, true)

        for (j in 0 until height) {
            for (i in 0 until width) {
                val b = blocks[getAddress(i,j)]
                if (b > 0) {
                    val color = TColor.IDX[b].clone()
                    if (blinkLines.contains(j) && 0 < i && i < width-1) {
                        color.a = 0.5f+((clock-statusClock).toFloat() % (blinkClock /2)) / (blinkClock)
                    }
                    color.multyplyRGB(0.7f)
                    drawBlock(i,j,color)
                }
            }
        }

        textLayer.drawText(RectF(100f,0f,-1f,80f),"점수 $score", TColor.WHITE)
        textLayer.drawText(500f,500f,80f,"$scores", TColor.WHITE)
    }
}