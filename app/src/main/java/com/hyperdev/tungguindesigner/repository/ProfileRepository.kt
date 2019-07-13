package com.hyperdev.tungguindesigner.repository

import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ProfileRepository {
    fun getProfile(token: String, accept: String) : Flowable<ProfileResponse>
    fun updateProfile(authHeader: String, accept: String, name: RequestBody,
                      email: RequestBody, phone: RequestBody) : Observable<ProfileResponse>
    fun updateProfileWithImage(authHeader: String, accept: String, name: RequestBody,
                               email: RequestBody, phone: RequestBody,
                               image: MultipartBody.Part) : Observable<ProfileResponse>
}