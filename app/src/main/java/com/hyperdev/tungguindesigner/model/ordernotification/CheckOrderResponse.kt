package com.hyperdev.tungguindesigner.model.ordernotification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguindesigner.network.HandleResponse

data class CheckOrderResponse (

    @SerializedName("meta")
    @Expose
    val meta: HandleResponse? = null,

    @SerializedName("data")
    @Expose
    val data: CheckOrderData? = null
)