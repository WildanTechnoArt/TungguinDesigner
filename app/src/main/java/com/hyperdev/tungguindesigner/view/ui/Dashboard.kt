package com.hyperdev.tungguindesigner.view.ui

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.fragment.DashboardFragment
import com.hyperdev.tungguindesigner.fragment.HistoryFragment
import com.hyperdev.tungguindesigner.fragment.ProfileFragment
import com.hyperdev.tungguindesigner.fragment.TestimoniFragment
import com.hyperdev.tungguindesigner.model.profile.DataUser
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkClient
import com.hyperdev.tungguindesigner.presenter.DesignerPresenter
import com.hyperdev.tungguindesigner.presenter.TokenFCMPresenter
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.view.ProfileView
import com.hyperdev.tungguindesigner.view.TokenFCMView
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import kotlinx.android.synthetic.main.activity_dashboard.*

class Dashboard : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, ProfileView.DesignerId {

    companion object {
        const val PERMISSION_STORAGE = 77
    }

    private lateinit var baseApiService: BaseApiService
    private lateinit var getToken: String
    private lateinit var designerId: String
    private lateinit var presenter: ProfileView.DesignerPresenter
    private lateinit var presenterFCM: TokenFCMView.Presenter
    private lateinit var toast: Toast
    private var lastBackPressTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initData()

        checkPermission()

        bottom_navigation.setOnNavigationItemSelectedListener(this)

        postTokenFCM()

        if (intent.extras != null) {
            val getMessage = intent.getBooleanExtra("get_message", false)
            if (getMessage) {
                val alertDialog = AlertDialog.Builder(this).apply {
                    setMessage("Silakan buka halaman chat di web")
                    setPositiveButton(
                        "OK"
                    ) { dialog, _ -> dialog.dismiss() }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }
    }

    private fun initData() {
        getToken = SharedPrefManager.getInstance(this@Dashboard).token.toString()

        baseApiService = NetworkClient.getClient(this@Dashboard)!!
            .create(BaseApiService::class.java)

        val scheduler = AppSchedulerProvider()

        presenter = DesignerPresenter(this@Dashboard, baseApiService, scheduler)
        presenter.getDesignerId("Bearer $getToken", this@Dashboard)

        presenterFCM = TokenFCMPresenter(baseApiService, scheduler)

        // Mengaktifkan Kembali FCM
        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        RxPaparazzo.register(application)

        if (intent.extras != null) {
            val orderAccept = intent.getBooleanExtra("toHistoriOrder", false)
            if (orderAccept) {
                loadFragment(HistoryFragment())
                bottom_navigation.selectedItemId = R.id.histori
            } else {
                loadFragment(DashboardFragment())
            }
        } else {
            loadFragment(DashboardFragment())
        }
    }

    private fun postTokenFCM() {
        val tokenFCM = SharedPrefManager.getInstance(this@Dashboard).tokenFCM.toString()
        presenterFCM.sendTokenFCM("Bearer $getToken", "application/json", tokenFCM)
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this@Dashboard, android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this@Dashboard, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this@Dashboard, arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_STORAGE
            )

        }
    }

    override fun getHashedId(data: DataUser) {
        designerId = data.hashedId.toString()
    }

    private fun loadFragment(fragment: Fragment?): Boolean {

        @Suppress("SENSELESS_COMPARISON")
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
            return true
        }

        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null

        when (item.itemId) {
            R.id.dashboard -> fragment = DashboardFragment()
            R.id.testimoni -> fragment = TestimoniFragment()
            R.id.histori -> fragment = HistoryFragment()
            R.id.profile -> fragment = ProfileFragment()
        }

        return loadFragment(fragment)
    }

    override fun onBackPressed() {

        @Suppress("SENSELESS_COMPARISON")
        if (this.lastBackPressTime < System.currentTimeMillis() - 3000) {
            toast = Toast.makeText(this@Dashboard, "Tekan tombol kembali lagi untuk keluar", 3000)
            toast.show()
            this.lastBackPressTime = System.currentTimeMillis()
        } else {
            if (toast != null) {
                toast.cancel()
            }
            super.onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
        presenterFCM.onDestroy()
    }
}