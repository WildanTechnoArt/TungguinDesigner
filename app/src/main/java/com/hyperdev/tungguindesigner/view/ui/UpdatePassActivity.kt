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
import com.hyperdev.tungguindesigner.network.NetworkUtil
import com.hyperdev.tungguindesigner.presenter.UpdatePassPresenter
import com.hyperdev.tungguindesigner.repository.UpdatePassRepositoryImp
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.utils.Validation.Companion.validateFields
import com.hyperdev.tungguindesigner.view.UpdatePassView
import kotlinx.android.synthetic.main.activity_update_pass.*

class UpdatePassActivity : AppCompatActivity(), UpdatePassView.View {

    //Deklarasi Variable
    private lateinit var newPassword: String
    private lateinit var cPassword: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var presenter: UpdatePassView.Presenter
    private lateinit var getToken: String
    private lateinit var getName: String
    private lateinit var getEmail: String
    private lateinit var getPhone: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_pass)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //Inisialisasi Toolbar dan Menampilkan Menu Home pada Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getToken = SharedPrefManager.getInstance(this@UpdatePassActivity).token.toString()

        baseApiService = NetworkUtil.getClient(this@UpdatePassActivity)!!
            .create(BaseApiService::class.java)

        val repository = UpdatePassRepositoryImp(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = UpdatePassPresenter(this@UpdatePassActivity, this, repository, scheduler)

        getName = intent?.getStringExtra("userName").toString()
        getEmail = intent?.getStringExtra("userEmail").toString()
        getPhone = intent?.getStringExtra("userPhone").toString()

        btnSavePass.setOnClickListener {
            changePassUser()
        }
    }

    private fun changePassUser(){
        newPassword = newpass.text.toString()
        cPassword = newpassConfirm.text.toString()

        var err = 0

        if(!validateFields(newPassword)){
            err++
            newpass.error = "Masukan password baru !"
        }

        if(!validateFields(cPassword)){
            err++
            newpassConfirm.error = "Tidak boleh kosong !"
        }

        if(err == 0){
            if(newPassword != cPassword){
                newpassConfirm.error = "Password yang dimasukan tidak sama!"
            }else{
                progressBar.visibility = View.VISIBLE
                newpass.isEnabled = false
                newpassConfirm.isEnabled = false
                presenter.updatePassword("Bearer $getToken", "application/json", getName, getEmail, getPhone, newPassword, cPassword)
            }
        }
    }

    override fun onSuccess() {
        Toast.makeText(this@UpdatePassActivity, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun showPregressBar() {
        progressBar.visibility = View.VISIBLE
        newpass.isEnabled = false
        newpassConfirm.isEnabled = false
    }

    override fun hidePregressBar() {
        progressBar.visibility = View.GONE
        newpass.isEnabled = true
        newpassConfirm.isEnabled = true
    }

    override fun noInternetConnection(message: String) {
        Snackbar.make(change_pass_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}