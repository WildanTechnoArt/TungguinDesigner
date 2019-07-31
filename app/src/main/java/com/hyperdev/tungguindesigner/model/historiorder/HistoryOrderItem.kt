package com.hyperdev.tungguindesigner.model.historiorder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HistoryOrderItem (

    @SerializedName("formatted_id")
    @Expose
    var formattedId: String? = null,

    @SerializedName("price_after_cut_formatted")
    @Expose
    var priceAfterCutFormatted: String? = null,

    @SerializedName("formatted_date")
    @Expose
    var formattedDate: String? = null,

    @SerializedName("status_formatted")
    @Expose
    var statusFormatted: StatusOrderHis? = null,

    @SerializedName("hashed_id")
    @Expose
    var hashedId: String? = null,

    @SerializedName("designer_detail_link")
    @Expose
    var designerDetailLink: String? = null
)