package com.hyperdev.tungguindesigner.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.model.TestimoniHistori.TestimoniItem
import kotlinx.android.synthetic.main.testimoni_layout_item.view.*

class TestimoniHistoriAdapter(private var testimoniItem: ArrayList<TestimoniItem>)
    : RecyclerView.Adapter<TestimoniHistoriAdapter.ViewHolder>(){

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

        holder.itemView.listFormattedDate.text = getDate
        holder.itemView.listFormattedAmount.text = "Tip: $getAmount"
        holder.itemView.listFormattedID.text = "ID: $getID"
        holder.itemView.rating.rating = getRating?.toFloat()!!
        holder.itemView.testimoni_designer.text = getTestimoni
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}