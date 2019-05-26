package com.hyperdev.tungguindesigner.fcm

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.widget.Toast
import com.hyperdev.tungguindesigner.view.ui.NewOrderActivity
import org.json.JSONObject

class BackgroundService : IntentService(BackgroundService::class.simpleName) {

    private var getTypeAction: String? = null
    private var getOrderId: String? = null
    private var getData: JSONObject? = null
    private var getItems: String? = null
    private var totalHarga: String? = null

    init {
        setIntentRedelivery(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show()
        return Service.START_NOT_STICKY
    }

    override fun onHandleIntent(intent: Intent?) {
        synchronized(true){
            if(intent?.extras != null){
                Toast.makeText(this, "Intent Ditemukan", Toast.LENGTH_LONG).show()
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
                            val intent2 = Intent(this, NewOrderActivity::class.java)
                            intent2.putExtra("sendOrderID", getOrderId)
                            intent2.putExtra("sendItems", getItems)
                            intent2.putExtra("sendTotalHarga", totalHarga)
                            startActivity(intent2)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Service Stoped", Toast.LENGTH_LONG).show()
    }
}