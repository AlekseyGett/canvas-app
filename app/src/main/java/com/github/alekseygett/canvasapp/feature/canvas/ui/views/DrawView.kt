package com.github.alekseygett.canvasapp.feature.canvas.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import com.github.alekseygett.canvasapp.R
import com.github.alekseygett.canvasapp.feature.canvas.ui.CanvasViewState
import kotlin.math.abs

class DrawView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var extraBitmap: Bitmap
    private lateinit var extraCanvas: Canvas

    private var strokeWidth = resources.getDimension(R.dimen.medium_stroke_width)
    private var drawColor = ResourcesCompat.getColor(resources, R.color.black, null)

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true

        color = drawColor
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = strokeWidth
    }

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var currentX = 0f
    private var currentY = 0f

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private var path = Path()

    private var onClick: () -> Unit = {}

    fun setOnClickField(onClickField: () -> Unit) {
        onClick = onClickField
    }

    fun render(state: CanvasViewState) {
        strokeWidth = resources.getDimension(state.lineWeight.value)
        paint.strokeWidth = strokeWidth

        drawColor = ResourcesCompat.getColor(resources, state.color.value, null)
        paint.color = drawColor
    }

    fun clear() {
        extraCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    fun getBitmap(): Bitmap? = if (::extraBitmap.isInitialized) extraBitmap else null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchEnd()
        }

        return true
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        if (::extraBitmap.isInitialized) {
            extraBitmap.recycle()
        }

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    private fun touchStart() {
        onClick()

        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)

        updateCurrentCoordinates()
    }

    private fun touchMove() {
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)

        if (dx >= touchTolerance || dy >= touchTolerance) {
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )

            updateCurrentCoordinates()

            extraCanvas.drawPath(path, paint)
            extraCanvas.save()
        }

        invalidate()
    }

    private fun touchEnd() { }

    private fun updateCurrentCoordinates() {
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

}