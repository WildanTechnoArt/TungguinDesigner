package com.hyperdev.tungguindesigner.view.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkClient
import com.hyperdev.tungguindesigner.presenter.LoginPresenter
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.utils.UtilsContant.Companion.HASHED_ID
import com.hyperdev.tungguindesigner.utils.Validation.Companion.validateEmail
import com.hyperdev.tungguindesigner.utils.Validation.Companion.validateFields
import com.hyperdev.tungguindesigner.view.LoginView
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), LoginView.View {

    //Deklarasi Variable
    private lateinit var emailUser: String
    private lateinit var passUser: String
    private lateinit var baseApiService: BaseApiService
    private var getTypeAction: String? = null
    private var getOrderId: String? = null
    private var getData: JSONObject? = null
    private var getItems: String? = null
    private var totalHarga: String? = null
    private lateinit var presenter: LoginView.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        baseApiService = NetworkClient.getClient(this@LoginActivity)!!
            .create(BaseApiService::class.java)

        val scheduler = AppSchedulerProvider()
        presenter = LoginPresenter(this@LoginActivity, baseApiService, scheduler)

        btn_login.setOnClickListener {
            login()
        }

        tv_register.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        tv_register.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://app.tungguin.com/designer/register")
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val getUserToken = SharedPrefManager.getInstance(this@LoginActivity).token
        if(getUserToken != null){

            if(intent?.extras != null){

                try{
                    getTypeAction = intent?.extras?.get("type").toString()
                    getData = JSONObject(intent?.getStringExtra("data").toString())
                    getOrderId = getData?.getString("orderId").toString()
                    getItems = getData?.getString("items").toString()
                    totalHarga = getData?.getString("total").toString()
                }catch (ex: JSONException){
                    ex.printStackTrace()
                }finally {

                    when(getTypeAction){
                        "orderAsk" -> {
                            val intent = Intent(this@LoginActivity, NewOrderActivity::class.java)
                            intent.putExtra(HASHED_ID, getOrderId)
                            intent.putExtra("sendItems", getItems)
                            intent.putExtra("sendTotalHarga", totalHarga)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(intent)
                            finish()
                        }
                        "order_accepted" -> {
                            val intent = Intent(this@LoginActivity, Dashboard::class.java)
                            intent.putExtra("toHistoriOrder", true)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(intent)
                            finish()
                        }
                        "new_order_message" -> {
                            val intent = Intent(this@LoginActivity, Dashboard::class.java)
                            intent.putExtra("get_message", true)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            startActivity(Intent(this@LoginActivity, Dashboard::class.java))
                            finish()
                        }
                    }
                }

            }
            else{
                startActivity(Intent(this@LoginActivity, Dashboard::class.java))
                finish()
            }
        }
    }

    private fun login(){

        emailUser = input_email.text.toString()
        passUser = input_password.text.toString()

        var err = 0

        if(!validateEmail(emailUser)){
            err++
            input_email.error = "Email tidak valid!"
        }

        if(!validateFields(passUser)){
            err++
            input_password.error = "Password tidak boleh kosong!"
        }

        if(err == 0){
            presenter.loginUser(this@LoginActivity, emailUser, passUser)
        }
    }

    override fun onSuccess() {
        startActivity(Intent(this@LoginActivity, Dashboard::class.java))
        finishAffinity()
    }

    override fun showPregressBar() {
        shadow.visibility = View.VISIBLE
        progress_bar.visibility = View.VISIBLE
    }

    override fun hidePregressBar() {
        shadow.visibility = View.GONE
        progress_bar.visibility = View.GONE
    }

    override fun noInternetConnection(message: String) {

    }
}
