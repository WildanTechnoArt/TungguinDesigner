package com.hyperdev.tungguindesigner.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.model.transactionhistori.TransactionItem

class TransactionHistoriAdapter(private var transactionItem: ArrayList<TransactionItem>)
    :RecyclerView.Adapter<TransactionHistoriAdapter.ViewHolder>(){

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // Deklarasi View
        val getListFormattedDate: TextView = itemView.findViewById(R.id.date)
        val getListType: TextView = itemView.findViewById(R.id.type)
        val getListFormattedAmount: TextView = itemView.findViewById(R.id.listFormattedAmount)
        val getDescription: TextView = itemView.findViewById(R.id.description)
    }

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

        holder.getListFormattedDate.text = getDate
        holder.getListType.text = "Type: $getType"

        if(getOrderId != null){
            holder.getDescription.text = "Order ID: $getOrderId"
        }else{
            holder.getDescription.text = "Note: $description"
        }
        holder.getListFormattedAmount.text = getAmount

    }
}