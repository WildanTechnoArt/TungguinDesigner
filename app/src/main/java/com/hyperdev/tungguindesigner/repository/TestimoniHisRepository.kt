package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.TestimoniHistori.TestimoniResponse
import io.reactivex.Flowable

interface TestimoniHisRepository {
    fun getTestimoniHistori(token: String, accept: String, page: Int) : Flowable<TestimoniResponse>
}