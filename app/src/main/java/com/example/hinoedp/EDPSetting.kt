package com.example.hinoedp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hinoedp.DataQuery.OrderQuery
import com.example.hinoedp.Model.OrderDetailModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class EDPSetting : AppCompatActivity() {
    companion object{
        lateinit var textViewDate: TextView
        lateinit var textViewPartNo: TextView
        lateinit var textViewTotalEDP: TextView
        lateinit var spinnerOrderNo: Spinner
        lateinit var spinnerStatus: Spinner
        lateinit var editTextViewSKB: TextView
        lateinit var buttonList: Button
        var initialDate:String? = null
        var chosenDate:String? = null
        var orderNoList = ArrayList<String>()
        var statusList = ArrayList<String>()
        var orderDetailList = ArrayList<OrderDetailModel>()
        val c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edpsetting)

        textViewDate = findViewById(R.id.textview_date)
        textViewPartNo = findViewById(R.id.textview_partNo)
        textViewTotalEDP = findViewById(R.id.textview_totaledp)
        spinnerOrderNo = findViewById(R.id.spinner_orderNo)
        spinnerStatus = findViewById(R.id.spinner_status)
        editTextViewSKB = findViewById(R.id.editText_skb)
        buttonList = findViewById(R.id.button_list)

        statusList.add("Select")
        statusList.add("OK")
        statusList.add("NG")
        statusList.add("Repair")
        loadSpinnerStatus()

        textViewDate.text = "$day/${month+1}/$year"

        spinnerOrderOnChange()

        textViewDate.setOnClickListener {
            datePicker()
        }

        editTextViewSKB.setOnKeyListener(View.OnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                editTextViewSKB.text = editTextViewSKB.text.toString().uppercase()
                if(spinnerOrderNo.selectedItemPosition != 0){

                }
            }
            false
        })

    }

    private fun datePicker(){
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, myear, mmonth, mday ->
            day = mday
            month = mmonth
            year = myear

            val date = "$day/${month+1}/$year"
            textViewDate.text = date
            //get List order no
            asyncListOrderNo("$year${String.format("%02d",month+1)}${String.format("%02d",day)}")
        }, year, month, day)
        dpd.show()
    }

    private fun asyncListOrderNo(orderDate:String){
        val deferred = lifecycleScope.async(Dispatchers.IO) {
            orderNoList.clear()
            orderNoList = OrderQuery().showOrder(orderDate)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            if (deferred.isActive) {
                val progressDialogBuilder = Gvariable().createProgressDialog(this@EDPSetting)

                try {
                    progressDialogBuilder.show()
                    deferred.await()
                } finally {
                    loadSpinnerOrderNo()
                    progressDialogBuilder.cancel()
                }
            } else {
                deferred.await()
            }
        }
    }

    private fun loadSpinnerOrderNo(){
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, orderNoList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOrderNo.adapter = arrayAdapter
    }

    private fun loadSpinnerStatus(){
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statusList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = arrayAdapter
    }

    private fun spinnerOrderOnChange(){
        spinnerOrderNo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                spinnerStatus.setSelection(0)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
    }

    private fun asyncScanSKB(orderDate:String){
        val deferred = lifecycleScope.async(Dispatchers.IO) {
            scanSKB()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            if (deferred.isActive) {
                val progressDialogBuilder = Gvariable().createProgressDialog(this@EDPSetting)

                try {
                    progressDialogBuilder.show()
                    deferred.await()
                } finally {
                    progressDialogBuilder.cancel()
                }
            } else {
                deferred.await()
            }
        }
    }

    private fun scanSKB(){
        var strTotal = ""
        var edpStatus:String = ""
        orderDetailList.clear()
        orderDetailList = OrderQuery().getOrderDetail(editTextViewSKB.text.toString().replace("|","-"), spinnerOrderNo.selectedItem.toString(), textViewDate.text.toString())

        if(orderDetailList.isNotEmpty()){
            //check part no = EDP?
            if(OrderQuery().checkEDP(orderDetailList[0].partNo!!)){
                when {
                    orderDetailList[0].receiveDate.isNullOrEmpty() -> {

                    }

                    orderDetailList[0].packingDate!!.isNotEmpty() || orderDetailList[0].packingDate != null -> {

                    }

                    else -> {

                    }
                }

            }
        }else{
            Gvariable().alarm(this)
            Gvariable().messageAlertDialog(this, "ไม่พบเลขที่ Serial ใน Order นี้ กรุณาเลือกเลขที่ Order ใหม่", layoutInflater)
        }
    }
}