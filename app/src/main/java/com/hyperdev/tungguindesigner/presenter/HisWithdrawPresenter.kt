package com.hyperdev.tungguindesigner.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguindesigner.model.withdrawhistori.WithdrawHisResponse
import com.hyperdev.tungguindesigner.network.ConnectivityStatus.Companion.isConnected
import com.hyperdev.tungguindesigner.network.HandleError
import com.hyperdev.tungguindesigner.repository.WithdrawHisRepositoryImpl
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.WithdrawHisView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class HisWithdrawPresenter(private val view: WithdrawHisView.View,
                           private val withdraw: WithdrawHisRepositoryImpl,
                           private val scheduler: SchedulerProvider) : WithdrawHisView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun getHistoriWithdraw(token: String, context: Context, page:Int) {
        view.displayProgress()

        compositeDisposable.add(withdraw.getWithdrawHistori(token, "application/json", page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<WithdrawHisResponse>(){
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onNext(t: WithdrawHisResponse) {
                    try{
                        t.data?.withdrawItem?.let { view.loadWithdrawHis(it) }
                        t.data?.let { view.loadWithdrawData(it) }
                    }catch(ex: Exception){
                        ex.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    view.hideProgress()

                    if(isConnected(context)){
                        when (e) {
                            is HttpException -> // non 200 error codes
                                HandleError.handleError(e, e.code(), context)
                            is SocketTimeoutException -> // connection errors
                                Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_LONG).show()
                        }
                    }else{
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