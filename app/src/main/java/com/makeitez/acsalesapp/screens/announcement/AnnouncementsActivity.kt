package com.makeitez.acsalesapp.screens.announcement

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACActivity

class AnnouncementsActivity : ACActivity(R.layout.activity_announcements) {

    enum class AnnouncementsViewType {
        General,
        NewProducts,
        Admin
    }

    private val announcementsViewType: AnnouncementsViewType by lazy {
        intent.getStringExtra(ANNOUNCEMENTS_VIEW_TYPE_KEY)?.let { AnnouncementsViewType.valueOf(it) } ?: AnnouncementsViewType.General
    }

    private val navController: NavController by lazy {
        this.findNavController(R.id.announcementsNavHost)
    }

    override fun onConfirmedCreate(savedInstanceState: Bundle?) {
        super.onConfirmedCreate(savedInstanceState)
        setupNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    private fun setupNavigation() {
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_announcements)
        navGraph.startDestination = when (announcementsViewType) {
            AnnouncementsViewType.General -> {
                R.id.generalAnnouncementsFragment
            }
            AnnouncementsViewType.NewProducts -> {
                R.id.newProductAnnouncementsFragment
            }
            AnnouncementsViewType.Admin -> {
                R.id.announcementTypeFragment
            }
        }
        navController.graph = navGraph
    }



    companion object {
        private const val ANNOUNCEMENTS_VIEW_TYPE_KEY = "announcementsViewType"

        fun show(activity: ACActivity, viewType: AnnouncementsViewType) {
            val intent = Intent(activity, AnnouncementsActivity::class.java).apply {
                putExtra(AnnouncementsActivity.ANNOUNCEMENTS_VIEW_TYPE_KEY, viewType.toString())
            }
            activity.startActivity(intent)
        }
    }

}