package com.hyperdev.tungguindesigner.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.model.withdrawhistori.WithdrawItem

class WithdrawHistoriAdapter(private var historItem: ArrayList<WithdrawItem>)
    :RecyclerView.Adapter<WithdrawHistoriAdapter.ViewHolder>(){

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        //Deklarasi View
        val getListFormattedDate: TextView = itemView.findViewById(R.id.listFormattedDate)
        val getListFormattedStatus: TextView = itemView.findViewById(R.id.listFormattedStatus)
        val getListFormattedAmount: TextView = itemView.findViewById(R.id.listFormattedAmount)
        val getBankName: TextView = itemView.findViewById(R.id.bank_name)
        val getOwnerName: TextView = itemView.findViewById(R.id.nameOwner)
        val getNote: TextView = itemView.findViewById(R.id.note)
    }

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

        holder.getListFormattedDate.text = getDate
        holder.getListFormattedStatus.text = getStatus
        holder.getListFormattedStatus.setBackgroundColor(Color.parseColor(getColorHex))
        holder.getListFormattedAmount.text = getAmount
        holder.getBankName.text = bankName
        holder.getOwnerName.text = ownerName

        if(noteRefund != "null"){
            holder.getNote.text = "Catatan: $noteRefund"
        }else{
            holder.getNote.text = "Catatan: -"
        }
    }
}