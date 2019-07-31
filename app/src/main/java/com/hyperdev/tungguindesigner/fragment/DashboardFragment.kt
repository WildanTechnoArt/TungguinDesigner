package com.hyperdev.tungguindesigner.fragment

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.app.NotificationCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.cardview.widget.CardView
import android.view.*
import android.webkit.WebView
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.model.announcement.AnnouncementData
import com.hyperdev.tungguindesigner.model.chartorder.ChartData
import com.hyperdev.tungguindesigner.model.profile.DataUser
import com.hyperdev.tungguindesigner.model.transactionhistori.TransactionData
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkClient
import com.hyperdev.tungguindesigner.presenter.ChartProfilePresenter
import com.hyperdev.tungguindesigner.presenter.DashboardPresenter
import com.hyperdev.tungguindesigner.presenter.ToggleStatusPresenter
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.view.ChartOrderView
import com.hyperdev.tungguindesigner.view.DashboardView
import com.hyperdev.tungguindesigner.view.ToggleStatusView
import com.hyperdev.tungguindesigner.view.ui.Dashboard
import com.hyperdev.tungguindesigner.view.ui.WithdrawActivity
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.io.IOException

class DashboardFragment : Fragment(), DashboardView.View, ChartOrderView.View,
    ToggleStatusView.View {

    private lateinit var mySaldo: TextView
    private lateinit var thisWeekOrder: TextView
    private lateinit var todayOrder: TextView
    private lateinit var totalOrder: TextView
    private lateinit var presenter: DashboardView.Presenter
    private lateinit var presenterStatus: ToggleStatusView.Presenter
    private lateinit var chartPresenter: ChartOrderView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var btnWithdraw: Button
    private lateinit var switchButton: Switch
    private lateinit var announcementText: WebView
    private lateinit var cardAnnouncement: CardView
    private lateinit var cardOrderan: CardView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        mySaldo = view.findViewById(R.id.my_saldo)
        thisWeekOrder = view.findViewById(R.id.tv_week_order)
        todayOrder = view.findViewById(R.id.tv_today_order)
        totalOrder = view.findViewById(R.id.tv_total_order)
        refresh = view.findViewById(R.id.refreshLayout)
        btnWithdraw = view.findViewById(R.id.withdraw)
        switchButton = view.findViewById(R.id.status_designer)
        announcementText = view.findViewById(R.id.announcement)
        cardAnnouncement = view.findViewById(R.id.card_announcement)
        cardOrderan = view.findViewById(R.id.card_orderan)

        loadData()

        refresh.isEnabled = false

        btnWithdraw.setOnClickListener {
            val intent = Intent(it.context, WithdrawActivity::class.java)
            intent.putExtra("saldoWallet", mySaldo.text.toString())
            startActivity(intent)
        }

        switchButton.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                chechStatusToggler("1", true)
            } else {
                chechStatusToggler("0", false)
            }

        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun checkStatusDesigner() {
        val getStatus: Boolean? = SharedPrefManager.getInstance(context!!).status
        switchButton.isChecked = getStatus!!
        if (switchButton.isChecked) {
            switchButton.text = "Aktif"
            switchButton.setTextColor(Color.parseColor("#32AD4A"))
        } else {
            switchButton.text = "Nonaktif"
            switchButton.setTextColor(Color.parseColor("#FFD40101"))
        }
    }

    private fun chechStatusToggler(status: String, kondisi: Boolean) {
        presenterStatus.toggleStatus("Bearer $token", "application/json", status, kondisi)
    }

    private fun notificationProperties(title: String, message: String, status: Boolean) {

        val intent = Intent(context!!, Dashboard::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context!!, 0, intent, PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = "Default"
        val builder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.drawable.ic_tungguin_notification)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_LIGHTS and Notification.DEFAULT_SOUND)
            .setVibrate(longArrayOf(0L))
            .setContentIntent(pendingIntent)
        builder.setOngoing(true)
        val manager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        manager.notify(1, builder.build())
        if (status) {
            manager.cancel(1)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun displayChartStat(chartData: ChartData) {
        thisWeekOrder.text = "Order Minggu ini : ${chartData.thisWeek.toString()}"
        todayOrder.text = "Order Hari ini : ${chartData.today.toString()}"
        totalOrder.text = "Total Order : ${chartData.total.toString()} Order"
        tv_total_fee.text = "Total Fee : ${chartData.totalFee.toString()}"
    }

    override fun loaddAnnouncement(text: AnnouncementData) {
        if (text.announcement.toString() != "null") {
            cardOrderan.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            cardOrderan.layoutParams.height = resources.getDimensionPixelSize(R.dimen.cartHeight)
            cardAnnouncement.visibility = View.VISIBLE
            announcementText.loadData(text.announcement.toString(), "text/html", null)
        } else {
            cardOrderan.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            cardOrderan.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            cardAnnouncement.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showProfile(profileItem: DataUser) {
        tv_designer_name.text = "Halo ${profileItem.name.toString()}"
    }

    override fun onResume() {
        super.onResume()
        presenter.getDashboardData("Bearer $token", context!!)
        presenter.getUserProfile("Bearer $token", context!!)
        chartPresenter.getChartItem("Bearer $token", context!!)
        presenter.getAnnouncementData("Bearer $token")
    }

    private fun loadData() {
        token = SharedPrefManager.getInstance(context!!).token.toString()

        baseApiService = NetworkClient.getClient(context!!)!!
            .create(BaseApiService::class.java)

        val scheduler = AppSchedulerProvider()
        presenter = DashboardPresenter(this, baseApiService, scheduler)

        chartPresenter = ChartProfilePresenter(this, baseApiService, scheduler)

        presenterStatus = ToggleStatusPresenter(context!!, this, baseApiService, scheduler)

    }

    override fun showToggleProgress() {
        refresh.isRefreshing = true
    }

    override fun hideTogglePregress() {
        refresh.isRefreshing = false
    }

    override fun noInternetConnection(message: String) {
        Toast.makeText(context!!, message, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessSendStatus(kondisi: Boolean) {
        refresh.isRefreshing = false
        try {

            if (kondisi) {
                switchButton.text = "Aktif"
                switchButton.setTextColor(Color.parseColor("#32AD4A"))
                SharedPrefManager.getInstance(context!!).sendStatus(true)

                notificationProperties("Tungguin", "Anda Sedang Aktif", false)
                tv_happy_working.visibility = View.VISIBLE
            } else {
                switchButton.text = "Nonaktif"
                SharedPrefManager.getInstance(context!!).sendStatus(false)
                switchButton.setTextColor(Color.parseColor("#FFD40101"))

                notificationProperties("Tungguin", "Anda Sedang Nonaktif", true)
                tv_happy_working.visibility = View.GONE
            }

        } catch (e2: IOException) {
            e2.printStackTrace()
        } catch (e3: KotlinNullPointerException) {
            e3.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun loadSaldoData(saldo: TransactionData) {
        mySaldo.text = "Saldo : ${saldo.balance.toString()}"
    }

    override fun displayProgress() {
        refresh.isRefreshing = true
    }

    override fun hideProgress() {
        refresh.isRefreshing = false
    }

    override fun onSuccess() {
        refresh.isRefreshing = false
        checkStatusDesigner()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
        chartPresenter.onDestroy()
    }
}