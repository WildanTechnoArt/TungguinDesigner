package com.hyperdev.tungguindesigner.view.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.hyperdev.tungguindesigner.network.NetworkClient
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import kotlinx.android.synthetic.main.activity_new_order.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException
import kotlin.properties.Delegates
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.provider.Settings
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.hyperdev.tungguindesigner.BuildConfig
import com.hyperdev.tungguindesigner.model.profile.DataUser
import com.hyperdev.tungguindesigner.presenter.NewOrderPresenter
import com.hyperdev.tungguindesigner.utils.UtilsContant.Companion.HASHED_ID
import com.hyperdev.tungguindesigner.view.NewOrderView

class NewOrderActivity : AppCompatActivity(), NewOrderView.View{

    // Deklarasi Variable
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: NewOrderView.Presenter
    private lateinit var orderId: String
    private lateinit var totalHarga: String
    private lateinit var items: String
    private var startTime: Long = 0
    private lateinit var jsonArray: JSONArray
    private lateinit var countdownTimer: CountDownTimer
    private var itemList: ArrayList<NewOrderItem> = arrayListOf()
    private var adapter by Delegates.notNull<OrderListAdapter>()
    private lateinit var myVibrator: Vibrator
    private lateinit var mediaPlayer: MediaPlayer

    // WebSocket
    private var socket: Socket? = null

    private fun initSocket(){
        try{
            socket = IO.socket(BuildConfig.WEBSOCKET_URL)
        }catch (ex: URISyntaxException){
            ex.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n", "WakelockTimeout")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_order)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mediaPlayer = MediaPlayer.create(this@NewOrderActivity, Settings.System.DEFAULT_RINGTONE_URI)

        initSocket()
        initData()

        accept.setOnClickListener {
            if(mediaPlayer.isPlaying){
                mediaPlayer.stop()
            }
            presenter.acceptRequest(this@NewOrderActivity, "Bearer $token", orderId)
        }

        cancel.setOnClickListener {
            if(mediaPlayer.isPlaying){
                mediaPlayer.stop()
            }
            presenter.rejectRequest(this@NewOrderActivity, "Bearer $token", orderId)
        }

        refreshLayout.setOnRefreshListener {
            presenter.checkOrderOffer("Bearer $token", orderId)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData(){

        if(intent.extras != null){
            orderId = intent?.getStringExtra(HASHED_ID).toString()
            totalHarga = intent?.getStringExtra("sendTotalHarga").toString()
            items = intent?.getStringExtra("sendItems").toString()

            jsonArray = JSONArray(items)
            for(data in 0 until jsonArray.length()){
                val getProductName = jsonArray.getJSONObject(data).getString("product").toString()
                val getProductPrice = jsonArray.getJSONObject(data).getString("price").toString()
                val getProductCount = jsonArray.getJSONObject(data).getString("options_count").toString()
                val model = NewOrderItem(getProductName, getProductCount, getProductPrice)
                itemList.add(model)
            }

            token = SharedPrefManager.getInstance(this@NewOrderActivity).token.toString()

            baseApiService = NetworkClient.getClient(this@NewOrderActivity)!!
                .create(BaseApiService::class.java)

            val scheduler = AppSchedulerProvider()

            val layout = LinearLayoutManager(this@NewOrderActivity)
            orderList.layoutManager = layout
            orderList.setHasFixedSize(false)
            orderList.isNestedScrollingEnabled = false

            adapter = OrderListAdapter(itemList)

            orderList.adapter = adapter

            presenter = NewOrderPresenter(this, baseApiService, scheduler)
            presenter.checkOrderOffer("Bearer $token", orderId)
            presenter.getUserProfile("Bearer $token", this@NewOrderActivity)

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

    override fun displayProfile(profileItem: DataUser) {
        socket?.on("designer", closeOrderListener)
        socket?.connect()
        socket?.emit("designer", profileItem.hashedId.toString())
    }

    override fun acceptSuccess() {
        mediaPlayer.stop()
        refreshLayout.isRefreshing = false
        Toast.makeText(this@NewOrderActivity, "Orderan Diterima", Toast.LENGTH_LONG).show()
        val intent = Intent(this@NewOrderActivity, Dashboard::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun rejectSuccess() {
        mediaPlayer.stop()
        refreshLayout.isRefreshing = false
        Toast.makeText(this@NewOrderActivity, "Orderan Ditolak", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun orderOffer(check: CheckOrderData) {

        if(check.isAvailable!!){
            data_view.visibility = View.VISIBLE
            txt_expired_order.visibility = View.GONE
            startTime = check.remainTime?.times(1000)!!
            countdownTimer = MyCountdownTimer(startTime, interval)
            countdownTimer.start()
            mediaPlayer.start()
            deviceVibrate()
        }else{
            data_view.visibility = View.GONE
            txt_expired_order.visibility = View.VISIBLE
        }
    }

    private fun deviceVibrate(){
        // Set Alarm Animation
        val shake = AnimationUtils.loadAnimation(this@NewOrderActivity, R.anim.shakeanim)
        notifyImage.animation = shake

        myVibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            myVibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            myVibrator.vibrate(1000)
        }
    }

    override fun onDisplayProgress() {
        refreshLayout.isRefreshing = true
    }

    override fun onHideProgress() {
        refreshLayout.isRefreshing = false
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onBackPressed() {
        // Dissable BackPressed
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
