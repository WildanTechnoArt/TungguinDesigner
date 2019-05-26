package com.hyperdev.tungguindesigner.view.ui

import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.model.requestwithdraw.WithdrawResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkUtil
import kotlinx.android.synthetic.main.activity_withdraw.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WithdrawActivity : AppCompatActivity() {

    private lateinit var inputOwnerBank: String
    private lateinit var inputNamaBank: String
    private lateinit var inputCabangBank: String
    private lateinit var inputNomorRek: String
    private lateinit var inputAmount: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var getToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initMain()

        btn_request.setOnClickListener {
            getInputData()
        }
    }

    private fun getInputData(){
        inputOwnerBank = owner_bank.text.toString()
        inputNamaBank = bank_name.text.toString()
        inputCabangBank = bank_branch.text.toString()
        inputNomorRek = nomor_rekening.text.toString()
        inputAmount = get_amount.text.toString()

        progressBar.visibility = View.VISIBLE
        shadow.visibility = View.VISIBLE

        baseApiService.requestWithdraw("Bearer $getToken", "application/json", inputOwnerBank,
            inputNamaBank, inputNomorRek, inputCabangBank,inputAmount)
            .enqueue(object : Callback<WithdrawResponse>{
                override fun onFailure(call: Call<WithdrawResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    shadow.visibility = View.GONE
                    Toast.makeText(this@WithdrawActivity, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<WithdrawResponse>, response: Response<WithdrawResponse>) {
                    if (response.isSuccessful) {
                        finish()
                        Toast.makeText(this@WithdrawActivity, "Permintaan Penarikan berhasil dikirim", Toast.LENGTH_SHORT).show()
                    }else{
                        progressBar.visibility = View.GONE
                        shadow.visibility = View.GONE
                        val gson = Gson()
                        val message = gson.fromJson(response.errorBody()?.charStream(), WithdrawResponse::class.java)
                        if(message != null){
                            Toast.makeText(this@WithdrawActivity, message.meta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun initMain(){

        saldo_wallet.text = intent.getStringExtra("saldoWallet").toString()

        getToken = SharedPrefManager.getInstance(this@WithdrawActivity).token.toString()

        baseApiService = NetworkUtil.getClient(this@WithdrawActivity)!!
            .create(BaseApiService::class.java)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}