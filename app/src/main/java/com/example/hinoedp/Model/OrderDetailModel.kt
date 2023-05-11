package com.example.hinoedp.Model

class OrderDetailModel {
    var pId:String? = null
    var orderDetailI:String? = null
    var orderNo:String? = null
    var serialNo:String? = null
    var packingDate:String? = null
    var receiveDate:String? = null
    var edpSetting:String? = null
    var edpQualityCheckDate:String? = null
    var edpStatus:String? = null
    var edpQualityCheckStatus:String? = null
    var deliveryDate:String? = null
    var partNo:String? = null
    var qty:Int? = null

    constructor(
        pId: String?,
        orderDetailI: String?,
        orderNo: String?,
        serialNo: String?,
        packingDate: String?,
        receiveDate: String?,
        edpSetting: String?,
        edpQualityCheckDate: String?,
        edpStatus: String?,
        edpQualityCheckStatus: String?,
        deliveryDate: String?,
        partNo: String?,
        qty: Int?
    ) {
        this.pId = pId
        this.orderDetailI = orderDetailI
        this.orderNo = orderNo
        this.serialNo = serialNo
        this.packingDate = packingDate
        this.receiveDate = receiveDate
        this.edpSetting = edpSetting
        this.edpQualityCheckDate = edpQualityCheckDate
        this.edpStatus = edpStatus
        this.edpQualityCheckStatus = edpQualityCheckStatus
        this.deliveryDate = deliveryDate
        this.partNo = partNo
        this.qty = qty
    }
}