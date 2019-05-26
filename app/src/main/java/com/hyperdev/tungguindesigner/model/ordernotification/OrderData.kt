package com.hyperdev.tungguindesigner.model.ordernotification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OrderData (

    @SerializedName("orderId")
    @Expose
    var orderId: String? = null
)