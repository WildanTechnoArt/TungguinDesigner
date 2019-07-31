package com.hyperdev.tungguindesigner.fcm

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hyperdev.tungguindesigner.utils.UtilsContant.Companion.HASHED_ID
import com.hyperdev.tungguindesigner.view.ui.NewOrderActivity
import org.json.JSONObject

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FcmReceiver : BroadcastReceiver() {

    private var getTypeAction: String? = null
    private var getOrderId: String? = null
    private var getData: JSONObject? = null
    private var getItems: String? = null
    private var totalHarga: String? = null

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.extras != null){
            try{
                getTypeAction = intent.extras?.get("type").toString()
                getData = JSONObject(intent.getStringExtra("data").toString())
                getOrderId = getData?.getString("orderId").toString()
                getItems = getData?.getString("items").toString()
                totalHarga = getData?.getString("total").toString()
            }catch (ex: NullPointerException){
                ex.printStackTrace()
            }finally {
                when(getTypeAction){
                    "orderAsk" -> {
                        val intent2 = Intent(context, NewOrderActivity::class.java)
                        intent2.putExtra(HASHED_ID, getOrderId)
                        intent2.putExtra("sendItems", getItems)
                        intent2.putExtra("sendTotalHarga", totalHarga)
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        context?.startActivity(intent2)
                    }
                }
            }
        }
    }
}