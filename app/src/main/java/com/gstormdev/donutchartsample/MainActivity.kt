package com.gstormdev.donutchartsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.gstormdev.donutchart.DonutChart

class MainActivity : AppCompatActivity() {
    private var value = 75
    private lateinit var donut: DonutChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        donut = findViewById(R.id.donut)
        val btnIncrease = findViewById<Button>(R.id.btn_increase)
        val btnDecrease = findViewById<Button>(R.id.btn_decrease)

        setDonutValues()

        btnIncrease.setOnClickListener {
            value += 10
            setDonutValues()
        }

        btnDecrease.setOnClickListener {
            value -= 10
            setDonutValues()
        }
    }

    private fun setDonutValues() {
        donut.setPercentage(value/100f)
        donut.text = value.toString()
    }
}