package com.hyperdev.tungguindesigner.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.hyperdev.tungguindesigner.GlideApp
import com.hyperdev.tungguindesigner.R
import com.hyperdev.tungguindesigner.database.SharedPrefManager
import com.hyperdev.tungguindesigner.model.profile.DataUser
import com.hyperdev.tungguindesigner.model.profile.ProfileResponse
import com.hyperdev.tungguindesigner.network.BaseApiService
import com.hyperdev.tungguindesigner.network.NetworkUtil
import com.hyperdev.tungguindesigner.presenter.ProfilePresenter
import com.hyperdev.tungguindesigner.repository.ProfileRepositoryImpl
import com.hyperdev.tungguindesigner.utils.AppSchedulerProvider
import com.hyperdev.tungguindesigner.utils.Validation.Companion.validateEmail
import com.hyperdev.tungguindesigner.utils.Validation.Companion.validateFields
import com.hyperdev.tungguindesigner.view.ProfileView
import com.hyperdev.tungguindesigner.view.ui.LoginActivity
import com.hyperdev.tungguindesigner.view.ui.UpdatePassActivity
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileFragment : Fragment(), ProfileView.View {

    // View
    private lateinit var btnEditProfile: Button
    private lateinit var btnEditPass: Button
    private lateinit var btnTakePhoto: FloatingActionButton
    private lateinit var nameDesigner: TextInputEditText
    private lateinit var emailDesigner: TextInputEditText
    private lateinit var nomorDesigner: TextInputEditText
    private lateinit var getKode: TextView
    private lateinit var imageProfile: CircleImageView
    private lateinit var nameLayout: TextInputLayout
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var myToolbar: Toolbar

    private lateinit var presenter: ProfileView.Presenter
    private lateinit var baseApiService: BaseApiService
    private lateinit var getToken: String
    private lateinit var namaUser: RequestBody
    private lateinit var nomorUser: RequestBody
    private lateinit var emailUser: RequestBody
    private lateinit var requestFile: RequestBody
    private lateinit var imageFile: MultipartBody.Part
    private lateinit var file: File
    private var updateImage = false

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        setHasOptionsMenu(true)

        btnEditProfile = view.findViewById(R.id.btnEditProfil)
        btnEditPass = view.findViewById(R.id.btnEditPass)
        nameDesigner = view.findViewById(R.id.my_name)
        emailDesigner = view.findViewById(R.id.my_email)
        nomorDesigner = view.findViewById(R.id.phone_number)
        getKode = view.findViewById(R.id.id_designer)
        imageProfile = view.findViewById(R.id.profile_image)
        nameLayout = view.findViewById(R.id.nameLayout)
        refreshLayout = view.findViewById(R.id.refreshLayout)
        btnTakePhoto = view.findViewById(R.id.btn_takePhoto)
        myToolbar = view.findViewById(R.id.toolbar) as Toolbar

        ((activity as AppCompatActivity).setSupportActionBar(myToolbar))

        refreshLayout.setOnRefreshListener {
            presenter.getUserProfile("Bearer $getToken", context!!)
        }

        btnTakePhoto.setOnClickListener {
            presenter.takeImageFromGallry(this)
        }

        btnEditProfile.setOnClickListener {
            if(btnEditProfile.text == "Edit Profil"){
                btnEditProfile.text = "Simpan"
                enabledView()
                btnEditPass.text = "Tutup"
            }else if(btnEditProfile.text == "Simpan"){
                editProfile()
            }
        }

        btnEditPass.setOnClickListener {
            if(btnEditPass.text == "Tutup"){
                disabledView()
                btnEditPass.text = "Ubah Password"
            }else if(btnEditPass.text == "Ubah Password"){
                val intent = Intent(context!!, UpdatePassActivity::class.java)
                intent.putExtra("userName", nameDesigner.text.toString())
                intent.putExtra("userEmail", emailDesigner.text.toString())
                intent.putExtra("userPhone", nomorDesigner.text.toString())
                startActivity(intent)
            }
        }

        return view
    }

    private fun setRequestBody(data: String): RequestBody{
        return RequestBody.create(MediaType.parse("plain/text"), data)
    }

    override fun loadFile(file: FileData?) {

        refreshLayout.isRefreshing = true

        this.file = file?.file!!

        updateImage = true

        requestFile = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), file.file)
        imageFile = MultipartBody.Part.createFormData("photo", file.filename, requestFile)

        editProfile()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData(){
        getToken = SharedPrefManager.getInstance(context!!).token.toString()

        baseApiService = NetworkUtil.getClient(context!!)!!
            .create(BaseApiService::class.java)

        val request = ProfileRepositoryImpl(baseApiService)
        val scheduler = AppSchedulerProvider()

        presenter = ProfilePresenter(this, request, scheduler)
        presenter.getUserProfile("Bearer $getToken", context!!)

    }

    private fun editProfile(){
        namaUser = setRequestBody(nameDesigner.text.toString())
        nomorUser = setRequestBody(nomorDesigner.text.toString())
        emailUser = setRequestBody(emailDesigner.text.toString())

        var err = 0

        if(!validateFields(nameDesigner.text.toString())){
            err++
            nameDesigner.error = "Nama tidak boleh kosong !"
        }

        if(!validateFields(nomorDesigner.text.toString())){
            err++
            nomorDesigner.error = "Nomor telepon tidak boleh kosong !"
        }

        if(!validateEmail(emailDesigner.text.toString())){
            err++
            emailDesigner.error = "Email tidak valid !"
        }

        if(err == 0){
            if(updateImage){
                editRequestWithImage(namaUser, emailUser, nomorUser)
                updateImage = false
            }else{
                editRequest(namaUser, emailUser, nomorUser)
            }
        }
    }

    private fun editRequest(name: RequestBody, email: RequestBody, number: RequestBody){
        refreshLayout.isRefreshing = true
        baseApiService.updateProfile("Bearer $getToken", "application/json", name, email, number)
            .enqueue(object : Callback<ProfileResponse> {

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("Profile.kt", "onFailure ERROR -> "+ t.message)
                    showSnackBarMessage("Tidak ada koneksi internet !")
                    refreshLayout.isRefreshing = false
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                        btnEditPass.text = "Ubah Password"
                        presenter.getUserProfile("Bearer $getToken", context!!)
                        disabledView()
                        refreshLayout.isRefreshing = false
                        Toast.makeText(context!!, "Data Profil Berhasil Diubah", Toast.LENGTH_SHORT).show()
                    }else{
                        refreshLayout.isRefreshing = false
                        val gson = Gson()
                        val message = gson.fromJson(response.errorBody()?.charStream(), ProfileResponse::class.java)
                        if(message != null){
                            Toast.makeText(context!!, message.getMeta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun editRequestWithImage(name: RequestBody, email: RequestBody, number: RequestBody){
        baseApiService.updateProfileWithImage("Bearer $getToken", "application/json", name, email, number, imageFile)
            .enqueue(object : Callback<ProfileResponse> {

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("Profile.kt", "onFailure ERROR -> "+ t.message)
                    refreshLayout.isRefreshing = false
                    showSnackBarMessage("Tidak ada koneksi internet !")
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        Log.i("debug", "onResponse: BERHASIL")
                        GlideApp.with(this@ProfileFragment)
                            .load(file)
                            .into(profile_image)
                        Toast.makeText(context!!, "Foto Profil Berhasil Diubah", Toast.LENGTH_SHORT).show()
                    }else{
                        refreshLayout.isRefreshing = false
                        val gson = Gson()
                        val message = gson.fromJson(response.errorBody()?.charStream(), ProfileResponse::class.java)
                        if(message != null){
                            Toast.makeText(context!!, message.getMeta?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(profile_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.option_view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.logout -> {
                val logout = AlertDialog.Builder(context!!)
                logout.setTitle("Konfirmasi")
                logout.setMessage("Anda yakin ingin keluar?")
                logout.setNegativeButton("Tidak") { d, _ ->
                    d.dismiss()
                }
                logout.setPositiveButton("Ya") { _, _ ->
                    SharedPrefManager.getInstance(context!!).deleteToken()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                    (context as Activity).finish()
                }
                logout.setCancelable(false)
                logout.create()
                logout.show()
            }
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun displayProfile(profileItem: DataUser) {

        GlideApp.with(context!!)
            .load(profileItem.photoUrl.toString())
            .placeholder(R.drawable.circle_profil)
            .into(imageProfile)

        getKode.text = "Kode: ${profileItem.formattedId.toString()}"
        nameDesigner.setText(profileItem.name.toString())
        emailDesigner.setText(profileItem.email.toString())
        nomorDesigner.setText(profileItem.phoneNumber.toString())
    }

    override fun displayProgress() {
        refreshLayout.isRefreshing = true
    }

    override fun hideProgress() {
        refreshLayout.isRefreshing = false
    }

    private fun enabledView(){
        nameDesigner.isEnabled = true
        emailDesigner.isEnabled = true
        nomorDesigner.isEnabled = true
        nameLayout.isCounterEnabled = true
    }

    @SuppressLint("SetTextI18n")
    private fun disabledView(){
        btnEditProfile.text = "Edit Profil"
        nameDesigner.isEnabled = false
        emailDesigner.isEnabled = false
        nomorDesigner.isEnabled = false
        nameLayout.isCounterEnabled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}