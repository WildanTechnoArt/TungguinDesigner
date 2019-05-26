package com.hyperdev.tungguindesigner.model.withdrawhistori

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WithdrawItem (

    @SerializedName("bank_name")
    @Expose
    var bankName : String? = null,

    @SerializedName("bank_owner")
    @Expose
    var bankOwner : String? = null,

    @SerializedName("bank_account")
    @Expose
    var bankAccount : String? = null,

    @SerializedName("bank_branch")
    @Expose
    var bankBranch : String? = null,

    @SerializedName("refund_note")
    @Expose
    var refundNote : String? = null,

    @SerializedName("formatted_id")
    @Expose
    var formattedId : String? = null,

    @SerializedName("formatted_amount")
    @Expose
    var formattedAmount : String? = null,

    @SerializedName("formatted_date")
    @Expose
    var formattedDate : String? = null,

    @SerializedName("formatted_status")
    @Expose
    var formattedStatus : String? = null,

    @SerializedName("status_color_hex")
    @Expose
    var statusColorHex : String? = null
)