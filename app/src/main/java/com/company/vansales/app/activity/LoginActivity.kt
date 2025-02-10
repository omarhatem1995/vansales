package com.company.vansales.app.activity

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.company.vansales.BuildConfig
import com.company.vansales.R
import com.company.vansales.app.NextActivity
import com.company.vansales.app.datamodel.models.localmodels.ApplicationSettings
import com.company.vansales.app.datamodel.models.mastermodels.LoginRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.LoginResponseModel
import com.company.vansales.app.domain.usecases.LoginUseCases
import com.company.vansales.app.utils.Constants
import com.company.vansales.app.utils.LocaleHelper
import com.company.vansales.app.utils.enums.SettingEnum
import com.company.vansales.app.utils.extensions.*
import com.company.vansales.app.view.entities.LoginViewState
import com.company.vansales.app.viewmodel.ApplicationSettingsViewModel
import com.company.vansales.app.viewmodel.StartDayViewModel
import com.company.vansales.databinding.LoginLayoutBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), LoginUseCases.View {

    private var setupApiUrl = false
    private var applicationSettings: ApplicationSettings? = null
    private lateinit var binding: LoginLayoutBinding
    val applicationSettingsViewModel: ApplicationSettingsViewModel by viewModels()
    val startDayViewModel: StartDayViewModel by viewModels()
    private var driverNumber: String = ""
    private var printerMACAddress: String = ""
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()!=null)
            LocaleHelper.setLocale(this, applicationSettingsViewModel.sharedPreferences.getLanguage()!!.toLowerCase()) // the code for setting your local
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login_layout)
        val versionName = BuildConfig.VERSION_NAME
        binding.versionNumber.text = "Version : $versionName"
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()?.toLowerCase() == "ar"){
            LocaleHelper.applyLocalizedContext(this, "ar")
            binding.root.layoutDirection = resources.configuration.layoutDirection
        }
        callBacks()
        initButtons()
