package com.hyperdev.tungguindesigner.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Response (

    @SerializedName("meta")
    @Expose
    var meta: ErrorResponse? = null
)