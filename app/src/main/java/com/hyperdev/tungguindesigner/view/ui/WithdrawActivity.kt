package com.hyperdev.tungguindesigner.view.ui

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.view.View
import android.widget.Toast
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkClient
import com.hyperdev.tungguindesigner.presenter.RequestWithdrawPresenter
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.view.RequestWithdrawView
import kotlinx.android.synthetic.main.activity_withdraw.*

class WithdrawActivity : AppCompatActivity(), RequestWithdrawView.View {

    private lateinit var inputOwnerBank: String
    private lateinit var inputNamaBank: String
    private lateinit var inputCabangBank: String
    private lateinit var inputNomorRek: String
    private lateinit var inputAmount: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: RequestWithdrawView.Presenter
    private lateinit var getToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

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

        presenter.withdrawWallet("Bearer $getToken", "application/json", inputOwnerBank,
            inputNamaBank, inputNomorRek, inputCabangBank,inputAmount)
    }

    private fun initMain(){

        saldo_wallet.text = intent?.getStringExtra("saldoWallet").toString()

        getToken = SharedPrefManager.getInstance(this@WithdrawActivity).token.toString()

        baseApiService = NetworkClient.getClient(this@WithdrawActivity)!!
            .create(BaseApiService::class.java)

        val scheduler = AppSchedulerProvider()
        presenter = RequestWithdrawPresenter(this@WithdrawActivity, this, baseApiService, scheduler)

    }

    override fun onSuccess() {
        Toast.makeText(this@WithdrawActivity, "Permintaan Penarikan berhasil dikirim", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun showPregressBar() {
        progress_bar.visibility = View.VISIBLE
        shadow.visibility = View.VISIBLE
    }

    override fun hidePregressBar() {
        progress_bar.visibility = View.GONE
        shadow.visibility = View.GONE
    }

    override fun noInternetConnection(message: String) {
        Snackbar.make(withdraw_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}