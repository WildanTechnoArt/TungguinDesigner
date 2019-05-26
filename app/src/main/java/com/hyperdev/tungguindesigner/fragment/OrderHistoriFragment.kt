package com.hyperdev.tungguindesigner.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.adapter.OrderHistoriAdapter
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.model.historiorder.HistoriOrderItem
import com.hyperdev.tungguindesigner.model.historiorder.HistoriOrderResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkUtil
import com.hyperdev.tungguindesigner.presenter.HisOrderPresenter
import com.hyperdev.tungguindesigner.repository.OrderHisRepositoryImpl
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.view.OrderHisView
import kotlin.properties.Delegates

class OrderHistoriFragment : Fragment(), OrderHisView.View {

    // Deklarasi Variable
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingBar: ProgressBar
    private lateinit var textImgNotFound: TextView
    private var listOrder: MutableList<HistoriOrderItem> = mutableListOf()
    private lateinit var presenter: OrderHisView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private val TAG = javaClass.simpleName
    private var adapter by Delegates.notNull<OrderHistoriAdapter>()
    private var page by Delegates.notNull<Int>()
    private lateinit var nextPageURL: String
    private var isLoading by Delegates.notNull<Boolean>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_order_histori, container, false)

        recyclerView = view.findViewById(R.id.orderList)
        loadingBar = view.findViewById(R.id.progressBar)
        textImgNotFound = view.findViewById(R.id.txt_img_not_found)

        page = 1

        loadData()

        recyclerView.adapter = adapter

        initListener()

        return view
    }

    private fun loadData(){
        token = SharedPrefManager.getInstance(context!!).token.toString()

        baseApiService = NetworkUtil.getClient(context!!)!!
            .create(BaseApiService::class.java)

        val layout = LinearLayoutManager(context!!)
        recyclerView.layoutManager = layout
        recyclerView.setHasFixedSize(true)

        val request = OrderHisRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = HisOrderPresenter(this, request, scheduler)
        adapter = OrderHistoriAdapter(listOrder as ArrayList<HistoriOrderItem>)

        presenter.getHistoriOrder("Bearer $token", context!!, page = page)
    }

    private fun initListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val countItem = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val isLastPosition = countItem.minus(1) == lastVisiblePosition
                Log.d(TAG, "countItem: $countItem")
                Log.d(TAG, "lastVisiblePosition: $lastVisiblePosition")
                Log.d(TAG, "isLastPosition: $isLastPosition")
                if (!isLoading && isLastPosition && nextPageURL != "null") {
                    page = page.plus(1)
                    presenter.getHistoriOrder("Bearer $token", context!!, page = page)
                }
            }
        })
    }

    override fun loadWithdrawHis(item: List<HistoriOrderItem>) {
        if(page == 1){
            listOrder.clear()
            listOrder.addAll(item)
            adapter.notifyDataSetChanged()
        }else{
            adapter.refreshAdapter(item)
        }

        if(adapter.itemCount.toString() != "0"){
            textImgNotFound.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }else{
            textImgNotFound.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    override fun loadWithdrawData(order: HistoriOrderResponse) {
        nextPageURL = order.next_page_url.toString()
    }

    override fun displayProgress() {
        isLoading = true
        loadingBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        isLoading = false
        loadingBar.visibility = View.GONE
    }

    override fun onSuccess() {
        isLoading = false
        loadingBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
