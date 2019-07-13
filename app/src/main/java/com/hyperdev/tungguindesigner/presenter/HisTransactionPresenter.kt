package com.hyperdev.tungguindesigner.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguindesigner.model.transactionhistori.TransactionResponse
import com.hyperdev.tungguindesigner.network.ConnectivityStatus.Companion.isConnected
import com.hyperdev.tungguindesigner.network.HandleError
import com.hyperdev.tungguindesigner.repository.TransactionHisRepositoryImpl
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.TransactionHisView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class HisTransactionPresenter(
    private val view: TransactionHisView.View,
    private val transaction: TransactionHisRepositoryImpl,
    private val scheduler: SchedulerProvider
) : TransactionHisView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getHistoriTransaction(token: String, context: Context, page: Int) {
        view.displayProgress()

        compositeDisposable.add(
            transaction.getTransactionHistori(token, "application/json", page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<TransactionResponse>() {
                    override fun onComplete() {
                        view.onSuccess()
                    }

                    override fun onNext(t: TransactionResponse) {
                        try {
                            t.data?.transactionHistory?.let { view.loadTransactionData(it) }
                            t.data?.transactionHistory?.transactionItem?.let { view.loadTransactionHis(it) }
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