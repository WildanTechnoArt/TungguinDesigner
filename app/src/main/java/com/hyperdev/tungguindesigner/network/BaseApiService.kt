package com.hyperdev.tungguindesigner.network

import com.hyperdev.tungguindesigner.model.TestimoniHistori.TestimoniResponse
import com.hyperdev.tungguindesigner.model.announcement.AnnouncementResponse
import com.hyperdev.tungguindesigner.model.chartorder.ChartResponse
import com.hyperdev.tungguindesigner.model.fcm.FcmResponse
import com.hyperdev.tungguindesigner.model.historiorder.HistoriOrderResponse
import com.hyperdev.tungguindesigner.model.login.LoginResponse
import com.hyperdev.tungguindesigner.model.ordernotification.AcceptResponse
import com.hyperdev.tungguindesigner.model.ordernotification.CheckOrderResponse
import com.hyperdev.tungguindesigner.model.ordernotification.RejectResponse
import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.model.requestwithdraw.WithdrawResponse
import com.hyperdev.tungguindesigner.model.transactionhistori.TransactionResponse
import com.hyperdev.tungguindesigner.model.withdrawhistori.WithdrawHisResponse
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface BaseApiService {

    @POST("designer/login")
    @FormUrlEncoded
    fun loginRequest(@Field("email") email: String,
                     @Field("password") password: String): Call<LoginResponse>

    @GET("designer/profile")
    fun getProfile(@Header("Authorization") authHeader: String,
                   @Header("Accept") accept: String) : Flowable<ProfileResponse>

    @POST("designer/profile-update")
    @Multipart
    fun updateProfileWithImage(@Header("Authorization") authHeader: String,
                               @Header("Accept") accept: String,
                               @Part("name") name: RequestBody,
                               @Part("email") email: RequestBody,
                               @Part("phone_number") phone: RequestBody,
                               @Part image: MultipartBody.Part): Call<ProfileResponse>

    @POST("designer/profile-update")
    @Multipart
    fun updateProfile(@Header("Authorization") authHeader: String,
                      @Header("Accept") accept: String,
                      @Part("name") name: RequestBody,
                      @Part("email") email: RequestBody,
                      @Part("phone_number") phone: RequestBody): Call<ProfileResponse>

    @POST("designer/profile-update")
    @FormUrlEncoded
    fun updatePassword(@Header("Authorization") authHeader: String,
                       @Header("Accept") accept: String,
                       @Field("name") name: String,
                       @Field("email") email: String,
                       @Field("phone_number") phone: String,
                       @Field("password") password: String,
                       @Field("password_confirmation") c_password: String): Call<ProfileResponse>

    @GET("designer/wallet/withdraw")
    fun getWithdrawHistori(@Header("Authorization") authHeader: String,
                           @Header("Accept") accept: String,
                           @Query("page") page: Int) : Flowable<WithdrawHisResponse>

    @GET("designer/wallet")
    fun getTransactionHistori(@Header("Authorization") authHeader: String,
                              @Header("Accept") accept: String,
                              @Query("page") page: Int) : Flowable<TransactionResponse>

    @GET("designer/wallet")
    fun getDashboard(@Header("Authorization") authHeader: String,
                     @Header("Accept") accept: String) : Flowable<TransactionResponse>

    @GET("designer/order")
    fun getOrderHistori(@Header("Authorization") authHeader: String,
                        @Header("Accept") accept: String,
                        @Query("page") page: Int) : Flowable<HistoriOrderResponse>

    @POST("designer/wallet/withdraw")
    @FormUrlEncoded
    fun requestWithdraw(@Header("Authorization") authHeader: String,
                        @Header("Accept") accept: String,
                        @Field("bank_owner") bankOwner: String,
                        @Field("bank_name") bankName: String,
                        @Field("bank_account") bankAccount: String,
                        @Field("bank_branch") bankBranch: String,
                        @Field("amount") amount: String): Call<WithdrawResponse>

    @GET("designer/order/testimonial")
    fun getTestimoniHistori(@Header("Authorization") authHeader: String,
                            @Header("Accept") accept: String,
                            @Query("page") page: Int) : Flowable<TestimoniResponse>

    @POST("designer/fcm-token")
    @FormUrlEncoded
    fun fcmRequest(@Header("Authorization") authHeader: String,
                   @Header("Accept") accept: String,
                   @Field("fcm_token") token: String): Call<FcmResponse>

    @POST("designer/status")
    @FormUrlEncoded
    fun setToogleStatus(@Header("Authorization") authHeader: String,
                        @Header("Accept") accept: String,
                        @Field("is_active") token: String): Call<ProfileResponse>

    @GET("designer/order-statsistic")
    fun getChartOrder(@Header("Authorization") authHeader: String,
                      @Header("Accept") accept: String) : Flowable<ChartResponse>

    @GET("designer/order/{order_id}/accept")
    fun acceptOrder(@Header("Authorization") authHeader: String,
                    @Header("Accept") accept: String,
                    @Path("order_id") order_id: String) : Flowable<AcceptResponse>

    @GET("designer/order/{order_id}/reject")
    fun rejectOrder(@Header("Authorization") authHeader: String,
                    @Header("Accept") accept: String,
                    @Path("order_id") order_id: String) : Flowable<RejectResponse>

    @GET("designer-announcement")
    fun announcementDesigner(@Header("Authorization") authHeader: String) : Flowable<AnnouncementResponse>

    @GET("designer/order/{order_id}/check")
    fun checkOrderOffer(@Header("Authorization") authHeader: String,
                        @Path("order_id") orderId: String) : Flowable<CheckOrderResponse>

}