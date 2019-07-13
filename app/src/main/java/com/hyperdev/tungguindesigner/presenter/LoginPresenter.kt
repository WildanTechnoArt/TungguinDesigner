package com.hyperdev.tungguindesigner.presenter

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.model.login.LoginResponse
import com.hyperdev.tungguindesigner.network.ConnectivityStatus
import com.hyperdev.tungguindesigner.network.Response
import com.hyperdev.tungguindesigner.repository.LoginRepositoryImp
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.LoginView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.net.SocketTimeoutException

class LoginPresenter(private val view: LoginView.View,
                     private val login: LoginRepositoryImp,
                     private val scheduler: SchedulerProvider) : LoginView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun loginUser(context: Context, email: String, password: String) {
        view.showPregressBar()

        login.loginUser(email, password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<LoginResponse>{
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: LoginResponse) {
                    SharedPrefManager.getInstance(context).storeToken(t.getData?.token.toString())
                }

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