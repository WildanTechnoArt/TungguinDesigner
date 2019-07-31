package com.hyperdev.tungguindesigner.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.model.NewOrderItem
import kotlinx.android.synthetic.main.order_item.view.*

class OrderListAdapter(private var item: ArrayList<NewOrderItem>)
    : RecyclerView.Adapter<OrderListAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = item.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val getProductName = item[position].productName
        val getProductAmount = item[position].productPrice
        val getProductCount = item[position].productCount

        holder.itemView.nama_desain.text = getProductName
        holder.itemView.desain_price.text = getProductAmount
        holder.itemView.number_desain.text = "$getProductCount Desain"
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}