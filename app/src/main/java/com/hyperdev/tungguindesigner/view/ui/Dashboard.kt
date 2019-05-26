package com.hyperdev.tungguindesigner.view.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.fragment.TestimoniFragment
import com.hyperdev.tungguindesigner.fragment.DashboardFragment
import com.hyperdev.tungguindesigner.fragment.HistoriFragment
import com.hyperdev.tungguindesigner.fragment.ProfileFragment
import com.hyperdev.tungguindesigner.model.fcm.FcmResponse
import com.hyperdev.tungguindesigner.model.profile.DataUser
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkUtil
import com.hyperdev.tungguindesigner.presenter.ProfilePresenter
import com.hyperdev.tungguindesigner.repository.ProfileRepositoryImpl
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.view.ProfileView
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URISyntaxException

class Dashboard : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, ProfileView.View {

    companion object {
        const val PERMISSION_STORAGE = 77
    }

    private lateinit var baseApiService: BaseApiService
    private lateinit var getToken: String
    private lateinit var designerId: String
    private lateinit var presenter: ProfileView.Presenter
    private lateinit var toast: Toast
    private var lastBackPressTime: Long = 0

    // WebSocket
    private var socket: Socket? = null

    private fun initSocket(){
        try{
            socket = IO.socket("https://tungguin-socket.azishapidin.com/")
        }catch (ex: URISyntaxException){
            ex.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initData()

        checkPermission()

        bottom_navigation.setOnNavigationItemSelectedListener(this)

        postTokenFCM()

        initSocket()
    }

    private fun initData(){
        getToken = SharedPrefManager.getInstance(this@Dashboard).token.toString()

        baseApiService = NetworkUtil.getClient(this@Dashboard)!!
            .create(BaseApiService::class.java)

        val request = ProfileRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()

        presenter = ProfilePresenter(this, request, scheduler)
        presenter.getUserProfile("Bearer $getToken", this@Dashboard)

        // Mengaktifkan Kembali FCM
        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        RxPaparazzo.register(application)

        if(intent.extras != null){
            val orderAccept = intent.getBooleanExtra("toHistoriOrder", false)
            if(orderAccept){
                loadFragment(HistoriFragment())
                bottom_navigation.selectedItemId = R.id.histori
            }else{
                loadFragment(DashboardFragment())
            }
        }else{
            loadFragment(DashboardFragment())
        }
    }

    private val newOrderListener = Emitter.Listener { args ->
        runOnUiThread {

            val data = args[0] as JSONObject
            val orderId = data.getJSONObject("data").getString("orderId").toString()
            val totalHarga = data.getJSONObject("data").getString("total").toString()
            val items = data.getJSONObject("data").getJSONArray("items")
            val type: String?

            try{

                type = data.getString("type")

                if(type != null){
                    when(type){
                        "orderAsk" ->{
                            val intent = Intent(this@Dashboard, NewOrderActivity::class.java)
                            intent.putExtra("sendOrderID", orderId)
                            intent.putExtra("sendTotalHarga", totalHarga)
                            intent.putExtra("sendItems", items.toString())
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }catch (ex: JSONException){
                ex.printStackTrace()
            }
        }
    }

    private fun postTokenFCM(){
        val tokenFCM = SharedPrefManager.getInstance(this@Dashboard).tokenFCM.toString()
        baseApiService.fcmRequest("Bearer $getToken", "application/json", tokenFCM)
            .enqueue(object : Callback<FcmResponse> {

                override fun onFailure(call: Call<FcmResponse>, t: Throwable) {
                    Log.e("RegisterPage.kt", "onFailure ERROR -> "+ t.message)
                }

                override fun onResponse(call: Call<FcmResponse>, response: Response<FcmResponse>) {

                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                    } else {
                        Log.i("debug", "onResponse: GA BERHASIL")
                        val gson = Gson()
                        val message = gson.fromJson(response.errorBody()?.charStream(), FcmResponse::class.java)
                        if(message != null){
                            Toast.makeText(this@Dashboard, message.meta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(
                this@Dashboard, android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this@Dashboard, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this@Dashboard, arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_STORAGE
            )

        }
    }

    override fun displayProfile(profileItem: DataUser) {
        designerId = profileItem.hashedId.toString()
        socket?.on("designer", newOrderListener)
        socket?.connect()
        socket?.emit("designer", designerId)
    }

    override fun loadFile(file: FileData?) {}

    override fun displayProgress() {}

    override fun hideProgress() {}

    private fun loadFragment(fragment: Fragment?): Boolean {

        @Suppress("SENSELESS_COMPARISON")
        if(fragment != null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
            return true
        }

        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null

        when(item.itemId){
            R.id.dashboard -> fragment = DashboardFragment()
            R.id.testimoni -> fragment = TestimoniFragment()
            R.id.histori -> fragment = HistoriFragment()
            R.id.profile -> fragment = ProfileFragment()
        }

        return loadFragment(fragment)
    }

    override fun onBackPressed() {


        @Suppress("SENSELESS_COMPARISON")
        if(this.lastBackPressTime < System.currentTimeMillis() - 3000){
            toast = Toast.makeText(this@Dashboard,"Tekan tombol kembali lagi untuk keluar", 3000)
            toast.show()
            this.lastBackPressTime = System.currentTimeMillis()
        }else {
            if(toast != null){
                toast.cancel()
            }
            super.onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        socket?.disconnect()
        socket?.off("designer", newOrderListener)
        presenter.onDestroy()
    }
}