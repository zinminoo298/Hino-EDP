package com.example.hinoedp.DataQuery.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hinoedp.Model.ListModel
import com.example.hinoedp.R

class ListAdapter (private var Dataset:ArrayList<ListModel>, private val context: Context) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {
    class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view){
        val textViewNo:TextView = view.findViewById(R.id.textview_no)
        val textViewPartNo:TextView = view.findViewById(R.id.textview_partNo)
        val textViewQty:TextView = view.findViewById(R.id.textview_qty)
        val textViewEDP:TextView = view.findViewById(R.id.textview_edp)
        val textViewQC:TextView = view.findViewById(R.id.textview_qc)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val textViewNo = holder.textViewNo
        val textViewPartNo = holder.textViewPartNo
        val textViewQty = holder.textViewQty
        val textViewEDP = holder.textViewEDP
        val textViewQC = holder.textViewQC

        textViewNo.text = ( position + 1 ).toString()
        textViewPartNo.text = Dataset[position].partNo
        textViewQty.text = Dataset[position].qty.toString()
        textViewEDP.text = Dataset[position].edp
        textViewQC.text = Dataset[position].qc
    }

    override fun getItemCount() = Dataset.size
}