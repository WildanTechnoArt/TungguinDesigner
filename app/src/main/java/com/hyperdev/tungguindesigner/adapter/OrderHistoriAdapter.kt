package com.hyperdev.tungguindesigner.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.model.historiorder.HistoriOrderItem

class OrderHistoriAdapter(private var orderItem: ArrayList<HistoriOrderItem>)
    : RecyclerView.Adapter<OrderHistoriAdapter.ViewHolder>(){

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        //Deklarasi View
        val getListFormattedDate: TextView = itemView.findViewById(R.id.listFormattedDate)
        val getListFormattedStatus: TextView = itemView.findViewById(R.id.listFormattedStatus)
        val getListFormattedAmount: TextView = itemView.findViewById(R.id.listFormattedAmount)
        val getListFormattedID: TextView = itemView.findViewById(R.id.listFormattedID)
        val getListDesignerLink: TextView = itemView.findViewById(R.id.listDetailDesignerLink)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.order_layout_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = orderItem.size

    fun refreshAdapter(orderList: List<HistoriOrderItem>) {
        this.orderItem.addAll(orderList)
        notifyItemRangeChanged(0, this.orderItem.size)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val getDate = orderItem[position].formattedDate.toString()
        val getStatus = orderItem[position].statusFormatted?.status.toString()
        val getLabel = orderItem[position].statusFormatted?.label.toString()
        val getAmount = orderItem[position].priceAfterCutFormatted.toString()
        val getID = orderItem[position].formattedId.toString()
        val getDesignerLink = orderItem[position].designerDetailLink.toString()

        holder.getListFormattedDate.text = getDate
        holder.getListFormattedAmount.text = getAmount
        holder.getListFormattedID.text = getID

        when(getStatus){
            "in_progress" -> {
                holder.getListFormattedStatus.setBackgroundResource(R.drawable.order_round_no_hover)
            }
            "pending" -> {
                holder.getListFormattedStatus.setBackgroundResource(R.drawable.orange_round_no_hover)
            }
            "expired" -> {
                holder.getListFormattedStatus.setBackgroundResource(R.drawable.red_round_no_hover)
            }
            "success" -> {
                holder.getListFormattedStatus.setBackgroundResource(R.drawable.order_round_no_hover)
            }
        }
        holder.getListFormattedStatus.text = getLabel

        holder.getListDesignerLink.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        holder.getListDesignerLink.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(getDesignerLink)
            (it.context as Activity).startActivity(intent)
        }
    }
}