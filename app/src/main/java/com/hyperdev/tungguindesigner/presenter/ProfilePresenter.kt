package com.hyperdev.tungguindesigner.presenter

import android.support.v7.app.AppCompatActivity.RESULT_OK
import android.content.Context
import android.support.v4.app.Fragment
import android.widget.Toast
import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.network.ConnectivityStatus.Companion.isConnected
import com.hyperdev.tungguindesigner.network.HandleError
import com.hyperdev.tungguindesigner.repository.ProfileRepositoryImpl
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.ProfileView
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ProfilePresenter(private val view: ProfileView.View,
                       private val profile: ProfileRepositoryImpl,
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

        compositeDisposable.add(profile.getProfile(token, "application/json")
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

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}