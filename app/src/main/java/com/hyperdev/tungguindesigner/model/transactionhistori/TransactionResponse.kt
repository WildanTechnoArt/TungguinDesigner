package com.hyperdev.tungguindesigner.model.transactionhistori

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hyperdev.tungguindesigner.network.HandleResponse

data class TransactionResponse (

    @SerializedName("meta")
    @Expose
    var meta: HandleResponse? = null,

    @SerializedName("data")
    @Expose
    var data: TransactionData? = null
)