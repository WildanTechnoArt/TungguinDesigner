package com.hyperdev.tungguindesigner.presenter

import com.hyperdev.tungguindesigner.model.fcm.FcmResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.TokenFCMView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class TokenFCMPresenter(private val baseApiService: BaseApiService,
                        private val scheduler: SchedulerProvider) : TokenFCMView.Presenter{

    private val compositeDisposable = CompositeDisposable()

    override fun sendTokenFCM(authHeader: String, accept: String, token: String) {

        baseApiService.fcmRequest(authHeader, accept, token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(scheduler.io())
            .unsubscribeOn(scheduler.io())
            .subscribe(object : Observer<FcmResponse>{
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: FcmResponse) {}

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

            })
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}