package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import io.reactivex.Observable

interface UpdatePassRepository {
    fun updatePassword(authHeader: String, accept: String, name: String,
                       email: String, phone: String, password: String,
                       c_password: String) : Observable<ProfileResponse>
}