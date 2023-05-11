package com.example.hinoedp.DataQuery

import com.example.hinoedp.Gvariable
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
               orderList.add(resultSet.getString("OrderNo"))
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
                val pId = resultSet.getString("OrderProcess.PId")
                val orderDetailI = resultSet.getString("OrderProcess.OrderDetailId")
                val orderNo = resultSet.getString("OrderProcess.OrderNo")
                val serial = resultSet.getString("OrderProcess.SerialNo")
                val packingDate = resultSet.getString("OrderProcess.PackingDate")
                val receiveDate = resultSet.getString("OrderProcess.ReceiveDate")
                val edpSetting = resultSet.getString("OrderProcess.EDPSettingDate")
                val edpQualityCheckDate = resultSet.getString("OrderProcess.EDPQualityCheckDate")
                val edpStatus = resultSet.getString("OrderProcess.EDPSettingStatus")
                val edpQualityCheckStatus = resultSet.getString("OrderProcess.EDPQualityCheckStatus")
                val deliveryDate = resultSet.getString("OrderProcess.DeliveryDate")
                val partNo = resultSet.getString("OrderProcess.PartNo")
                val qty = resultSet.getInt("OrderProcess.Qty")
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
}