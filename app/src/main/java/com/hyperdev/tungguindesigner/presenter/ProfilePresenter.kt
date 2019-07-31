package com.hyperdev.tungguindesigner.presenter

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.ConnectivityStatus.Companion.isConnected
import com.hyperdev.tungguindesigner.network.HandleError
import com.hyperdev.tungguindesigner.network.Response
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.ProfileView
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.ResourceSubscriber
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ProfilePresenter(private val context: Context,
                       private val view: ProfileView.View,
                       private val baseApiService: BaseApiService,
                       private val scheduler: SchedulerProvider) : ProfileView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun takeImageFromGallry(fragment: Fragment) {
        compositeDisposable.add(
            RxPaparazzo.single(fragment)
                .usingGallery()
                .subscribeOn(scheduler.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if(it.resultCode() != RESULT_OK){
                        return@subscribe
                    }
                    view.loadFile(it.data())
                    view.hideProgress()
                }
        )
    }

    override fun getUserProfile(token: String, context: Context) {
        view.displayProgress()

        compositeDisposable.add(baseApiService.getProfile(token, "application/json")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : ResourceSubscriber<ProfileResponse>(){
                override fun onComplete() {
                    view.hideProgress()
                }

                override fun onNext(t: ProfileResponse) {
                    try{
                        t.data?.let { view.displayProfile(it) }
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

    override fun updateProfile(authHeader: String, accept: String, name: RequestBody,
                               email: RequestBody, phone: RequestBody) {

        view.displayProgress()

        baseApiService.updateProfile(authHeader, accept, name, email, phone)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<ProfileResponse> {
                override fun onComplete() {
                    view.onSuccessEditProfile()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: ProfileResponse) {}

                override fun onError(e: Throwable) {
                    view.hideProgress()

                    if(isConnected(context)){
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

    override fun updateProfileWithImage(authHeader: String, accept: String, name: RequestBody,
                                        email: RequestBody, phone: RequestBody, image: MultipartBody.Part) {

        view.displayProgress()

        baseApiService.updateProfileWithImage(authHeader, accept, name, email, phone, image)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<ProfileResponse> {
                override fun onComplete() {
                    view.onSuccessEditFotoProfile()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: ProfileResponse) {}

                override fun onError(e: Throwable) {
                    view.hideProgress()

                    if(isConnected(context)){
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