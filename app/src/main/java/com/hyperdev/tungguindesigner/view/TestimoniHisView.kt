package com.hyperdev.tungguindesigner.view

import android.content.Context
import com.hyperdev.tungguindesigner.model.TestimoniHistori.TestimoniData
import com.hyperdev.tungguindesigner.model.TestimoniHistori.TestimoniItem

class TestimoniHisView {

    interface View{
        fun loadTestimoniHis(item: List<TestimoniItem>)
        fun loadTestimoniData(testimoni: TestimoniData)
        fun displayProgress()
        fun hideProgress()
        fun onSuccess()
    }

    interface Presenter{
        fun getHistoriTestimoni(token: String, context: Context, page: Int)
        fun onDestroy()
    }

}