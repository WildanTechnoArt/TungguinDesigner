package com.hyperdev.tungguindesigner.model.ordernotification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguindesigner.network.ErrorResponse

data class CheckOrderResponse (

    @SerializedName("meta")
    @Expose
    val meta: ErrorResponse? = null,

    @SerializedName("data")
    @Expose
    val data: CheckOrderData? = null
)