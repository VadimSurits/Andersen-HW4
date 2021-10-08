package com.example.android.andersen_hw4

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import java.time.ZoneId
import java.util.*

class MyCustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_HOUR_POINTER_COLOR = Color.BLACK
        private const val DEFAULT_MINUTE_POINTER_COLOR = Color.RED
        private const val DEFAULT_SECOND_POINTER_COLOR = Color.BLUE
        private const val DEFAULT_HOUR_POINTER_WIDTH = 40f
        private const val DEFAULT_MINUTE_POINTER_WIDTH = 20f
        private const val DEFAULT_SECOND_POINTER_WIDTH = 10f
    }

    private var hourPointerColor = DEFAULT_HOUR_POINTER_COLOR
    private var minutePointerColor = DEFAULT_MINUTE_POINTER_COLOR
    private var secondPointerColor = DEFAULT_SECOND_POINTER_COLOR

    private var hourPointerWidth = DEFAULT_HOUR_POINTER_WIDTH
    private var minutePointerWidth = DEFAULT_MINUTE_POINTER_WIDTH
    private var secondPointerWidth = DEFAULT_SECOND_POINTER_WIDTH

    private val pointerRange = 20f
    private val scaleMax = 50
    private var radius = 400f
    private var centerX = 0f
    private var centerY = 0f

    private val myPaint = Paint().apply {
        isAntiAlias = true
    }

    init {
        setupAttributes(attrs)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        centerX = (width / 2).toFloat()
        centerY = (height / 2).toFloat()
    }

    //Метод установки кастомных аттрибутов в xml
    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.MyCustomView,
            0, 0
        )
        hourPointerColor = typedArray.getColor(
            R.styleable.MyCustomView_hourPointerColor,
            DEFAULT_HOUR_POINTER_COLOR
        )
        minutePointerColor = typedArray.getColor(
            R.styleable.MyCustomView_minutePointerColor,
            DEFAULT_MINUTE_POINTER_COLOR
        )
        secondPointerColor = typedArray.getColor(
            R.styleable.MyCustomView_secondPointerColor,
            DEFAULT_SECOND_POINTER_COLOR
        )
        hourPointerWidth = typedArray.getDimension(
            R.styleable.MyCustomView_hourPointerWidth,
            DEFAULT_HOUR_POINTER_WIDTH
        )
        minutePointerWidth = typedArray.getDimension(
            R.styleable.MyCustomView_minutePointerWidth,
            DEFAULT_MINUTE_POINTER_WIDTH
        )
        secondPointerWidth = typedArray.getDimension(
            R.styleable.MyCustomView_secondPointerWidth,
            DEFAULT_SECOND_POINTER_WIDTH
        )
        typedArray.recycle()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(centerX, centerY)
        drawClock(canvas)
        drawClockScale(canvas)
        drawPointer(canvas)
        // Перерисовка/обновление View
        invalidate()
    }

    //Метод отрисовки главного круга для часов
    private fun drawClock(canvas: Canvas) {
        myPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }
        canvas.drawCircle(0f, 0f, radius, myPaint)
    }

    //Метод отрисовки часовой шкалы
    private fun drawClockScale(canvas: Canvas) {
        for (index in 1..60 step 5) {
            myPaint.strokeWidth = 20f
            canvas.rotate(30f, 0f, 0f)
            canvas.drawLine(0f, -radius, 0f, -radius + scaleMax, myPaint)
        }
    }

    //Метод отрисовки стрелок
    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawPointer(canvas: Canvas) {
        // Получаем текущее время
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Europe/Moscow")))
        val hour = calendar[Calendar.HOUR]
        val minute = calendar[Calendar.MINUTE]
        val second = calendar[Calendar.SECOND]
        val millisecond = calendar[Calendar.MILLISECOND]

        //Определяем угол перемещения стрелок
        val angleHour = (hour + minute.toFloat() / 60) * 360 / 12
        val angleMinute = (minute + second.toFloat() / 60) * 360 / 60
        val angleSecond = (second + millisecond.toFloat() / 1000) * 360 / 60

        //Рисуем часовую стрелку
        canvas.save()
        canvas.rotate(angleHour, 0f, 0f)
        val rectHour = RectF(
            -hourPointerWidth / 2,
            -radius / 2,
            hourPointerWidth / 2,
            radius / 6
        )
        myPaint.color = hourPointerColor
        myPaint.style = Paint.Style.FILL
        myPaint.strokeWidth = 40f
        canvas.drawRoundRect(rectHour, pointerRange, pointerRange, myPaint)
        canvas.restore()

        // Рисуем минутную стрелку
        canvas.save()
        canvas.rotate(angleMinute, 0f, 0f)
        val rectMinute = RectF(
            -minutePointerWidth / 2,
            -radius * 3.7f / 5,
            minutePointerWidth / 2,
            radius / 6
        )
        myPaint.color = minutePointerColor

        myPaint.strokeWidth = 20f
        canvas.drawRoundRect(rectMinute, pointerRange, pointerRange, myPaint)
        canvas.restore()

        // Рисуем секундную стрелку
        canvas.save()
        canvas.rotate(angleSecond, 0f, 0f)
        val rectSecond = RectF(
            -secondPointerWidth / 2,
            -radius + 30,
            secondPointerWidth / 2,
            radius / 6
        )
        myPaint.color = secondPointerColor
        myPaint.strokeWidth = 10f
        canvas.drawRoundRect(rectSecond, pointerRange, pointerRange, myPaint)
        canvas.restore()

        // Рисуем маленький круг сверху стрелок
        myPaint.color = Color.BLACK
        canvas.drawCircle(0f, 0f, 4f * 4, myPaint)
    }
}