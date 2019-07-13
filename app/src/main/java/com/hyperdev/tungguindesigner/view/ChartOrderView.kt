package com.hyperdev.tungguindesigner.view

import android.content.Context
import com.hyperdev.tungguindesigner.model.chartorder.ChartData

class ChartOrderView {

    interface View{
        fun displayChartStat(chartData: ChartData)
        fun displayProgress()
        fun hideProgress()
    }

    interface Presenter{
        fun getChartItem(token: String, context: Context)
        fun onDestroy()
    }

}