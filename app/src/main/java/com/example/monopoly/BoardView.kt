package com.example.monopoly

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min

class BoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private val boardBaseColor = Color.parseColor("#E7F1E4")
    private val boardCenterColor = Color.parseColor("#DCEAD6")
    private val tileBaseColor = Color.parseColor("#F7F3EA")
    private val cornerColor = Color.parseColor("#F2E1C9")
    private val borderColor = Color.parseColor("#2E2E2E")
    private val lineColor = Color.parseColor("#B6C2B0")
    private val titleColor = Color.parseColor("#D34A3A")

    private val brown = Color.parseColor("#8B5E3C")
    private val lightBlue = Color.parseColor("#9DD9F3")
    private val pink = Color.parseColor("#D84A8A")
    private val orange = Color.parseColor("#F59A3E")
    private val red = Color.parseColor("#E0514E")
    private val yellow = Color.parseColor("#F4D35E")
    private val green = Color.parseColor("#4CAF50")
    private val blue = Color.parseColor("#2B5CAA")
    private val neutral = Color.parseColor("#C9C9C9")

    private val bottomBands = arrayOf(
        brown, null, brown, neutral, neutral, lightBlue, null, lightBlue, lightBlue
    )
    private val leftBands = arrayOf(
        pink, pink, neutral, orange, null, orange, orange, red, red
    )
    private val topBands = arrayOf(
        yellow, neutral, yellow, green, null, green, green, blue, blue
    )
    private val rightBands = arrayOf(
        red, null, yellow, neutral, green, null, blue, null, neutral
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height).toFloat()
        val left = (width - size) / 2f
        val top = (height - size) / 2f
        val cell = size / 11f

        // Board base
        fillPaint.color = boardBaseColor
        canvas.drawRect(left, top, left + size, top + size, fillPaint)

        // Center area
        val innerLeft = left + cell
        val innerTop = top + cell
        val innerRight = left + size - cell
        val innerBottom = top + size - cell
        fillPaint.color = boardCenterColor
        canvas.drawRect(innerLeft, innerTop, innerRight, innerBottom, fillPaint)

        val lineWidth = max(1.5f * resources.displayMetrics.density, cell * 0.04f)
        strokePaint.strokeWidth = lineWidth
        strokePaint.color = lineColor

        // Corners
        drawCorner(canvas, cellRect(left, top, cell, 10, 10))
        drawCorner(canvas, cellRect(left, top, cell, 0, 10))
        drawCorner(canvas, cellRect(left, top, cell, 0, 0))
        drawCorner(canvas, cellRect(left, top, cell, 10, 0))

        // Bottom row (col 9..1)
        for (i in 0 until 9) {
            val col = 9 - i
            val rect = cellRect(left, top, cell, col, 10)
            drawTile(canvas, rect, bottomBands.getOrNull(i), BandSide.TOP, lineWidth)
        }

        // Left column (row 9..1)
        for (i in 0 until 9) {
            val row = 9 - i
            val rect = cellRect(left, top, cell, 0, row)
            drawTile(canvas, rect, leftBands.getOrNull(i), BandSide.RIGHT, lineWidth)
        }

        // Top row (col 1..9)
        for (i in 0 until 9) {
            val col = i + 1
            val rect = cellRect(left, top, cell, col, 0)
            drawTile(canvas, rect, topBands.getOrNull(i), BandSide.BOTTOM, lineWidth)
        }

        // Right column (row 1..9)
        for (i in 0 until 9) {
            val row = i + 1
            val rect = cellRect(left, top, cell, 10, row)
            drawTile(canvas, rect, rightBands.getOrNull(i), BandSide.LEFT, lineWidth)
        }

        // Borders
        strokePaint.color = borderColor
        strokePaint.strokeWidth = max(2f * resources.displayMetrics.density, cell * 0.06f)
        canvas.drawRect(left, top, left + size, top + size, strokePaint)
        canvas.drawRect(innerLeft, innerTop, innerRight, innerBottom, strokePaint)

        // Title
        titlePaint.color = titleColor
        titlePaint.textSize = size * 0.08f
        canvas.drawText("MONOPOLY", left + size / 2f, top + size / 2f, titlePaint)
    }

    private fun cellRect(originX: Float, originY: Float, cell: Float, col: Int, row: Int): RectF {
        val left = originX + col * cell
        val top = originY + row * cell
        return RectF(left, top, left + cell, top + cell)
    }

    private fun drawCorner(canvas: Canvas, rect: RectF) {
        fillPaint.color = cornerColor
        canvas.drawRect(rect, fillPaint)
        strokePaint.color = lineColor
        canvas.drawRect(rect, strokePaint)
    }

    private fun drawTile(
        canvas: Canvas,
        rect: RectF,
        bandColor: Int?,
        bandSide: BandSide,
        lineWidth: Float
    ) {
        fillPaint.color = tileBaseColor
        canvas.drawRect(rect, fillPaint)

        if (bandColor != null) {
            fillPaint.color = bandColor
            val band = RectF(rect)
            val thickness = rect.width() * 0.22f
            when (bandSide) {
                BandSide.TOP -> band.bottom = band.top + thickness
                BandSide.BOTTOM -> band.top = band.bottom - thickness
                BandSide.LEFT -> band.right = band.left + thickness
                BandSide.RIGHT -> band.left = band.right - thickness
            }
            canvas.drawRect(band, fillPaint)
        }

        strokePaint.color = lineColor
        strokePaint.strokeWidth = lineWidth
        canvas.drawRect(rect, strokePaint)
    }

    private enum class BandSide {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }
}
