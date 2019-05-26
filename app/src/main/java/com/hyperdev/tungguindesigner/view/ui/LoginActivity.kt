package com.hyperdev.tungguindesigner.view.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.model.login.DataLogin
import com.hyperdev.tungguindesigner.model.login.LoginResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkUtil
import com.hyperdev.tungguindesigner.utils.Validation.Companion.validateEmail
import com.hyperdev.tungguindesigner.utils.Validation.Companion.validateFields
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.NullPointerException

class LoginActivity : AppCompatActivity() {

    //Deklarasi Variable
    private lateinit var emailUser: String
    private lateinit var passUser: String
    private lateinit var baseApiService: BaseApiService
    private var getTypeAction: String? = null
    private var getOrderId: String? = null
    private var getData: JSONObject? = null
    private var getItems: String? = null
    private var totalHarga: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        baseApiService = NetworkUtil.getClient(this@LoginActivity)!!
            .create(BaseApiService::class.java)

        btnLogin.setOnClickListener {
            val connectivity  = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivity.activeNetworkInfo

            if (networkInfo != null && networkInfo.isConnected) {
                disableView()
                login()
            } else {
                Toast.makeText(this@LoginActivity, "Tidak terhubung dengan internet !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val getUserToken = SharedPrefManager.getInstance(this@LoginActivity).token
        if(getUserToken != null){

            if(intent?.extras != null){

                try{
                    getTypeAction = intent?.extras?.get("type").toString()
                    getData = JSONObject(intent.getStringExtra("data").toString())
                    getOrderId = getData?.getString("orderId").toString()
                    getItems = getData?.getString("items").toString()
                    totalHarga = getData?.getString("total").toString()
                }catch (ex: NullPointerException){
                    ex.printStackTrace()
                }finally {
                    when(getTypeAction){
                        "orderAsk" -> {
                            val intent = Intent(this@LoginActivity, NewOrderActivity::class.java)
                            intent.putExtra("sendOrderID", getOrderId)
                            intent.putExtra("sendItems", getItems)
                            intent.putExtra("sendTotalHarga", totalHarga)
                            startActivity(intent)
                            finish()
                        }
                        "order_accepted" -> {
                            startActivity(Intent(this@LoginActivity, Dashboard::class.java))
                            intent.putExtra("toHistoriOrder", true)
                            startActivity(intent)
                            finish()
                        }else -> {
                        startActivity(Intent(this@LoginActivity, Dashboard::class.java))
                        finish()
                        }
                    }
                }

            }else{
                startActivity(Intent(this@LoginActivity, Dashboard::class.java))
                finish()
            }
        }
    }

    private fun login(){

        //Memasukan DataUser User Pada Variable
        emailUser = email.text.toString()
        passUser = pass.text.toString()

        var err = 0

        if(!validateEmail(emailUser)){
            err++
            email.error = "Email tidak valid !"
        }

        if(!validateFields(passUser)){
            err++
            pass.error = "Password tidak boleh kosong !"
        }

        if(err == 0){
            loginRequest(emailUser, passUser)
        }else{
            enableView()
        }
    }

    private fun loginRequest(emailUser: String, passUser: String){
        baseApiService.loginRequest(emailUser, passUser)
            .enqueue(object : Callback<LoginResponse> {

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("RegisterPage.kt", "onFailure ERROR -> "+ t.message)
                    showSnackBarMessage("Tidak terhubung dengan internet !")
                    enableView()
                }

                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                        try {
                            val data: DataLogin = response.body()?.getData!!
                            SharedPrefManager.getInstance(this@LoginActivity).storeToken(data.token.toString())
                            startActivity(Intent(this@LoginActivity, Dashboard::class.java))
                            finishAffinity()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.i("debug", "onResponse: GA BERHASIL")
                        enableView()
                        val gson = Gson()
                        val message = gson.fromJson(response.errorBody()?.charStream(), LoginResponse::class.java)
                        if(message != null){
                            Toast.makeText(this@LoginActivity, message.getMeta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun disableView(){
        shadow.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false
        email.isEnabled = false
        pass.isEnabled = false
    }

    private fun enableView(){
        shadow.visibility = View.GONE
        progressBar.visibility = View.GONE
        btnLogin.isEnabled = true
        email.isEnabled = true
        pass.isEnabled = true
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(login_page, message, Snackbar.LENGTH_SHORT).show()
    }
}
