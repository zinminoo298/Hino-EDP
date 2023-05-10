package com.example.hinoedp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textViewEDP = findViewById<TextView>(R.id.textview_edp)
        val textViewQC = findViewById<TextView>(R.id.textview_qc)

        textViewEDP.setOnClickListener {
            startActivity(EDPSetting::class.java)
        }

        textViewQC.setOnClickListener {
            startActivity(QualityControl::class.java)
        }
    }

    private fun startActivity(cls:Class<*>){
        val intent = Intent(this,cls)
        startActivity(intent)
    }
}