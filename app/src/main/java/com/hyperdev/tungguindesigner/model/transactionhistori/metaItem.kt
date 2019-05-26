package com.hyperdev.tungguindesigner.model.transactionhistori

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MetaItem (

    @SerializedName("type")
    @Expose
    var typIteme: String? = null,

    @SerializedName("withdrawId")
    @Expose
    var withdrawId: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("orderId")
    @Expose
    var orderId: String? = null
)