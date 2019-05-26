package com.hyperdev.tungguindesigner.model.transactionhistori

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TransactionItem (

    @SerializedName("type")
    @Expose
    var type: String? = null,

    @SerializedName("meta")
    @Expose
    var metaTransaction: MetaItem? = null,

    @SerializedName("formatted_amount")
    @Expose
    var formattedAmount: String? = null,

    @SerializedName("formatted_date")
    @Expose
    var formattedDate: String? = null
)