package com.hyperdev.tungguindesigner.fragment

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.CardView
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.model.announcement.AnnouncementData
import com.hyperdev.tungguindesigner.model.chartorder.ChartData
import com.hyperdev.tungguindesigner.model.chartorder.ChartItem
import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.model.transactionhistori.TransactionData
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkUtil
import com.hyperdev.tungguindesigner.presenter.ChartProfilePresenter
import com.hyperdev.tungguindesigner.presenter.DashboardPresenter
import com.hyperdev.tungguindesigner.repository.ChartRepositoryImpl
import com.hyperdev.tungguindesigner.repository.TransactionHisRepositoryImpl
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.view.ChartOrderView
import com.hyperdev.tungguindesigner.view.DashboardView
import com.hyperdev.tungguindesigner.view.ui.WithdrawActivity
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.DecimalFormat

class DashboardFragment : Fragment(), DashboardView.View, ChartOrderView.View {

    private lateinit var mySaldo: TextView
    private lateinit var thisWeekOrder: TextView
    private lateinit var todayOrder: TextView
    private lateinit var totalOrder: TextView
    private lateinit var presenter: DashboardView.Presenter
    private lateinit var chartPresenter: ChartOrderView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var btnWithdraw: Button
    private lateinit var switchButton: Switch
    private lateinit var chartStatistic: LineChart
    private lateinit var dataSet1: LineDataSet
    private var xAxisList: ArrayList<String> = arrayListOf()
    private var ilineDataSet: ArrayList<ILineDataSet> = arrayListOf()
    private var lineEntry: ArrayList<Entry> = arrayListOf()
    private lateinit var announcementText: WebView
    private lateinit var cardAnnouncement: CardView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        mySaldo = view.findViewById(R.id.my_saldo)
        thisWeekOrder = view.findViewById(R.id.this_week_order)
        todayOrder = view.findViewById(R.id.today_order)
        totalOrder = view.findViewById(R.id.total_order)
        refresh = view.findViewById(R.id.refreshLayout)
        btnWithdraw = view.findViewById(R.id.withdraw)
        switchButton = view.findViewById(R.id.status_designer)
        chartStatistic = view.findViewById(R.id.dataChart)
        announcementText = view.findViewById(R.id.announcement)
        cardAnnouncement = view.findViewById(R.id.card_announcement)

        loadData()

        refresh.setOnRefreshListener {
            presenter.getDashboardData("Bearer $token", context!!)
            chartPresenter.getChartItem("Bearer $token", context!!)
        }

        btnWithdraw.setOnClickListener {
            val intent = Intent(it.context, WithdrawActivity::class.java)
            intent.putExtra("saldoWallet", mySaldo.text.toString())
            startActivity(intent)
        }

