package com.example.hinoedp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.example.hinoedp.DataQuery.OrderQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearLayoutEDP = findViewById<LinearLayout>(R.id.linearLayout_edp)
        val linearLayoutQC = findViewById<LinearLayout>(R.id.linearLayout_qc)
        var edpState = false
        var qcState = false

        for(i in 0 until Gvariable.accessList.size){
            if(Gvariable.accessList[i] == "EDP_1"){
                edpState = true
                linearLayoutEDP.backgroundTintList = AppCompatResources.getColorStateList(this, R.color.blue)
            }
            if(Gvariable.accessList[i] == "EDP_2"){
                qcState = true
                linearLayoutQC.backgroundTintList =  AppCompatResources.getColorStateList(this, R.color.red_80)
            }
        }

        linearLayoutEDP.setOnClickListener {
            if(edpState){
                asyncEDP()
            }
        }

        linearLayoutQC.setOnClickListener {
            if(qcState){
                asyncQC()
            }
        }
    }

    private fun startActivity(cls:Class<*>){
        val intent = Intent(this,cls)
        startActivity(intent)
    }

    private fun asyncEDP(){
        val deferred = lifecycleScope.async(Dispatchers.IO) {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val orderDate = "$year${String.format("%02d",month+1)}${String.format("%02d",day)}"
            EDPSetting.orderNoList.clear()
            EDPSetting.orderNoList = OrderQuery().showOrder(orderDate)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            if (deferred.isActive) {
                val progressDialogBuilder = Gvariable().createProgressDialog(this@MainActivity)

                try {
                    progressDialogBuilder.show()
                    deferred.await()
                } finally {
                    progressDialogBuilder.cancel()
                    startActivity(EDPSetting::class.java)
                }
            } else {
                deferred.await()
            }
        }
    }

    private fun asyncQC(){
        val deferred = lifecycleScope.async(Dispatchers.IO) {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val orderDate = "$year${String.format("%02d",month+1)}${String.format("%02d",day)}"
            QualityControl.orderNoList.clear()
            QualityControl.orderNoList = OrderQuery().showOrder(orderDate)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            if (deferred.isActive) {
                val progressDialogBuilder = Gvariable().createProgressDialog(this@MainActivity)

                try {
                    progressDialogBuilder.show()
                    deferred.await()
                } finally {
                    progressDialogBuilder.cancel()
                    startActivity(QualityControl::class.java)
                }
            } else {
                deferred.await()
            }
        }
    }
}