package com.hyperdev.tungguindesigner.view.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.view.View
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkUtil
import com.hyperdev.tungguindesigner.presenter.LoginPresenter
import com.hyperdev.tungguindesigner.repository.LoginRepositoryImp
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.utils.Validation.Companion.validateEmail
import com.hyperdev.tungguindesigner.utils.Validation.Companion.validateFields
import com.hyperdev.tungguindesigner.view.LoginView
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.lang.NullPointerException

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

        baseApiService = NetworkUtil.getClient(this@LoginActivity)!!
            .create(BaseApiService::class.java)

        val repository = LoginRepositoryImp(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = LoginPresenter(this@LoginActivity, repository, scheduler)

        btnLogin.setOnClickListener {
            login()
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
            presenter.loginUser(this@LoginActivity, emailUser, passUser)
        }
    }

    override fun onSuccess() {
        startActivity(Intent(this@LoginActivity, Dashboard::class.java))
        finishAffinity()
    }

    override fun showPregressBar() {
        shadow.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false
        email.isEnabled = false
        pass.isEnabled = false
    }

    override fun hidePregressBar() {
        shadow.visibility = View.GONE
        progressBar.visibility = View.GONE
        btnLogin.isEnabled = true
        email.isEnabled = true
        pass.isEnabled = true
    }

    override fun noInternetConnection(message: String) {
        Snackbar.make(login_page, message, Snackbar.LENGTH_SHORT).show()
    }
}