//        Constants.CURRENT_LANG = applicationSettingsViewModel.getCurrentLanguage() ?: "EN"
        binding.isInitialPassword = false
    }

    private fun initButtons() {
        binding.loginSignInButton.setOnClickListener {
  /*          if (!setupApiUrl && applicationSettings == null) {
                this.displayToast(this.resources.getString(R.string.required_data))
            } else {*/
                if (checkNotEmptyData()) {
                    this.hideKeyboard()
                    driverNumber = if (binding.loginDriverNameEditText.text.toString().isNotEmpty()){
                        binding.loginDriverNameEditText.text.toString().addZerosToDriver()
                    }
                    else{
                        applicationSettings?.driver ?: "CMS1254"
                    }
                    if (driverNumber.isEmpty()){
                        driverNumber = "CMS1254"
                     }
                    if (binding.isInitialPassword == true) {
                        val newPass = binding.loginNewPasswordEditText.text.toString()
                        val confirmPass = binding.loginConfirmNewPasswordEditText.text.toString()
                        if (newPass.isEmpty()) {
                            binding.loginNewPasswordEditText.error = this.stringText(R.string.field_cannot_be_empty)
                            return@setOnClickListener
                        }
                        if (confirmPass.isEmpty()) {
                            binding.loginConfirmNewPasswordEditText.error = this.stringText(R.string.field_cannot_be_empty)
                            return@setOnClickListener
                        }
                        if (confirmPass != newPass) {
                            binding.loginNewPasswordEditText.error = this.stringText(R.string.should_match)
                            return@setOnClickListener
                        }
                        applicationSettingsViewModel.login(false,
                            LoginRequestModel(
                                driver = driverNumber.uppercase(),
                                userName = binding.loginUserNameEditText.value.toString(),
                                password = binding.loginNewPasswordEditText.text.toString(),
                                crtPass = "X"
                            )
                        )
                    }
                    else {
                        applicationSettingsViewModel.login(false,
                            LoginRequestModel(
                                driver = driverNumber.uppercase(),
                                userName = binding.loginUserNameEditText.value.toString(),
                                password = binding.loginPasswordEditText.value.toString()
                            )
                        )
                    }
                } else {
                    this.displayToast(R.string.please_check_empty_fields)
                }
            }
//        }

    }

    private fun callBacks() {
        applicationSettingsViewModel.getAppSettingsLiveData.observe(this) {
            applicationSettings = it
            it?.let { appSettings ->
                binding.loginDriverNameEditText.setText(appSettings.driver.toString())
                printerMACAddress = appSettings.printerMACAddress.toString()
                Log.d("asdkjsakdjsadk", "$printerMACAddress")
            }
            if (applicationSettings == null) {
                applicationSettingsViewModel.insert(
                    ApplicationSettings(
                        1, Constants.ENGLISH_LANGUAGE,
                        port = Constants.BASE_PORT.toInt(),
                        host = Constants.BASE_URL,
                        driver = driverNumber,
                        isDayStarted = false,
                        printerMACAddress = printerMACAddress
                    )
                )
                setupApiUrl = true
            } else {
                applicationSettingsViewModel.updateUrl()
            }
        }
        applicationSettingsViewModel.viewStateLogin.observe(this) { viewState ->
            when (viewState) {
                is LoginViewState.LoginSuccess -> {
                    renderLoading(false)
                    renderLoginSuccess(viewState.data)
                }
                is LoginViewState.Loading -> renderLoading(viewState.show)
                is LoginViewState.LoginFailure -> {
                    renderLoading(false)
                    renderNetworkFailure()
                }
                is LoginViewState.NetworkFailure -> {
                    renderLoading(false)
                    renderNetworkFailure()
                }
            }
        }
    }

    private fun checkNotEmptyData(): Boolean {
        return when {
            binding.loginUserNameEditText.value.toString().trim() == "" -> {
                binding.loginUserNameEditText.error =
                    getString(R.string.user_name_field_cannot_be_empty)
                false
            }
            binding.loginPasswordEditText.value.toString().trim() == "" -> {
                binding.loginPasswordEditText.error =
                    getString(R.string.password_field_cannot_be_empty)
                false
            }
            else -> {
                true
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun renderLoginSuccess(data: LoginResponseModel) {
        if (!data.msg1.isNullOrEmpty()) {
            this.displayToastLong(data.msg1)
        }
        when (data.data) {
            "S" -> {
                val userName = binding.loginUserNameEditText.value.toString()
                var isDayStarted = false
                /*applicationSettingsViewModel.getAppSettingsLiveData.observe(this) { appSettings ->
                    if(appSettings.isDayStarted!=null && appSettings.isDayStarted == true){
                        isDayStarted = true
                    }
                }*/
                //check if the user is the previous one or not
                val isTheSameUser =
                    applicationSettingsViewModel.sharedPreferences.getDriverName() != null &&
                            applicationSettingsViewModel.sharedPreferences.getDriverNumber() != null &&
                            applicationSettingsViewModel.sharedPreferences.getDriverName()!!.trim() == userName.trim() &&
                            applicationSettingsViewModel.sharedPreferences.getDriverNumber()!!.trim() == driverNumber.trim()

                val appSetting = applicationSettingsViewModel.getAppSettingsLiveData.value
                appSetting?.let {
                    applicationSettingsViewModel.updateSettings(
                        port = it.port.toString(),
                        host = it.host.toString(),
                        driver = driverNumber.uppercase(),
                        printerMACAddress = printerMACAddress
                    )
                }
  /*              applicationSettingsViewModel.userPreference.setUser(
                    binding.loginUserNameEditText.text.toString(),
                    driverNumber.uppercase()
                )*/
                applicationSettingsViewModel.sharedPreferences.setDriverNumber(driverNumber.uppercase())
                applicationSettingsViewModel.sharedPreferences.setDriverName(binding.loginUserNameEditText
                    .value.toString())

                //if not the same we just clear all data
                applicationSettingsViewModel.updateDayStatus(false)
                if(!isTheSameUser){
                    startDayViewModel.deleteAllOldData()
                }
                startActivity(Intent(this, StartDayActivity::class.java))
                finishAffinity()
            }
            "F" -> this.displayToast(R.string.please_enter_valid_data)
            "L" -> this.displayToast(getString(R.string.this_account_is_locked))
            "I" -> {
                this.displayToast("you should enter password again")
                binding.isInitialPassword = true
            }
        }
    }

    override fun renderLoading(show: Boolean) {
        binding.isLoading = show
    }

    override fun renderNetworkFailure() {
        this.displayToast(R.string.no_internet)
    }

}
