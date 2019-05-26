package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.chartorder.ChartResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import io.reactivex.Flowable

class ChartRepositoryImpl (private val baseApiService: BaseApiService): ChartRepository {
    override fun getChartOrder(token: String, accept: String): Flowable<ChartResponse> = baseApiService.getChartOrder(token, accept)
}