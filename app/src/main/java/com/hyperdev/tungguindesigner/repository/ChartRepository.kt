package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.chartorder.ChartResponse
import io.reactivex.Flowable

interface ChartRepository {
    fun getChartOrder(token: String, accept: String) : Flowable<ChartResponse>
}