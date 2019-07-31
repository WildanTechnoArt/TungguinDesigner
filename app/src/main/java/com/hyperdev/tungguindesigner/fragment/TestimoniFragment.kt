package com.hyperdev.tungguindesigner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.adapter.TestimoniHistoriAdapter
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.model.TestimoniHistori.TestimoniData
import com.hyperdev.tungguindesigner.model.TestimoniHistori.TestimoniItem
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkClient
import com.hyperdev.tungguindesigner.presenter.HisTestimoniPresenter
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.view.TestimoniHisView
import kotlin.properties.Delegates

class TestimoniFragment : Fragment(), TestimoniHisView.View {

    // Deklarasi Variable
    private lateinit var recyclerView: RecyclerView
    private lateinit var textImgNotFound: TextView
    private lateinit var swipeLayout: SwipeRefreshLayout
    private var listTestimoni: MutableList<TestimoniItem> = mutableListOf()
    private lateinit var presenter: TestimoniHisView.Presenter
    private lateinit var token: String
    private lateinit var baseApiService: BaseApiService
    private val TAG = javaClass.simpleName
    private var adapter by Delegates.notNull<TestimoniHistoriAdapter>()
    private var page: Int = 1
    private lateinit var nextPageURL: String
    private var isLoading by Delegates.notNull<Boolean>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_testimoni, container, false)

        recyclerView = view.findViewById(R.id.testimoniList)
        textImgNotFound = view.findViewById(R.id.txt_img_not_found)
        swipeLayout = view.findViewById(R.id.refreshLayout)

        loadData()

        recyclerView.adapter = adapter

        initListener()

        swipeLayout.setOnRefreshListener {
            presenter.getHistoriTestimoni("Bearer $token", context!!, page = page)
        }

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
        presenter = HisTestimoniPresenter(this, baseApiService, scheduler)
        adapter = TestimoniHistoriAdapter(listTestimoni as ArrayList<TestimoniItem>)

        presenter.getHistoriTestimoni("Bearer $token", context!!, page = page)
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
                    presenter.getHistoriTestimoni("Bearer $token", context!!, page = page)
                }
            }
        })
    }

    override fun loadTestimoniHis(item: List<TestimoniItem>) {
        if(page == 1){
            listTestimoni.clear()
            listTestimoni.addAll(item)
            adapter.notifyDataSetChanged()
        }else{
            adapter.refreshAdapter(item)
        }

        if(adapter.itemCount != 0){
            textImgNotFound.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }else{
            textImgNotFound.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    override fun loadTestimoniData(testimoni: TestimoniData) {
        nextPageURL = testimoni.next_page_url.toString()
    }

    override fun onSuccess() {
        swipeLayout.isRefreshing = false
        isLoading = false
    }

    override fun displayProgress() {
        swipeLayout.isRefreshing = true
        isLoading = true
    }

    override fun hideProgress() {
        swipeLayout.isRefreshing = false
        isLoading = false
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}