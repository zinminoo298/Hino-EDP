package com.example.hinoedp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView

class EDPSetting : AppCompatActivity() {
    companion object{
        lateinit var textViewDate: TextView
        lateinit var textViewPartNo: TextView
        lateinit var textViewTotalEDP: TextView
        lateinit var spinnerOrderNo: Spinner
        lateinit var spinnerStatus: Spinner
        lateinit var editTextViewSKB: TextView
        lateinit var buttonList: Button
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edpsetting)

        textViewDate = findViewById(R.id.textview_date)
        textViewPartNo = findViewById(R.id.textView_partNo)
        textViewTotalEDP = findViewById(R.id.textview_edp)
        spinnerOrderNo = findViewById(R.id.spinner_orderNo)
        spinnerStatus = findViewById(R.id.spinner_status)
        editTextViewSKB = findViewById(R.id.editText_skb)
        buttonList = findViewById(R.id.button_list)

        textViewDate.setOnClickListener {

        }

    }
}