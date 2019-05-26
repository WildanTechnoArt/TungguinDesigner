package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import io.reactivex.Flowable

interface ProfileRepository {
    fun getProfile(token: String, accept: String) : Flowable<ProfileResponse>
}