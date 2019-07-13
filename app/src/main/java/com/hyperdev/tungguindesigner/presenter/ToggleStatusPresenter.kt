package com.hyperdev.tungguindesigner.presenter

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.network.ConnectivityStatus
import com.hyperdev.tungguindesigner.network.Response
import com.hyperdev.tungguindesigner.repository.ToggleStatusRepositoryImp
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.ToggleStatusView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ToggleStatusPresenter(private val context: Context,
                            private val view: ToggleStatusView.View,
                            private val toggle: ToggleStatusRepositoryImp,
                            private val scheduler: SchedulerProvider) : ToggleStatusView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun toggleStatus(authHeader: String, accept: String, status: String, kondisi: Boolean) {
        view.showToggleProgress()

        toggle.toggleStatus(authHeader, accept, status)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<ProfileResponse>{
                override fun onComplete() {
                    view.onSuccessSendStatus(kondisi)
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: ProfileResponse) {}

                override fun onError(e: Throwable) {
                    view.hideTogglePregress()

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