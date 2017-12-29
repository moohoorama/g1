package com.elcarim.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.elcarim.myapplication.data.DBHelper
import com.elcarim.myapplication.game.MyGame
import com.elcarim.myapplication.game.MyTetris
import com.google.android.gms.ads.*
import android.R.string.cancel
import android.content.DialogInterface
import android.text.InputType
import android.support.v4.widget.SearchViewCompat.setInputType
import android.widget.EditText




class MainActivity : Activity() {
    var game: MyGame = MyTetris(this)
    private var mInterstitialAd: InterstitialAd? = null
    lateinit var mGLView: MyGLSurfaceView

    var dbHelper:DBHelper?= DBHelper(this,"moohoorama",null,1)

    var mediaPlayer: MediaPlayer? = null
    var mSoundPool: SoundPool = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
//        mSoundIdx[0] = mSoundPool.load(context, R.raw.sword, 0);


    fun getBasePath(): String {
        return this.getFilesDir().getPath().toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        mInterstitialAd = newInterstitialAd()
        loadInterstitial()
        */
        setContentView(R.layout.linear_layour)

/*
        mediaPlayer=MediaPlayer.create(this, R.raw.cannoon)
        mediaPlayer!!.start()
         */
        mSoundPool.load(this,R.raw.boing,0)

        val src= findViewById<View>(R.id.linear_layout)
        Log.w("Init",src.javaClass.name)
        var layout = findViewById<View>(R.id.linear_layout) as LinearLayout;

        MobileAds.initialize(this,getString(R.string.interstitial_ad_app_id))
        val adView = AdView(this)
        adView.adSize = AdSize.SMART_BANNER
        adView.adUnitId = getString(R.string.ad_unit_id)
//        adView.adUnitId = getString(R.string.interstitial_ad_unit_id)

        val adRequest = AdRequest.Builder().addTestDevice("0EEA64C597E1820057051401DA6757AA").build()
        Log.i("ads","${adRequest.isTestDevice(this)}")
        adView.loadAd(adRequest)
        layout.addView(adView)

        mGLView = MyGLSurfaceView(this)
        layout.addView(mGLView);
    }

    fun readText(cb:(String)->Unit):String {
        var builder=AlertDialog.Builder(this)
        builder.setTitle("Title")

        val input = EditText(this)
//        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        var text=""
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> cb(input.text.toString()) })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> cb("");dialog.cancel() })

        builder.show()
        return text
    }

    private var mLostFocus = false
    private var mToBeResumed = false

    override fun onPause() {
        mLostFocus = true
        mGLView.onPause()

        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        mToBeResumed = mLostFocus
        if (!mLostFocus) {
            mGLView.onResume()
            mGLView.GetRenderer().reload()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        mLostFocus = !hasFocus
        if (mToBeResumed && hasFocus) {
            mToBeResumed = false
            mGLView.onResume()
        }
    }

    fun AdPopUp(){
        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded) {
            mInterstitialAd!!.show()
        } else {
        }
    }

    private fun viewToast(msg: String) {
        Toast.makeText(this,
                msg, Toast.LENGTH_SHORT).show()
    }

    private fun newInterstitialAd(): InterstitialAd {
        val interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = getString(R.string.interstitial_ad_unit_id)
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
            }

            override fun onAdFailedToLoad(errorCode: Int) {
            }

            override fun onAdClosed() {
                loadInterstitial()
            }
        }
        return interstitialAd
    }

    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build()
        mInterstitialAd!!.loadAd(adRequest)
    }
}
