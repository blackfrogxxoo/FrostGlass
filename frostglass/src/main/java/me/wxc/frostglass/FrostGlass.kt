package me.wxc.frostglass

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import kotlin.math.roundToInt

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
class FrostGlass(private val src: View, private val dst: ImageView) {
    companion object {
        const val TAG = "FrostGlass"
        const val BITMAP_SCALE = 0.2f
    }

    private val rect: Rect = Rect()
    private val paint: Paint = Paint()

    init {
        paint.flags = Paint.FILTER_BITMAP_FLAG
    }

    fun display(blurRadius: Float) {
        makeRectValid()
        val time = System.currentTimeMillis()
        src.isDrawingCacheEnabled = true
        var drawingCache: Bitmap? = null
        try {
            drawingCache = src.drawingCache
        } catch (ignore: java.lang.NullPointerException) {
        }
        if (drawingCache == null) {
            src.isDrawingCacheEnabled = false
            return
        }
        var result = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawBitmap(drawingCache, 0f, 0f, paint)
        src.isDrawingCacheEnabled = false
        drawingCache.recycle()
        result = blurBitmap(dst.context, result, blurRadius)
        val result1 = result
        dst.post { dst.setImageBitmap(result1) }
        Log.i(TAG, "displaying frost glass costs: " + (System.currentTimeMillis() - time))
    }

    fun display() {
        display(8.0f)
    }

    private fun makeRectValid() {
        if (rect.width() <= 0 || rect.height() <= 0) {
            val srcLocation = IntArray(2)
            val dstLocation = IntArray(2)
            src.getLocationInWindow(srcLocation)
            dst.getLocationInWindow(dstLocation)
            Log.i(TAG, "run: " + srcLocation[0] + ", " + srcLocation[1])
            rect.left = srcLocation[0] - dstLocation[0]
            rect.top = srcLocation[1] - dstLocation[1]
            rect.right = srcLocation[0] - dstLocation[0] + dst.width
            rect.bottom = srcLocation[1] - dstLocation[1] + dst.height
        }
    }

    private fun blurBitmap(context: Context?, image: Bitmap, blurRadius: Float): Bitmap? {
        val width = (image.width * BITMAP_SCALE).roundToInt()
        val height = (image.height * BITMAP_SCALE).roundToInt()
        val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)
        val rs = RenderScript.create(context)
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
        blurScript.setRadius(blurRadius)
        blurScript.setInput(tmpIn)
        blurScript.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        inputBitmap.recycle()
        return outputBitmap
    }
}