package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import io.reactivex.Observable

class ToggleStatusRepositoryImp(private val baseApiService: BaseApiService) : ToggleStatusRepository{
    override fun toggleStatus(authHeader: String, accept: String, status: String): Observable<ProfileResponse> = baseApiService.setToogleStatus(authHeader, accept, status)
}