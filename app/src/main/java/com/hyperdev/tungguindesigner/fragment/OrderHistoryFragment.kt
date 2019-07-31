package com.hyperdev.tungguindesigner.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.adapter.OrderHistoriAdapter
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.model.historiorder.HistoryOrderItem
import com.hyperdev.tungguindesigner.model.historiorder.HistoryOrderResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkClient
import com.hyperdev.tungguindesigner.presenter.HisOrderPresenter
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.view.OrderHisView
import kotlin.properties.Delegates

class OrderHistoryFragment : Fragment(), OrderHisView.View {

    // Deklarasi Variable
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingBar: ProgressBar
    private lateinit var textImgNotFound: TextView
    private var listOrder: MutableList<HistoryOrderItem> = mutableListOf()
    private lateinit var presenter: OrderHisView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private val TAG = javaClass.simpleName
    private var adapter by Delegates.notNull<OrderHistoriAdapter>()
    private var page: Int = 1
    private lateinit var nextPageURL: String
    private var isLoading by Delegates.notNull<Boolean>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_order_histori, container, false)

        recyclerView = view.findViewById(R.id.orderList)
        loadingBar = view.findViewById(R.id.progress_bar)
        textImgNotFound = view.findViewById(R.id.txt_img_not_found)

        loadData()

        recyclerView.adapter = adapter

        initListener()

        return view
    }

    private fun loadData(){
        token = SharedPrefManager.getInstance(context!!).token.toString()

        baseApiService = NetworkClient.getClient(context!!)!!
            .create(BaseApiService::class.java)

        val layout = LinearLayoutManager(context!!)
        recyclerView.layoutManager = layout
        recyclerView.setHasFixedSize(true)

        val scheduler = AppSchedulerProvider()
        presenter = HisOrderPresenter(this, baseApiService, scheduler)

        presenter.getHistoriOrder("Bearer $token", context!!, page = page)

        adapter = OrderHistoriAdapter(listOrder as ArrayList<HistoryOrderItem>)
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

    override fun loadWithdrawHis(item: List<HistoryOrderItem>) {
        if(page == 1){
            listOrder.clear()
            listOrder.addAll(item)
            adapter.notifyDataSetChanged()
        }else{
            adapter.refreshAdapter(item)
        }

        if(adapter.itemCount > 0){
            textImgNotFound.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }else{
            textImgNotFound.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    override fun loadWithdrawData(order: HistoryOrderResponse) {
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
