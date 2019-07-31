package com.hyperdev.tungguindesigner.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguindesigner.model.TestimoniHistori.TestimoniResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.ConnectivityStatus.Companion.isConnected
import com.hyperdev.tungguindesigner.network.HandleError
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.TestimoniHisView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class HisTestimoniPresenter(
    private val view: TestimoniHisView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : TestimoniHisView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getHistoriTestimoni(token: String, context: Context, page: Int) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getTestimoniHistori(token, "application/json", page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<TestimoniResponse>() {
                    override fun onComplete() {
                        view.onSuccess()
                    }

                    override fun onNext(t: TestimoniResponse) {
                        try {
                            t.data?.let { view.loadTestimoniData(it) }
                            t.data?.testimoniItem?.let { view.loadTestimoniHis(it) }
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