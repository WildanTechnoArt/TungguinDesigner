package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import io.reactivex.Observable

interface ToggleStatusRepository {
    fun toggleStatus(authHeader: String, accept: String, status: String) : Observable<ProfileResponse>
}