        switchButton.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked){
                chechStatusToggler("1", true)
            }else{
                chechStatusToggler("0", false)
            }

        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun checkStatusDesigner(){
        val getStatus: Boolean? = SharedPrefManager.getInstance(context!!).status
        switchButton.isChecked = getStatus!!
        if(switchButton.isChecked){
            switchButton.text = "Aktif"
            switchButton.setTextColor(Color.parseColor("#32AD4A"))
        }else{
            switchButton.text = "Nonaktif"
            switchButton.setTextColor(Color.parseColor("#FFD40101"))
        }
    }

    private fun chechStatusToggler(status: String, kondisi: Boolean){
        baseApiService.setToogleStatus("Bearer $token", "application/json",status)
            .enqueue(object : Callback<ProfileResponse>{
                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Toast.makeText(context, t.message.toString(), Toast.LENGTH_LONG).show()
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                        try {

                            if(kondisi){
                                switchButton.text = "Aktif"
                                switchButton.setTextColor(Color.parseColor("#32AD4A"))
                                SharedPrefManager.getInstance(context!!).sendStatus(true)

                                notificationProperties("Tungguin", "Anda Sedang Aktif", false)
                            }else{
                                switchButton.text = "Nonaktif"
                                SharedPrefManager.getInstance(context!!).sendStatus(false)
                                switchButton.setTextColor(Color.parseColor("#FFD40101"))

                                notificationProperties("Tungguin", "Anda Sedang Nonaktif", true)
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.i("debug", "onResponse: GA BERHASIL")
                        val gson = Gson()
                        val errorBody = gson.fromJson(response.errorBody()?.charStream(), ProfileResponse::class.java)
                        val errorMessage = errorBody.getMeta?.message
                        if(errorMessage != null){
                            Toast.makeText(context, errorMessage.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            })
    }

    private fun notificationProperties(title: String, message: String, status: Boolean){

        val channelId = "Default"
        val builder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.drawable.ic_tungguin_notify)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        builder.setOngoing(true)
        val manager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        manager.notify(1, builder.build())
        if(status){
            manager.cancel(1)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun displayChartStat(chartData: ChartData) {
        thisWeekOrder.text = "Order Minggu ini : ${chartData.thisWeek.toString()}"
        todayOrder.text = "Order Hari ini : ${chartData.today.toString()}"
        totalOrder.text = "Total Order : ${chartData.total.toString()} Order"
    }

    override fun loaddAnnouncement(text: AnnouncementData) {
        if(text.announcement.toString() != "null"){
            cardAnnouncement.visibility = View.VISIBLE
            announcementText.loadData(text.announcement.toString(), "text/html", null)
        }else{
            cardAnnouncement.visibility = View.GONE
        }
    }

    override fun displayChart(chartItem: List<ChartItem>) {

        xAxisList.clear()
        lineEntry.clear()
        ilineDataSet.clear()
        chartItem.forEach {
            xAxisList.add(it.date.toString())
        }

        for ((index, value) in chartItem.withIndex()){
            lineEntry.add(Entry(index.toFloat(), value.count!!.toFloat()))
        }
    }

    override fun onChartView() {

        // LineChart Properties
        chartStatistic.isDragEnabled = true
        chartStatistic.setScaleEnabled(false)
        chartStatistic.animateY(1000)
        chartStatistic.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisList)
        chartStatistic.description.text = "Orderan Minggu Ini"
        chartStatistic.axisRight.isEnabled = false
        chartStatistic.isDragEnabled = true
        chartStatistic.setScaleEnabled(true)
        chartStatistic.setTouchEnabled(false)

        // LineChart set Legend
        val legend = chartStatistic.legend
        legend.form = Legend.LegendForm.LINE

        // Left YAxis Properties
        val leftAxis = chartStatistic.axisLeft
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.setDrawLimitLinesBehindData(true)

        dataSet1 = LineDataSet(lineEntry, "Order")

        // Style Properties
        dataSet1.fillAlpha = 110
        dataSet1.color = Color.GREEN
        dataSet1.lineWidth = 3f
        dataSet1.valueTextSize = 10f
        dataSet1.valueTextColor = Color.BLACK
        dataSet1.valueFormatter = IntegerFormatter()
        dataSet1.setDrawFilled(true)
        dataSet1.fillColor = Color.GREEN
        dataSet1.circleRadius = 3f
        dataSet1.setDrawCircleHole(false)
        dataSet1.setCircleColor(Color.BLACK)

        ilineDataSet.add(dataSet1)

        val data = LineData(ilineDataSet)
        chartStatistic.data = data
        chartStatistic.invalidate()
    }

    override fun onResume() {
        super.onResume()
        presenter.getDashboardData("Bearer $token", context!!)
        chartPresenter.getChartItem("Bearer $token", context!!)
        presenter.getAnnouncementData("Bearer $token")
    }

    private fun loadData(){
        token = SharedPrefManager.getInstance(context!!).token.toString()

        baseApiService = NetworkUtil.getClient(context!!)!!
            .create(BaseApiService::class.java)

        val request = TransactionHisRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = DashboardPresenter(this, request, scheduler)

        val chartRequest = ChartRepositoryImpl(baseApiService)
        chartPresenter = ChartProfilePresenter(this, chartRequest, scheduler)

    }

    override fun loadSaldoData(saldo: TransactionData) {
        mySaldo.text = saldo.balance.toString()
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

internal class IntegerFormatter : IndexAxisValueFormatter() {

    private val mFormat: DecimalFormat = DecimalFormat("###,###,##0")

    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value.toDouble())
    }
}