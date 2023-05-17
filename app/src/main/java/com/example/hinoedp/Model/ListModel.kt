package com.example.hinoedp.Model

class ListModel {
    var orderDetailId:String? = null
    var orderNo:String? = null
    var orderDate:String? = null
    var kpbNo:String? = null
    var maker:String? = null
    var partNo:String? = null
    var partName:String? = null
    var qty:Int? = null
    var delDate:String? = null
    var edp:String? = null

    constructor(
        orderDetailId: String?,
        orderNo: String?,
        orderDate: String?,
        kpbNo: String?,
        maker: String?,
        partNo: String?,
        partName: String?,
        qty: Int?,
        delDate: String?,
        edp: String?,
        qc: String?
    ) {
        this.orderDetailId = orderDetailId
        this.orderNo = orderNo
        this.orderDate = orderDate
        this.kpbNo = kpbNo
        this.maker = maker
        this.partNo = partNo
        this.partName = partName
        this.qty = qty
        this.delDate = delDate
        this.edp = edp
        this.qc = qc
    }

    var qc:String? = null
}