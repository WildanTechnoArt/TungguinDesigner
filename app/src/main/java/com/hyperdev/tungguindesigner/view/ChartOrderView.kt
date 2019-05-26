package com.hyperdev.tungguindesigner.view

import android.content.Context
import com.hyperdev.tungguindesigner.model.chartorder.ChartData
import com.hyperdev.tungguindesigner.model.chartorder.ChartItem

class ChartOrderView {

    interface View{
        fun displayChart(chartItem: List<ChartItem>)
        fun displayChartStat(chartData: ChartData)
        fun displayProgress()
        fun hideProgress()
        fun onChartView()
    }

    interface Presenter{
        fun getChartItem(token: String, context: Context)
        fun onDestroy()
    }

}