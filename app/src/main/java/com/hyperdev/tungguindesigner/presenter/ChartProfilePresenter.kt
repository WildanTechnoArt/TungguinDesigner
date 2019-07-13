package com.hyperdev.tungguindesigner.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguindesigner.model.chartorder.ChartResponse
import com.hyperdev.tungguindesigner.network.ConnectivityStatus.Companion.isConnected
import com.hyperdev.tungguindesigner.network.HandleError
import com.hyperdev.tungguindesigner.repository.ChartRepositoryImpl
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.ChartOrderView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ChartProfilePresenter(
    private val view: ChartOrderView.View,
    private val chart: ChartRepositoryImpl,
    private val scheduler: SchedulerProvider
) : ChartOrderView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getChartItem(token: String, context: Context) {
        view.displayProgress()

        compositeDisposable.add(
            chart.getChartOrder(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<ChartResponse>() {
                    override fun onComplete() {
                        view.hideProgress()
                    }

                    override fun onNext(t: ChartResponse) {
                        try {
                            t.data?.let { view.displayChartStat(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        view.hideProgress()

                        if (isConnected(context)) {
                            when (e) {
                                is HttpException -> // non 200 error codes
                                    HandleError.handleError(e, e.code(), context)
                                is SocketTimeoutException -> // connection errors
                                    Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Tidak Terhubung Dengan Internet!", Toast.LENGTH_LONG).show()
                        }
                    }
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}