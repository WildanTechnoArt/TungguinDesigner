package com.hyperdev.tungguindesigner.model.ordernotification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguindesigner.network.ErrorResponse

data class AcceptResponse (

    @SerializedName("meta")
    @Expose
    var meta: ErrorResponse? = null
)