package com.hyperdev.tungguindesigner.presenter

import android.content.Context
import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.ProfileView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber

class DesignerPresenter(
    private val view: ProfileView.DesignerId,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : ProfileView.DesignerPresenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getDesignerId(token: String, context: Context) {
        compositeDisposable.add(
            baseApiService.getProfile(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<ProfileResponse>() {
                    override fun onComplete() {}

                    override fun onNext(t: ProfileResponse) {
                        try {
                            t.data?.let { view.getHashedId(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}