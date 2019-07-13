package com.hyperdev.tungguindesigner.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HandleResponse (
    @SerializedName("code")
    @Expose
    var code: Int? = null,
    @SerializedName("status")
    @Expose
    var status: String? = null,
    @SerializedName("message")
    @Expose
    var message: String? = null
)