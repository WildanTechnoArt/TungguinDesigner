package com.hyperdev.tungguindesigner.presenter

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguindesigner.model.requestwithdraw.WithdrawResponse
import com.hyperdev.tungguindesigner.network.ConnectivityStatus
import com.hyperdev.tungguindesigner.network.Response
import com.hyperdev.tungguindesigner.repository.WithdrawRepositoryImp
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.RequestWithdrawView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.net.SocketTimeoutException

class RequestWithdrawPresenter(private val context: Context,
                               private val view: RequestWithdrawView.View,
                               private val withdraw: WithdrawRepositoryImp,
                               private val scheduler: SchedulerProvider) : RequestWithdrawView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun withdrawWallet(authHeader: String, accept: String,
                                bankOwner: String, bankName: String,
                                bankAccount: String, bankBranch: String,
                                amount: String) {

        view.showPregressBar()

        withdraw.requestWithdraw(authHeader, accept, bankOwner, bankName, bankAccount, bankBranch, amount)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<WithdrawResponse>{
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: WithdrawResponse) {}

                override fun onError(e: Throwable) {
                    view.hidePregressBar()

                    if(ConnectivityStatus.isConnected(context)){
                        when (e) {
                            is HttpException -> {
                                val gson = Gson()
                                val response = gson.fromJson(e.response().errorBody()?.charStream(), Response::class.java)
                                val message = response.meta?.message.toString()
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                            is SocketTimeoutException -> // connection errors
                                view.noInternetConnection("Connection Timeout!")
                        }
                    }else{
                        view.noInternetConnection("Tidak Terhubung Dengan Internet!")
                    }
                }

            })
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}