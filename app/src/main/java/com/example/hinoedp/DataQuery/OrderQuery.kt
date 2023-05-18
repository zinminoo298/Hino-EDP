package com.example.hinoedp.DataQuery

import com.example.hinoedp.Gvariable
import com.example.hinoedp.Model.ListModel
import com.example.hinoedp.Model.OrderDetailModel
import java.sql.ResultSet

class OrderQuery {
    fun showOrder(date:String): ArrayList<String> {
        var resultSet: ResultSet? = null
        val sql = "select * from [Order] where OrderDate like'$date%'"
        val orderList = ArrayList<String>()
        try {
            Gvariable().startConn()
            val statement = Gvariable.conn!!.createStatement()
            resultSet = statement.executeQuery(sql)
            orderList.add("Select")
           while (resultSet.next()){
               orderList.add(resultSet.getString("OrderNo").trim())
           }
        } catch (e: Exception) {
            e.printStackTrace()
            Gvariable.conn!!.close()
            orderList.clear()
        }
        return orderList
    }

    fun getOrderDetail(serialNo:String, order:String, date:String): ArrayList<OrderDetailModel> {
        var resultSet: ResultSet? = null
        val sql = "SELECT OrderProcess.PId, OrderProcess.OrderDetailId, OrderProcess.OrderNo, OrderProcess.SerialNo, OrderProcess.PackingDate, OrderProcess.ReceiveDate, " +
                "OrderProcess.EDPSettingDate, OrderProcess.EDPQualityCheckDate, OrderProcess.EDPSettingStatus, OrderProcess.EDPQualityCheckStatus, " +
                "OrderProcess.DeliveryDate, OrderDetail.PartNo, OrderDetail.Qty " +
                "FROM OrderProcess INNER JOIN OrderDetail ON OrderProcess.OrderDetailId = OrderDetail.OrderDetailId INNER JOIN [Order] on [Order].OrderNo = OrderDetail.OrderNo and [Order].OrderDate = OrderDetail.OrderDate " +
                "where OrderProcess.SerialNo ='$serialNo' and [Order].OrderNo ='$order' and [Order].OrderDate ='$date'"
        val orderDetailList = ArrayList<OrderDetailModel>()
        try {
            Gvariable().startConn()
            val statement = Gvariable.conn!!.createStatement()
            resultSet = statement.executeQuery(sql)

            if (resultSet.next()){
                val pId = if(resultSet.getString("PId") == null){
                    ""
                }else{
                    resultSet.getString("PId")
                }

                val orderDetailI = if(resultSet.getString("OrderDetailId") == null){
                    ""
                }else{
                    resultSet.getString("OrderDetailId")
                }

                val orderNo = if(resultSet.getString("OrderNo") == null){
                    ""
                }else{
                    resultSet.getString("OrderNo")
                }

                val serial = if(resultSet.getString("SerialNo") == null){
                    ""
                }else{
                    resultSet.getString("SerialNo")
                }

                val packingDate = if(resultSet.getString("PackingDate") == null){
                    ""
                }else{
                    resultSet.getString("PackingDate")
                }

                val receiveDate = if(resultSet.getString("ReceiveDate") == null){
                    ""
                }else{
                    resultSet.getString("ReceiveDate")
                }

                val edpSetting = if(resultSet.getString("EDPSettingDate") == null){
                    ""
                }else{
                    resultSet.getString("EDPSettingDate")
                }

                val edpQualityCheckDate = if(resultSet.getString("EDPQualityCheckDate") == null){
                    ""
                }else{
                    resultSet.getString("EDPQualityCheckDate")
                }

                val edpStatus = if(resultSet.getString("EDPSettingStatus") == null){
                    ""
                }else{
                    resultSet.getString("EDPSettingStatus")
                }

                val edpQualityCheckStatus = if(resultSet.getString("EDPQualityCheckStatus") == null){
                    ""
                }else{
                    resultSet.getString("EDPQualityCheckStatus")
                }

                val deliveryDate = if(resultSet.getString("DeliveryDate") == null){
                    ""
                }else{
                    resultSet.getString("DeliveryDate")
                }

                val partNo = if(resultSet.getString("PartNo") == null){
                    ""
                }else{
                    resultSet.getString("PartNo")
                }

                val qty = if(resultSet.getInt("Qty") == null){
                    0
                }else{
                    resultSet.getInt("Qty")
                }

                orderDetailList.add(OrderDetailModel(pId, orderDetailI, orderNo, serial, packingDate, receiveDate, edpSetting, edpQualityCheckDate, edpStatus, edpQualityCheckStatus, deliveryDate, partNo, qty))
            }
            else{
                orderDetailList.clear()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Gvariable.conn!!.close()
            orderDetailList.clear()
        }
        return orderDetailList
    }

    fun checkEDP(partNo:String) : Boolean{
        var resultSet: ResultSet? = null
        val sql = "SELECT [Part].EDP FROM [Part] WHERE [Part].PartNo = '$partNo'"
        var status = false
        try {
            Gvariable().startConn()
            val statement = Gvariable.conn!!.createStatement()
            resultSet = statement.executeQuery(sql)

            if (resultSet.next()){
               status = resultSet.getString("EDP") == "E"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Gvariable.conn!!.close()
            status = false
        }
        return status
    }

    fun updateOrderProcessEDPQC(orderProcessId:String, orderStatus:String) : Boolean{
        return try{
            Gvariable().startConn()
            val statement = Gvariable.conn!!.createStatement()
            val sql = "update [OrderProcess] set " +
                    " EDPQualityCheckDate = getdate(), EDPQualityCheckBy = '${Gvariable.userName}', EDPQualityCheckStatus= '$orderStatus', " +
                    " LastEditBy = '${Gvariable.userName}', LastEditDate = getdate() " +
                    " where PId = '$orderProcessId' "
            statement.executeUpdate(sql)
            true
        }catch (e:Exception){
            e.printStackTrace()
            Gvariable.conn!!.close()
            false
        }
    }

    fun updateOrderProcessEDPSetting(orderProcessId:String, orderStatus:String) : Boolean{
        return try{
            Gvariable().startConn()
            val statement = Gvariable.conn!!.createStatement()
            val sql = "update [OrderProcess] set " +
                    " EDPSettingDate = getdate(), EDPSettingBy = '${Gvariable.userName}', EDPSettingStatus= '$orderStatus', " +
                    " LastEditBy = '${Gvariable.userName}', LastEditDate = getdate() " +
                    " where PId = '$orderProcessId' "
            statement.executeUpdate(sql)
            true
        }catch (e:Exception){
            e.printStackTrace()
            Gvariable.conn!!.close()
            false
        }
    }

    fun writeLog(job:String, table:String, detail:String, key:String, userName:String){
        val sql = "Insert into LogFile (F_Issued_Date,F_Issued_Time,F_Job,F_Table,F_Detail,F_Program_ID,F_Key_ID,F_Hostname,F_Filler,F_User_ID, F_User_Name, F_System, F_Module) Values " +
                " (convert(varchar, getdate(), 103),'select convert(varchar, getdate(), 108)', '$job', '$table', '$detail', '$job',' $key' ,HOST_NAME(),'','$userName','','Service','$job')  "

        try {
            Gvariable().startConn()
            val statement = Gvariable.conn!!.createStatement()
            statement.executeUpdate(sql)

        } catch (e: Exception) {
            e.printStackTrace()
            Gvariable.conn!!.close()
        }
    }

    fun getTotal(detailId:String, order:String, date:String, process:String) : Int{
        var resultSet: ResultSet? = null
        val sql = "SELECT OrderProcess.PId, OrderProcess.OrderDetailId, OrderProcess.OrderNo, OrderProcess.SerialNo, OrderProcess.PackingDate, OrderProcess.ReceiveDate, " +
                " OrderProcess.DeliveryDate, OrderDetail.PartNo, OrderDetail.Qty " +
                " FROM OrderProcess INNER JOIN OrderDetail ON OrderProcess.OrderDetailId = OrderDetail.OrderDetailId  " +
                " INNER JOIN [Order] on [Order].OrderNo = OrderDetail.OrderNo and [Order].OrderDate = OrderDetail.OrderDate " +
                " where OrderProcess.OrderDetailId ='$detailId' and [Order].OrderNo ='$order' and [Order].OrderDate ='$date' and $process"
        var total = 0
        try {
            Gvariable().startConn()
            val statement = Gvariable.conn!!.createStatement()
            resultSet = statement.executeQuery(sql)

            while (resultSet.next()){
                total ++
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Gvariable.conn!!.close()
            total = 0
        }
        return total
    }

    fun loadDataOrderDetail(order:String) : ArrayList<ListModel>{
        var list = ArrayList<ListModel>()
        var resultSet:ResultSet? = null
        val sql = "select *, CAST( 0 AS INT) AS Delivery, CAST(0 As INT) As Packing, " +
                " CAST(0 As INT) As Receive from [OrderDetail] where OrderNo = '$order' "

        try{
            list.clear()
            Gvariable().startConn()
            val statement = Gvariable.conn!!.createStatement()
            resultSet = statement.executeQuery(sql)

            while(resultSet.next()){
                val orderDetailId = if(resultSet.getString("OrderDetailId") != null){
                    resultSet.getString("OrderDetailId")
                }else{
                    ""
                }

                val orderNo = if(resultSet.getString("OrderNo") != null){
                    resultSet.getString("OrderNo")
                }else{
                    ""
                }

                val orderDate = if(resultSet.getString("OrderDate") != null){
                    resultSet.getString("OrderDate")
                }else{
                    ""
                }

                val kpbNo = if(resultSet.getString("KPBNo") != null){
                    resultSet.getString("KPBNo")
                }else{
                    ""
                }

                val maker = if(resultSet.getString("Maker") != null){
                    resultSet.getString("Maker")
                }else{
                    ""
                }

                val partNo = if(resultSet.getString("PartNo") != null){
                    resultSet.getString("PartNo")
                }else{
                    ""
                }

                val partName = if(resultSet.getString("PartName") != null){
                    resultSet.getString("PartName")
                }else{
                    ""
                }

                val qty = if(resultSet.getInt("Qty") != null){
                    resultSet.getInt("Qty")
                }else{
                    0
                }

                val delDate = if(resultSet.getString("DelDate") != null){
                    resultSet.getString("DelDate")
                }else{
                    ""
                }

                val edp = checkCountEDP(orderDetailId).toString()
                val qc = checkCountQC(orderDetailId).toString()

                list.add(ListModel(orderDetailId, orderNo, orderDate, kpbNo, maker, partNo, partName, qty, delDate, edp, qc))
            }
        }catch (e:Exception){
            e.printStackTrace()
            Gvariable.conn!!.close()
            list.clear()
        }
        return list
    }

    private fun checkCountEDP(orderDetailId:String) : Int{
        var edp = 0
        var resultSet:ResultSet? = null
        val sql = "select OrderDetailId from [OrderProcess] where OrderDetailId ='$orderDetailId' and Isnull(EDPSettingDate,'') <> ''"
        try{
            val statement = Gvariable.conn!!.createStatement()
            resultSet = statement.executeQuery(sql)
            while(resultSet.next()){
                edp ++
            }
        }catch (e:Exception){
            e.printStackTrace()
            edp = 0
        }
        return edp
    }

    private fun checkCountQC(orderDetailId:String) : Int{
        var qc = 0
        var resultSet:ResultSet? = null
        val sql = "select OrderDetailId from [OrderProcess] where OrderDetailId ='$orderDetailId' and Isnull(EDPQualityCheckDate,'') <> ''"
        try{
            val statement = Gvariable.conn!!.createStatement()
            resultSet = statement.executeQuery(sql)
            while(resultSet.next()){
                qc ++
            }
        }catch (e:Exception){
            e.printStackTrace()
            qc = 0
        }
        return qc
    }
}