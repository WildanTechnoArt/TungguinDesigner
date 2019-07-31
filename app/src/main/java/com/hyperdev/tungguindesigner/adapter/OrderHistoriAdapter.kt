package com.hyperdev.tungguindesigner.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.model.historiorder.HistoryOrderItem
import com.hyperdev.tungguindesigner.utils.UtilsContant.Companion.HASHED_ID
import com.hyperdev.tungguindesigner.view.ui.DetailOrderActivity
import kotlinx.android.synthetic.main.history_order_item.view.*

class OrderHistoriAdapter(private var orderItem: ArrayList<HistoryOrderItem>) :
    RecyclerView.Adapter<OrderHistoriAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.history_order_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = orderItem.size

    fun refreshAdapter(orderList: List<HistoryOrderItem>) {
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
        val getOrderId = orderItem[position].hashedId.toString()

        holder.itemView.listFormattedDate.text = getDate
        holder.itemView.listFormattedAmount.text = getAmount
        holder.itemView.listFormattedID.text = getID

        when (getStatus) {
            "in_progress" -> {
                holder.itemView.listFormattedStatus.setBackgroundResource(R.drawable.order_round_no_hover)
            }
            "pending" -> {
                holder.itemView.listFormattedStatus.setBackgroundResource(R.drawable.orange_round_no_hover)
            }
            "expired" -> {
                holder.itemView.listFormattedStatus.setBackgroundResource(R.drawable.red_round_no_hover)
            }
            "success" -> {
                holder.itemView.listFormattedStatus.setBackgroundResource(R.drawable.order_round_no_hover)
            }
        }
        holder.itemView.listFormattedStatus.text = getLabel

        holder.itemView.btn_detail_order.setOnClickListener {
            val intent = Intent(it.context, DetailOrderActivity::class.java)
            intent.putExtra(HASHED_ID, getOrderId)
            (it.context as Activity).startActivity(intent)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}