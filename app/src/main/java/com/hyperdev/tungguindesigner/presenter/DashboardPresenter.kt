package com.hyperdev.tungguindesigner.presenter

import android.content.Context
import android.widget.Toast
import com.hyperdev.tungguindesigner.model.announcement.AnnouncementResponse
import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.model.transactionhistori.TransactionResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.ConnectivityStatus.Companion.isConnected
import com.hyperdev.tungguindesigner.network.HandleError
import com.hyperdev.tungguindesigner.utils.SchedulerProvider
import com.hyperdev.tungguindesigner.view.DashboardView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException
import java.net.SocketTimeoutException

class DashboardPresenter(
    private val view: DashboardView.View,
    private val baseApiService: BaseApiService,
    private val scheduler: SchedulerProvider
) : DashboardView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getUserProfile(token: String, context: Context) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getProfile(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<ProfileResponse>() {
                    override fun onComplete() {
                        view.onSuccess()
                    }

                    override fun onNext(t: ProfileResponse) {
                        try {
                            t.data?.let { view.showProfile(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        view.hideProgress()

                        if (isConnected(context)) {
                            when (e) {
                                is HttpException -> // non 200 error codes
                                    HandleError.handleError(e, e.code(), context)
                                is SocketTimeoutException -> // connection errors
                                    Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Tidak Terhubung Dengan Internet!", Toast.LENGTH_LONG).show()
                        }
                    }
                })
        )
    }

    override fun getDashboardData(token: String, context: Context) {
        view.displayProgress()

        compositeDisposable.add(
            baseApiService.getDashboard(token, "application/json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<TransactionResponse>() {
                    override fun onComplete() {
                        view.onSuccess()
                    }

                    override fun onNext(t: TransactionResponse) {
                        try {
                            t.data?.let { view.loadSaldoData(it) }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        view.hideProgress()

                        if (isConnected(context)) {
                            when (e) {
                                is HttpException -> // non 200 error codes
                                    HandleError.handleError(e, e.code(), context)
                                is SocketTimeoutException -> // connection errors
                                    Toast.makeText(context, "Connection Timeout!", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Tidak Terhubung Dengan Internet!", Toast.LENGTH_LONG).show()
                        }
                    }
                })
        )
    }


    override fun getAnnouncementData(token: String) {

        compositeDisposable.add(
            baseApiService.announcementDesigner(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : ResourceSubscriber<AnnouncementResponse>() {
                    override fun onComplete() {}

                    override fun onNext(t: AnnouncementResponse) {
                        try {
                            t.data?.let { view.loaddAnnouncement(it) }
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