package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.TestimoniHistori.TestimoniResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import io.reactivex.Flowable

class TestimoniHisRepositoryImpl (private val baseApiService: BaseApiService): TestimoniHisRepository {
    override fun getTestimoniHistori(token: String, accept: String, page: Int): Flowable<TestimoniResponse> = baseApiService.getTestimoniHistori(token, accept, page)
}