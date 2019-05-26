package com.hyperdev.tungguindesigner.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DataLogin (
    @SerializedName("token")
    @Expose
    var token: String? = null
)