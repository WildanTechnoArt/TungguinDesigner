package com.hyperdev.tungguindesigner.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.model.TestimoniHistori.TestimoniItem

class TestimoniHistoriAdapter(private var testimoniItem: ArrayList<TestimoniItem>)
    :RecyclerView.Adapter<TestimoniHistoriAdapter.ViewHolder>(){

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        //Deklarasi View
        val getListFormattedDate: TextView = itemView.findViewById(R.id.listFormattedDate)
        val getListFormattedAmount: TextView = itemView.findViewById(R.id.listFormattedAmount)
        val getListFormattedID: TextView = itemView.findViewById(R.id.listFormattedID)
        val getListRating: RatingBar = itemView.findViewById(R.id.rating)
        val getTestimoniDesigner: TextView = itemView.findViewById(R.id.testimoni_designer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.testimoni_layout_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = testimoniItem.size

    fun refreshAdapter(testimoniList: List<TestimoniItem>) {
        this.testimoniItem.addAll(testimoniList)
        notifyItemRangeChanged(0, this.testimoniItem.size)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val getDate = testimoniItem[position].formattedDate.toString()
        val getAmount = testimoniItem[position].designerTipFormatted.toString()
        val getID = testimoniItem[position].formattedOrderId.toString()
        val getRating = testimoniItem[position].starRating
        val getTestimoni = testimoniItem[position].designerTestimonial.toString()

        holder.getListFormattedDate.text = getDate
        holder.getListFormattedAmount.text = "Tip: $getAmount"
        holder.getListFormattedID.text = "ID: $getID"
        holder.getListRating.rating = getRating?.toFloat()!!
        holder.getTestimoniDesigner.text = getTestimoni
    }
}