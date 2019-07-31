package com.hyperdev.tungguindesigner.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.model.transactionhistori.TransactionItem
import kotlinx.android.synthetic.main.transaction_layout_item.view.*

class TransactionHistoriAdapter(private var transactionItem: ArrayList<TransactionItem>)
    : RecyclerView.Adapter<TransactionHistoriAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = transactionItem.size

    fun refreshAdapter(transactionList: List<TransactionItem>) {
        this.transactionItem.addAll(transactionList)
        notifyItemRangeChanged(0, this.transactionItem.size)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val getDate = transactionItem[position].formattedDate.toString()
        val getType = transactionItem[position].type.toString()
        val getAmount = transactionItem[position].formattedAmount.toString()
        val description = transactionItem[position].metaTransaction?.description.toString()
        val getOrderId = transactionItem[position].metaTransaction?.orderId

        holder.itemView.date.text = getDate
        holder.itemView.type.text = "Type: $getType"

        if(getOrderId != null){
            holder.itemView.description.text = "Order ID: $getOrderId"
        }else{
            holder.itemView.description.text = "Note: $description"
        }
        holder.itemView.listFormattedAmount.text = getAmount
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}