package com.hyperdev.tungguindesigner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.adapter.WithdrawHistoriAdapter
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.model.withdrawhistori.WithdrawData
import com.hyperdev.tungguindesigner.model.withdrawhistori.WithdrawItem
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkUtil
import com.hyperdev.tungguindesigner.presenter.HisWithdrawPresenter
import com.hyperdev.tungguindesigner.repository.WithdrawHisRepositoryImpl
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.view.WithdrawHisView
import kotlin.properties.Delegates

class WithdrawHistoriFragment : Fragment(), WithdrawHisView.View {

    // Deklarasi Variable
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingBar: ProgressBar
    private lateinit var textImgNotFound: TextView
    private var listHistori: MutableList<WithdrawItem> = mutableListOf()
    private lateinit var presenter: WithdrawHisView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private val TAG = javaClass.simpleName
    private var adapter by Delegates.notNull<WithdrawHistoriAdapter>()
    private var page by Delegates.notNull<Int>()
    private lateinit var nextPageURL: String
    private var isLoading by Delegates.notNull<Boolean>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_withdraw_histori, container, false)

        recyclerView = view.findViewById(R.id.withdrawList)
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

        val request = WithdrawHisRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()
        presenter = HisWithdrawPresenter(this, request, scheduler)
        adapter = WithdrawHistoriAdapter(listHistori as ArrayList<WithdrawItem>)

        presenter.getHistoriWithdraw("Bearer $token", context!!, page = page)
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
                    presenter.getHistoriWithdraw("Bearer $token", context!!, page = page)
                }
            }
        })
    }

    override fun loadWithdrawHis(item: List<WithdrawItem>) {
        if(page == 1){
            listHistori.clear()
            listHistori.addAll(item)
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

    override fun loadWithdrawData(histori: WithdrawData) {
        nextPageURL = histori.next_page_url.toString()
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
