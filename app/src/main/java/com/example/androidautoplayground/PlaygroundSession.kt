package com.example.androidautoplayground

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.util.Log
import androidx.car.app.*
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

const val TAG = "Playground"

class PlaygroundSession : Session(), DefaultLifecycleObserver {

    private val surfaceListener: SurfaceCallback = object : SurfaceCallback {

        override fun onSurfaceAvailable(surfaceContainer: SurfaceContainer) {
            Log.i(TAG, "onSurfaceAvailable")
        }

        override fun onSurfaceDestroyed(surfaceContainer: SurfaceContainer) {
            Log.i(TAG, "onSurfaceDestroyed")
        }

        override fun onStableAreaChanged(area: Rect) {
            Log.i(TAG, "onStableAreaChanged")
        }

        override fun onVisibleAreaChanged(area: Rect) {
            Log.i(TAG, "onVisibleAreaChanged")
        }

        override fun onScroll(distanceX: Float, distanceY: Float) {
            Log.i(TAG, "onScroll")
        }

        override fun onFling(velocityX: Float, velocityY: Float) {
            Log.i(TAG, "onFling")
        }

        override fun onScale(focusX: Float, focusY: Float, scaleFactor: Float) {
            Log.i(TAG, "onScale")
        }

    }

    init {
        lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        Log.i(TAG, "onCreate")
        carContext.getCarService(AppManager::class.java).setSurfaceCallback(surfaceListener)
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.i(TAG, "onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.i(TAG, "onPause")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.i(TAG, "onDestroy")
    }

    override fun onCreateScreen(intent: Intent): Screen {
        return PlaygroundScreen(carContext)
    }

}