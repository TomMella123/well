package com.example.wellfit.ui.salud

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt

class BarChartView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var valores: List<Int> = emptyList()
    private var dias: List<String> = emptyList()

    fun setData(valores: List<Int>, dias: List<String>) {
        this.valores = valores
        this.dias = dias
        invalidate()
    }

    private val paintBarra = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val paintTexto = Paint().apply {
        color = Color.BLACK
        textSize = 28f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private val paintDia = Paint().apply {
        color = Color.DKGRAY
        textSize = 26f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private val lineaBase = Paint().apply {
        color = Color.GRAY
        strokeWidth = 3f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (valores.isEmpty()) return

        val maxValor = (valores.maxOrNull() ?: 1).toFloat()
        val anchoBarra = width / (valores.size * 2f)
        val espacio = anchoBarra

        // Línea base
        canvas.drawLine(
            0f, height - 80f,
            width.toFloat(), height - 80f,
            lineaBase
        )

        valores.forEachIndexed { index, valor ->

            // Color por nivel
            val colorBarra = when {
                valor < 70 -> Color.parseColor("#3993DD") // BAJO azul
                valor > 180 -> Color.parseColor("#D84343") // ALTO rojo
                else -> Color.parseColor("#4CAF50") // NORMAL verde
            }
            paintBarra.color = colorBarra

            // Posiciones
            val left = espacio + index * (anchoBarra + espacio)
            val bottom = height - 80f
            val top = bottom - (valor / maxValor * (height - 200))
            val right = left + anchoBarra

            // Dibujar barra
            canvas.drawRoundRect(
                left, top, right, bottom,
                20f, 20f, paintBarra
            )

            // Valor numérico
            canvas.drawText(
                "$valor",
                left + (anchoBarra / 2),
                top - 10f,
                paintTexto
            )

            // Día
            canvas.drawText(
                dias[index],
                left + (anchoBarra / 2),
                height - 30f,
                paintDia
            )
        }
    }
}
