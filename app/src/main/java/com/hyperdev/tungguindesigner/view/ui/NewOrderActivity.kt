package com.hyperdev.tungguindesigner.view.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.adapter.OrderListAdapter
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.model.NewOrderItem
import com.hyperdev.tungguindesigner.model.ordernotification.CheckOrderData
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkUtil
import com.hyperdev.tungguindesigner.presenter.GetOrderPresenter
import com.hyperdev.tungguindesigner.repository.GetOrderRepositoryImpl
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.view.GetOrderView
import kotlinx.android.synthetic.main.activity_new_order.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException
import kotlin.properties.Delegates

class NewOrderActivity : AppCompatActivity(), GetOrderView.View {

    // Del;arasi Variable
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: GetOrderView.Presenter
    private lateinit var orderId: String
    private lateinit var totalHarga: String
    private lateinit var items: String
    private var startTime: Long = 0
    private lateinit var jsonArray: JSONArray
    private lateinit var countdownTimer: CountDownTimer
    private var itemList: ArrayList<NewOrderItem> = arrayListOf()
    private var adapter by Delegates.notNull<OrderListAdapter>()

    // WebSocket
    private var socket: Socket? = null

    private fun initSocket(){
        try{
            socket = IO.socket("https://tungguin-socket.azishapidin.com/")
        }catch (ex: URISyntaxException){
            ex.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_order)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initSocket()
        initData()

        accept.setOnClickListener {
            presenter.acceptRequest(this@NewOrderActivity, "Bearer $token", orderId)
        }

        cancel.setOnClickListener {
            presenter.rejectRequest(this@NewOrderActivity, "Bearer $token", orderId)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData(){

        if(intent.extras != null){
            orderId = intent.getStringExtra("sendOrderID").toString()
            totalHarga = intent.getStringExtra("sendTotalHarga").toString()
            items = intent.getStringExtra("sendItems").toString()

            jsonArray = JSONArray(items)
            for(data in 0 until jsonArray.length()){
                val getProductName = jsonArray.getJSONObject(data).getString("product").toString()
                val getProductPrice = jsonArray.getJSONObject(data).getString("price").toString()
                val getProductCount = jsonArray.getJSONObject(data).getString("options_count").toString()
                val model = NewOrderItem(getProductName, getProductCount, getProductPrice)
                itemList.add(model)
            }

            token = SharedPrefManager.getInstance(this@NewOrderActivity).token.toString()

            baseApiService = NetworkUtil.getClient(this@NewOrderActivity)!!
                .create(BaseApiService::class.java)

            val request = GetOrderRepositoryImpl(baseApiService)
            val scheduler = AppSchedulerProvider()

            val layout = LinearLayoutManager(this@NewOrderActivity)
            orderList.layoutManager = layout
            orderList.setHasFixedSize(false)
            orderList.isNestedScrollingEnabled = false

            adapter = OrderListAdapter(itemList)

            orderList.adapter = adapter

            presenter = GetOrderPresenter(this, request, scheduler)
            presenter.checkOrderOffer("Bearer $token", orderId)

            socket?.on("designer", closeOrderListener)
            socket?.connect()
            socket?.emit("designer", orderId)

            total_harga.text = "Total : $totalHarga"
        }
    }

    private val closeOrderListener = Emitter.Listener { args ->
        runOnUiThread {

            val data = args[0] as JSONObject
            val type: String?

            try{

                type = data.getString("type")

                if(type != null){
                    when(type){
                        "orderPopUpClose" ->{
                            finish()
                        }
                    }
                }
            }catch (ex: JSONException){
                ex.printStackTrace()
            }
        }
    }

    override fun acceptSuccess() {
        progressBar.visibility = View.GONE
        shadow.visibility = View.GONE
        Toast.makeText(this@NewOrderActivity, "Orderan Diterima", Toast.LENGTH_LONG).show()
        startActivity(Intent(this@NewOrderActivity, Dashboard::class.java))
        finish()
    }

    override fun rejectSuccess() {
        progressBar.visibility = View.GONE
        shadow.visibility = View.GONE
        Toast.makeText(this@NewOrderActivity, "Orderan Ditolak", Toast.LENGTH_LONG).show()
        startActivity(Intent(this@NewOrderActivity, Dashboard::class.java))
        finish()
    }

    override fun orderOffer(check: CheckOrderData) {

        if(check.isAvailable!!){
            data_view.visibility = View.VISIBLE
            txt_expired_order.visibility = View.GONE
            startTime = check.remainTime?.times(1000)!!
            countdownTimer = MyCountdownTimer(startTime, interval)
            countdownTimer.start()
        }else{
            data_view.visibility = View.GONE
            txt_expired_order.visibility = View.VISIBLE
        }
    }

    override fun onDisplayProgress() {
        progressBar.visibility = View.VISIBLE
        shadow.visibility = View.VISIBLE
    }

    override fun onHideProgress() {
        progressBar.visibility = View.GONE
        shadow.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    companion object {
        private const val interval: Long = 1 * 1000
    }

    inner class MyCountdownTimer(startTime: Long, interval: Long)
        : CountDownTimer(startTime, interval) {

        override fun onFinish() {
            presenter.rejectRequest(this@NewOrderActivity, "Bearer $token", orderId)
        }

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            timer.text = "${millisUntilFinished / 1000} Sec"
        }
    }
}
