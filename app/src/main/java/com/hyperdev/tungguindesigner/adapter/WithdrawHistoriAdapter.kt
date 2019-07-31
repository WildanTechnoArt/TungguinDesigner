package com.hyperdev.tungguindesigner.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.model.withdrawhistori.WithdrawItem
import kotlinx.android.synthetic.main.withdraw_layout_item.view.*

class WithdrawHistoriAdapter(private var historItem: ArrayList<WithdrawItem>)
    : RecyclerView.Adapter<WithdrawHistoriAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.withdraw_layout_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = historItem.size

    fun refreshAdapter(historiList: List<WithdrawItem>) {
        this.historItem.addAll(historiList)
        notifyItemRangeChanged(0, this.historItem.size)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val getDate = historItem[position].formattedDate.toString()
        val getStatus = historItem[position].formattedStatus.toString()
        val getAmount = historItem[position].formattedAmount.toString()
        val bankName = historItem[position].bankName.toString()
        val ownerName = historItem[position].bankOwner.toString()
        val noteRefund = historItem[position].refundNote.toString()
        val getColorHex = historItem[position].statusColorHex.toString()

        holder.itemView.listFormattedDate.text = getDate
        holder.itemView.listFormattedStatus.text = getStatus
        holder.itemView.listFormattedStatus.setBackgroundColor(Color.parseColor(getColorHex))
        holder.itemView.listFormattedAmount.text = getAmount
        holder.itemView.bank_name.text = bankName
        holder.itemView.nameOwner.text = ownerName

        if(noteRefund != "null"){
            holder.itemView.note.text = "Catatan: $noteRefund"
        }else{
            holder.itemView.note.text = "Catatan: -"
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}