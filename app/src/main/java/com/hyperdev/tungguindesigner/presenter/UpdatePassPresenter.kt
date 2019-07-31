package com.hyperdev.tungguindesigner.presenter

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.ConnectivityStatus
import com.hyperdev.tungguindesigner.network.Response
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.UpdatePassView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.net.SocketTimeoutException

class UpdatePassPresenter(private val context: Context,
                          private val view: UpdatePassView.View,
                          private val baseApiService: BaseApiService,
                          private val scheduler: SchedulerProvider) : UpdatePassView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun updatePassword(authHeader: String, accept: String,
                                name: String, email: String, phone: String,
                                password: String, c_password: String) {

        view.showPregressBar()

        baseApiService.updatePassword(authHeader, accept, name, email, phone, password, c_password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<ProfileResponse>{
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: ProfileResponse) {}

                override fun onError(e: Throwable) {
                    view.hidePregressBar()

                    if(ConnectivityStatus.isConnected(context)){
                        when (e) {
                            is HttpException -> {
                                val gson = Gson()
                                val response = gson.fromJson(e.response()?.errorBody()?.charStream(), Response::class.java)
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