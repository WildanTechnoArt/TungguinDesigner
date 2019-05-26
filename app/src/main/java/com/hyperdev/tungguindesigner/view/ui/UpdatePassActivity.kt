package com.hyperdev.tungguindesigner.view.ui

import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkUtil
import com.hyperdev.tungguindesigner.utils.Validation.Companion.validateFields
import kotlinx.android.synthetic.main.activity_update_pass.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePassActivity : AppCompatActivity() {

    //Deklarasi Variable
    private lateinit var newPassword: String
    private lateinit var cPassword: String
    private lateinit var baseApiService: BaseApiService
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

        getName = intent?.extras?.getString("userName")!!
        getEmail = intent?.extras?.getString("userEmail")!!
        getPhone = intent?.extras?.getString("userPhone")!!

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
                disabledView()
                savePassResponse(newPassword, cPassword)
            }
        }
    }

    private fun savePassResponse(newPass: String, cPass: String){
        baseApiService.updatePassword("Bearer $getToken", "application/json", getName, getEmail, getPhone, newPass, cPass)
            .enqueue(object : Callback<ProfileResponse> {
                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("ChangePassActivity.kt", "onFailure ERROR -> "+ t.message)
                    showSnackBarMessage("Tidak ada koneksi internet !")
                    enabledView()
                }

                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                        disabledView()
                        Toast.makeText(this@UpdatePassActivity, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        val gson = Gson()
                        enabledView()
                        val message = gson.fromJson(response.errorBody()?.charStream(), ProfileResponse::class.java)
                        Toast.makeText(this@UpdatePassActivity, message.getMeta?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            })
    }

    private fun enabledView(){
        progressBar.visibility = View.GONE
        newpass.isEnabled = true
        newpassConfirm.isEnabled = true
    }

    private fun disabledView(){
        progressBar.visibility = View.VISIBLE
        newpass.isEnabled = false
        newpassConfirm.isEnabled = false
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(change_pass_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
