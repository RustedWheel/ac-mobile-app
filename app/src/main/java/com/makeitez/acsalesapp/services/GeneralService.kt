package com.makeitez.acsalesapp.services

import com.makeitez.acsalesapp.models.HomeNotificationSummary
import com.makeitez.acsalesapp.utils.helper.OneSignalHelper
import com.makeitez.acsalesapp.utils.helper.StorageHelper

class GeneralService(private val networkService: NetworkService) {

    suspend fun fetchHomeNotificationSummary(): HomeNotificationSummary {
        val response = networkService.call {
            it.getHomeNotificationSummary()
        }
        return response.data ?: HomeNotificationSummary()
    }

    /**
     * Send app's OneSignal player id to backend once, only if it is available
     *
     * https://documentation.onesignal.com/docs/users#when-does-the-onesignal-player-id-change
     */
    suspend fun storeOneSignalPlayerID() {
        if(!StorageHelper.getBool(HAS_STORED_PLAYER_ID, false)) {
            OneSignalHelper.getPlayerId()?.let { osPlayerID ->
                networkService.callBase {
                    it.updatePlayerID(osPlayerID)
                }

                StorageHelper.setBool(HAS_STORED_PLAYER_ID, true)
            }
        }
    }

    companion object {
        private const val HAS_STORED_PLAYER_ID = "hasStoredPlayerId"
    }
}