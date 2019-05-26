package com.hyperdev.tungguindesigner.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguindesigner.model.ordernotification.AcceptResponse
import com.hyperdev.tungguindesigner.model.ordernotification.CheckOrderResponse
import com.hyperdev.tungguindesigner.model.ordernotification.RejectResponse
import com.hyperdev.tungguindesigner.network.HandleError
import com.hyperdev.tungguindesigner.repository.GetOrderRepositoryImpl
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.GetOrderView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class GetOrderPresenter(private val view: GetOrderView.View,
                        private val order: GetOrderRepositoryImpl,
                        private val scheduler: SchedulerProvider) : GetOrderView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun acceptRequest(context: Context, token: String, order_id: String) {
        view.onDisplayProgress()

        compositeDisposable.add(order.acceptOrder(token,"application/json", order_id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<AcceptResponse>(){
                override fun onComplete() {
                    view.onHideProgress()
                    view.acceptSuccess()
                }

                override fun onNext(t: AcceptResponse) {}

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    view.onHideProgress()

                    when (e) {
                        is HttpException -> // non 200 error codes
                            HandleError.handleError(e, e.code(), context)
                        is SocketTimeoutException -> // connection errors
                            Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_LONG).show()
                    }
                }
            })
        )
    }

    override fun rejectRequest(context: Context, token: String, order_id: String) {
        view.onDisplayProgress()

        compositeDisposable.add(order.rejectOrder(token, "application/json", order_id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<RejectResponse>(){
                override fun onComplete() {
                    view.onHideProgress()
                    view.rejectSuccess()
                }

                override fun onNext(t: RejectResponse) {}

                override fun onError(e: Throwable) {
                    e.printStackTrace()

                    when (e) {
                        is HttpException -> // non 200 error codes
                            HandleError.handleError(e, e.code(), context)
                        is SocketTimeoutException -> // connection errors
                            Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_LONG).show()
                    }
                }
            })
        )
    }

    override fun checkOrderOffer(token: String, order_id: String) {
        view.onDisplayProgress()

        compositeDisposable.add(order.checkOrderOffer(token, order_id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<CheckOrderResponse>(){
                override fun onComplete() {
                    view.onHideProgress()
                }

                override fun onNext(t: CheckOrderResponse) {
                    t.data?.let { view.orderOffer(it) }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    view.onHideProgress()
                }
            })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}