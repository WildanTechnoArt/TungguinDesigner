package com.hyperdev.tungguindesigner.model.transactionhistori

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TransactionData (

    @SerializedName("balance")
    @Expose
    var balance: String? = null,

    @SerializedName("transaction_history")
    @Expose
    var transactionHistory: TransactionHistori? = null
)