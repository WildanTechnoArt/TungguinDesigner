package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import io.reactivex.Flowable

class ProfileRepositoryImpl (private val baseApiService: BaseApiService): ProfileRepository {
    override fun getProfile(token: String, accept: String): Flowable<ProfileResponse> = baseApiService.getProfile(token, accept)
}