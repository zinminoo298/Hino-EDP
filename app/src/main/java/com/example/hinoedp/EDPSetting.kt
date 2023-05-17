package com.example.hinoedp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hinoedp.DataQuery.OrderQuery
import com.example.hinoedp.Model.ListModel
import com.example.hinoedp.Model.OrderDetailModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List


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
        var strTotal = ""
        var edpStatus:String = ""
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

        textViewDate.text = "${String.format("%02d",day)}/${String.format("%02d",month+1)}/$year"

        spinnerOrderOnChange()

        textViewDate.setOnClickListener {
            datePicker()
        }

        buttonList.setOnClickListener {
            asyncListOrder(spinnerOrderNo.selectedItem.toString())
        }

        editTextViewSKB.setOnKeyListener(View.OnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                editTextViewSKB.text = editTextViewSKB.text.toString().uppercase()
                if(spinnerOrderNo.selectedItemPosition != 0){
                    asyncScanSKB()
                }
                else{
                    Gvariable().alarm(this)
                    Gvariable().messageAlertDialog(this, "กรุณาเลือกเลขที่ Order", layoutInflater)
                }
                editTextViewSKB.setSelectAllOnFocus(true)
                editTextViewSKB.requestFocus()
            }
            false
        })

    }

    private fun datePicker(){
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, myear, mmonth, mday ->
            day = mday
            month = mmonth
            year = myear

            val date = "${String.format("%02d",day)}/${String.format("%02d",month+1)}/$year"
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

    private fun asyncScanSKB(){
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
                    editTextViewSKB.setSelectAllOnFocus(true)
                    editTextViewSKB.requestFocus()
                    progressDialogBuilder.cancel()
                    if(edpStatus.isNotEmpty()){
                        if(edpStatus != spinnerStatus.selectedItem.toString()){
                            if(edpStatus == "Repair" || edpStatus == "OK" || edpStatus == "NG"){
                                //show dialog
                                alertDialog("ต้องการเปลี่ยนสถานะหรือไม่ ($edpStatus -> ${spinnerStatus.selectedItem.toString()})")
                            }
                            else{
                                Gvariable().alarm(this@EDPSetting)
                                Gvariable().messageAlertDialog(this@EDPSetting, "ไม่สามารถเปลี่ยนสถานะของชิ้นงานได้", layoutInflater)
                            }
                        }
                        else{
                            Gvariable().alarm(this@EDPSetting)
                            Gvariable().messageAlertDialog(this@EDPSetting, "ชิ้นงานผ่านตรวจรับ EDP แล้ว (Receive EDP Process)", layoutInflater)
                        }
                    }
                }
            } else {
                deferred.await()
            }
        }
    }

    private fun scanSKB(){
        strTotal = ""
        edpStatus = ""
        orderDetailList.clear()
        orderDetailList = OrderQuery().getOrderDetail(editTextViewSKB.text.toString().replace("|","-"), spinnerOrderNo.selectedItem.toString(), "$year${String.format("%02d",month+1)}${String.format("%02d",day)}")

        if(orderDetailList.isNotEmpty()){
            //check part no = EDP?
            if(OrderQuery().checkEDP(orderDetailList[0].partNo!!)){
                when {
                    orderDetailList[0].receiveDate.isNullOrEmpty() -> {
                        Gvariable().alarm(this)
                        Gvariable().messageAlertDialog(this, "ชิ้นงานยังไม่ผ่านการตรวจรับ (No Receive Process)", layoutInflater)
                    }

                    orderDetailList[0].packingDate!!.isNotEmpty() -> {
                        Gvariable().alarm(this)
                        Gvariable().messageAlertDialog(this, "ชชิ้นงานถูก Packing เรียบร้อยแล้ว ไม่สามารถรับชิ้นงานได้", layoutInflater)
                    }

                    else -> {
                        if(orderDetailList[0].edpSettingDate.isNullOrEmpty()){
                            OrderQuery().updateOrderProcessEDPSetting(orderDetailList[0].pId!!, spinnerStatus.selectedItem.toString())
                            OrderQuery().writeLog("Scan EDP Program", "Order Process", "Scan Receive EDP Serial Kanban : ${editTextViewSKB.text.toString().trim()}", editTextViewSKB.text.toString().trim(), Gvariable.userName!!)
                            Handler(Looper.getMainLooper()).post {
                                textViewPartNo.text = orderDetailList[0].partNo
                            }
                        }
                        else{
                            //check user level
                            if(Gvariable.type == "Admin"){
                                edpStatus = try{
                                    orderDetailList[0].edpStatus!!
                                }catch (e:Exception){
                                    ""
                                }
                            }
                            else{
                                Gvariable().alarm(this)
                                Gvariable().messageAlertDialog(this, "ชิ้นงานผ่านตรวจรับ EDP แล้ว (Receive EDP Process)", layoutInflater)
                            }
                        }
                        //update EDP receive total
                        strTotal = OrderQuery().getTotal(orderDetailList[0].orderDetailID!!, spinnerOrderNo.selectedItem.toString(), "$year${String.format("%02d",month+1)}${String.format("%02d",day)}", "EDPSettingDate is not null").toString()
                        Handler(Looper.getMainLooper()).post {
                            textViewTotalEDP.text = strTotal + "/" + orderDetailList[0].qty
                        }
                    }
                }

            }
            else{
                Gvariable().alarm(this)
                Gvariable().messageAlertDialog(this, "ผิดพลาด: ชิ้นงานไม่ต้องผ่านขึ้นตอน EDP กรุณาแยกชิ้นงานออก", layoutInflater)
            }
        }else{
            Gvariable().alarm(this)
            Gvariable().messageAlertDialog(this, "ไม่พบเลขที่ Serial ใน Order นี้ กรุณาเลือกเลขที่ Order ใหม่", layoutInflater)
        }
    }

    private fun alertDialog(text:String){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.alert_dialog   , null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.setCancelable(false)
        val buttonYes = findViewById<Button>(R.id.button_yes)
        val buttonNo = findViewById<Button>(R.id.button_no)
        val textView = findViewById<TextView>(R.id.textview_text)

        textView.text = text

        buttonYes.setOnClickListener {
            edpStatus = if(edpStatus == "Repair"){
                edpStatus + "->" + spinnerStatus.selectedItem.toString()
            } else{
                spinnerStatus.selectedItem.toString()
            }
            //update and write log
            OrderQuery().updateOrderProcessEDPSetting(orderDetailList[0].pId!!, edpStatus)
            OrderQuery().writeLog("Scan EDP Program", "Order Process", "Re-Scan change EDP status  : ${editTextViewSKB.text.toString().trim()}", editTextViewSKB.text.toString().trim(), Gvariable.userName!!)
            textViewPartNo.text = orderDetailList[0].partNo
            dialog.dismiss()
        }

        buttonNo.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun asyncListOrder(orderNo:String){
        val deferred = lifecycleScope.async(Dispatchers.IO) {
            Gvariable.searchOrderList = OrderQuery().loadDataOrderDetail(orderNo)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            if (deferred.isActive) {
                val progressDialogBuilder = Gvariable().createProgressDialog(this@EDPSetting)
                try {
                    progressDialogBuilder.show()
                    deferred.await()
                } finally {
                    progressDialogBuilder.cancel()
                    val intent = Intent(this@EDPSetting, com.example.hinoedp.List::class.java)
                    startActivity(intent)
//                    Toast.makeText(this@EDPSetting, "DONE",Toast.LENGTH_LONG).show()
                }
            } else {
                deferred.await()
            }
        }
    }
}