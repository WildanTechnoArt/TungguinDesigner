package com.hyperdev.tungguindesigner.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.model.NewOrderItem

class OrderListAdapter(private var item: ArrayList<NewOrderItem>)
    :RecyclerView.Adapter<OrderListAdapter.ViewHolder>(){

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        //Deklarasi View
        val listProductName: TextView = itemView.findViewById(R.id.nama_desain)
        val listFormattedAmount: TextView = itemView.findViewById(R.id.desain_price)
        val listProductCount: TextView = itemView.findViewById(R.id.number_desain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.order_item_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = item.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val getProductName = item[position].productName
        val getProductAmount = item[position].productPrice
        val getProductCount = item[position].productCount

        holder.listProductName.text = getProductName
        holder.listFormattedAmount.text = getProductAmount
        holder.listProductCount.text = "$getProductCount Desain"
    }
}