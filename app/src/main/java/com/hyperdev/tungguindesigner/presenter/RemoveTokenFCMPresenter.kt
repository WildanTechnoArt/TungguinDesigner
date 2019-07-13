package com.hyperdev.tungguindesigner.presenter

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguindesigner.model.fcm.FcmResponse
import com.hyperdev.tungguindesigner.network.ConnectivityStatus
import com.hyperdev.tungguindesigner.network.Response
import com.hyperdev.tungguindesigner.repository.TokenFCMRepositoryImp
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.RemoveTokenFCMView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.net.SocketTimeoutException

class RemoveTokenFCMPresenter(private val context: Context,
                              private val view: RemoveTokenFCMView.View,
                              private val fcm: TokenFCMRepositoryImp,
                              private val scheduler: SchedulerProvider) : RemoveTokenFCMView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun removeTokenFCM(authHeader: String, accept: String, token: String?) {
        view.showPregressBar()

        fcm.postTokenFCM(authHeader, accept, token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<FcmResponse>{
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: FcmResponse) {}

                override fun onError(e: Throwable) {
                    e.printStackTrace()
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