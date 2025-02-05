package com.company.vansales.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.sap.cloud.mobile.flowv2.ext.FlowStateListener
import com.sap.cloud.mobile.foundation.model.AppConfig
import com.sap.cloud.mobile.foundation.authentication.AppLifecycleCallbackHandler
import com.sap.cloud.mobile.flowv2.model.FlowType
import com.sap.cloud.mobile.foundation.common.addUniqueInterceptor
import com.sap.cloud.mobile.foundation.mobileservices.ApplicationStates
import com.sap.cloud.mobile.foundation.networking.BlockedUserInterceptor
import com.sap.cloud.mobile.foundation.networking.BlockedUserInterceptor.BlockType
import com.sap.cloud.mobile.foundation.networking.LastConnectionTimeInterceptor
import com.sap.cloud.mobile.foundation.settings.policies.ClientPolicies
import org.slf4j.LoggerFactory
import okhttp3.OkHttpClient
import java.util.Date

class WizardFlowStateListener(private val application: SAPWizardApplication) :
    FlowStateListener() {

    private var sharedPreferences: SharedPreferences? = null
    private var userSwitchFlag = false

    override fun onAppConfigRetrieved(appConfig: AppConfig) {
    }

    override fun onApplicationReset() {
        this.application.resetApplication()
    }

    override fun onApplicationLocked() {
        super.onApplicationLocked()
        application.isApplicationUnlocked = false
    }

    override fun onOkHttpClientReady(httpClient: OkHttpClient) {
        val lastConnectionTimeInterceptor = LastConnectionTimeInterceptor(object :
            LastConnectionTimeInterceptor.LastConnectionTimeCallback {
            override fun updateLastConnectionTime() {
                sharedPreferences = application.getSharedPreferences(
                    SAPWizardApplication.KEY_LOCK_WIPE_POLICY_PARAMETERS_PREFERENCE, Context.MODE_PRIVATE
                )
                sharedPreferences?.edit()?.apply {
                    val dateTimeNow = Date().time
                    val dateTime = Date(dateTimeNow)
                    logger.info("sharedPreferences save data", "Saving lastConnection: $dateTime")
                    putLong(SAPWizardApplication.LAST_VALID_CONNECTION_TIME, dateTimeNow)
                }?.apply()
            }
        })

        val blockedUserInterceptor = BlockedUserInterceptor(object :
            BlockedUserInterceptor.BlockedUserCallback {
            override fun handleBlockedUser(blockType: BlockType) {
                if ((blockType == BlockType.REGISTRATION_WIPED) || (blockType == BlockType.REGISTRATION_LOCKED)) {
                    var isFlowRunning: Boolean = false
                    AppLifecycleCallbackHandler.getInstance().activity?.let {
                        isFlowRunning = (it.componentName.className == FLOW_ACTIVITY_NAME)
                        if (isFlowRunning) {
                            sharedPreferences = application.getSharedPreferences(
                                SAPWizardApplication.KEY_LOCK_WIPE_POLICY_PARAMETERS_PREFERENCE, Context.MODE_PRIVATE
                            )
                            sharedPreferences?.edit()?.apply {
                                logger.info(
                                    "sharedPreferences save data",
                                    "Saving the flag of blockType to invoke lock or wipe when flow is done : $blockType"
                                )
                                putString(SAPWizardApplication.LOCK_WIPE_INVOKING_TYPE, blockType.name)
                            }?.apply()
                        }
                    }
                    if (!isFlowRunning) {
                        application.applyLockOrWipeByServer(blockType)
                    }
                }
            }
        })

        httpClient.addUniqueInterceptor(
            interceptor = lastConnectionTimeInterceptor,
            save = true
        ).addUniqueInterceptor(
            interceptor = blockedUserInterceptor,
            save = true
        )
    }

    override fun onFlowFinished(flowName: String?) {
        flowName?.let{
            application.isApplicationUnlocked = true
        }

        if (userSwitchFlag) {
            Intent(application, MainBusinessActivity::class.java).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                application.startActivity(it)
            }
        }

        if (FlowType.TIMEOUT_UNLOCK.toString() == flowName && !ApplicationStates.isNetworkAvailable) {
            application.applyLockWipePolicy()
        } else {
            sharedPreferences = application.getSharedPreferences(
                SAPWizardApplication.KEY_LOCK_WIPE_POLICY_PARAMETERS_PREFERENCE, Context.MODE_PRIVATE
            )
            sharedPreferences?.getString(SAPWizardApplication.LOCK_WIPE_INVOKING_TYPE, BlockType.UNDEFINED_BLOCK.name)
                ?.let { blockType ->
                    if (!blockType.equals(BlockType.UNDEFINED_BLOCK.name)) {
                        var invokingType = when (blockType) {
                            BlockType.REGISTRATION_LOCKED.name -> {
                                BlockType.REGISTRATION_LOCKED
                            }
                            BlockType.REGISTRATION_WIPED.name -> {
                                BlockType.REGISTRATION_WIPED
                            }
                            else -> {
                                BlockType.UNDEFINED_BLOCK
                            }
                        }
                        //Clear the flag of blockType which indicates lock or wipe should be invoked or not
                        sharedPreferences?.edit()?.apply {
                            logger.info(
                                "sharedPreferences remove data",
                                "Clear the flag of blockType: $blockType"
                            )
                            remove(SAPWizardApplication.LOCK_WIPE_INVOKING_TYPE)
                        }?.apply()
                        application.applyLockOrWipeByServer(invokingType)
                    }
                }
        }
    }

    override fun onClientPolicyRetrieved(policies: ClientPolicies) {
        policies.blockWipingPolicy?.also { blockWipingPolicy ->
            sharedPreferences = application.getSharedPreferences(
                SAPWizardApplication.KEY_LOCK_WIPE_POLICY_PARAMETERS_PREFERENCE,
                Context.MODE_PRIVATE
            )
            sharedPreferences?.edit()?.apply {
                logger.info("Save data to sharedPreferences", "enable: ${blockWipingPolicy.blockWipeEnabled}, lockDays: ${blockWipingPolicy.blockDisconnectedPeriod}, wipeDays: ${blockWipingPolicy.wipeDisconnectedPeriod}")
                putBoolean(SAPWizardApplication.LOCK_WIPE_ENABLED, blockWipingPolicy.blockWipeEnabled)
                putInt(SAPWizardApplication.LOCK_WIPE_BLOCK_PERIOD, blockWipingPolicy.blockDisconnectedPeriod)
                putInt(SAPWizardApplication.LOCK_WIPE_WIPE_PERIOD, blockWipingPolicy.wipeDisconnectedPeriod)
            }?.apply()
        }
    }


    companion object {
        private val logger = LoggerFactory.getLogger(WizardFlowStateListener::class.java)
        const val FLOW_ACTIVITY_NAME = "com.sap.cloud.mobile.flowv2.core.FlowActivity"
    }
}
