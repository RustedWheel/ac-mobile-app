package com.makeitez.acsalesapp.screens.home

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACActivity
import com.makeitez.acsalesapp.models.ACUser
import com.makeitez.acsalesapp.screens.announcement.AnnouncementsActivity
import com.makeitez.acsalesapp.screens.products.ProductsActivity
import com.makeitez.acsalesapp.screens.salesorder.SalesOrderActivity
import com.makeitez.acsalesapp.utils.extensions.show
import com.makeitez.acsalesapp.utils.extensions.showProgressDialog
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : ACActivity.WithViewModel<HomeViewModel>(R.layout.activity_home, HomeViewModel::class.java), ConfirmLogoutDialog.Listener {

    override val handlesOwnLoadingIndicator: Boolean
        get() = true

    override fun onStart() {
        super.onStart()
        viewModel.fetchNotificationCount()
    }

    override fun onConfirmedCreate(savedInstanceState: Bundle?) {
        super.onConfirmedCreate(savedInstanceState)
        viewModel.init(ACService.generalService, ACService.authService)
        viewModel.state.observe(this, Observer { syncData(it) })
        viewModel.user.observe(this, Observer { syncUserData(it) })
        viewModel.inProgress.observe(this, Observer { syncProgress(it) })
        viewModel.logoutProgress.observe(this, Observer { showProgressDialog(it) })

        homeRefreshLayout.setOnRefreshListener {
            viewModel.fetchNotificationCount()
        }

        homeMenuButton.setOnClickListener {
            homeDrawer.openDrawer(GravityCompat.START)
        }

        homeGeneralAnnouncements.setOnClickListener {
            AnnouncementsActivity.show(this, AnnouncementsActivity.AnnouncementsViewType.General)
        }

        homeNewProductAnnouncements.setOnClickListener {
            AnnouncementsActivity.show(this, AnnouncementsActivity.AnnouncementsViewType.NewProducts)
        }

        homeCustomersCard.setOnClickListener {
            SalesOrderActivity.show(this, SalesOrderActivity.SalesOrderViewType.NewOrder)
        }

        homeHistoryCard.setOnClickListener {
            SalesOrderActivity.show(this, SalesOrderActivity.SalesOrderViewType.PreviousOrder)
        }

        homeProductsCard.setOnClickListener {
            ProductsActivity.show(this)
        }

        homeAnnouncementCard.setOnClickListener {
            AnnouncementsActivity.show(this, AnnouncementsActivity.AnnouncementsViewType.Admin)
        }

        homeApprovalsCard.setOnClickListener {
            SalesOrderActivity.show(this, SalesOrderActivity.SalesOrderViewType.PendingOrder)
        }

        homeSavedCard.setOnClickListener {
            SalesOrderActivity.show(this, SalesOrderActivity.SalesOrderViewType.SavedOrder)
        }

        homeLogoutButton.setOnClickListener {
            ConfirmLogoutDialog().show(this)
        }
    }

    override fun onLogoutConfirmed() {
        viewModel.logout()
    }

    private fun syncProgress(inProgress: Boolean) {
        homeRefreshLayout.isRefreshing = inProgress
    }

    private fun syncData(state: HomeViewModel.State) {
        with(state) {
            homeAppVersion.text = appVersion

            homeGeneralAnnouncementText.text = resources.getQuantityString(
                R.plurals.home_general_announcement_text, generalAnnouncementCount, generalAnnouncementCount)
            homeNewProductAnnouncementText.text = resources.getQuantityString(
                R.plurals.home_new_product_announcement_text, newProductAnnouncementCount, newProductAnnouncementCount)
            homeApprovalsCountText.text = pendingApprovalCount.toString()

            homeGeneralAnnouncements.isVisible = generalAnnouncementCount != 0
            homeNewProductAnnouncements.isVisible = newProductAnnouncementCount != 0
            homeApprovalsCountCard.isVisible = pendingApprovalCount != 0
        }
    }

    private fun syncUserData(user: ACUser) {
        homeWelcomeText.text = getString(R.string.home_hello_text, user.userName)
        homeAnnouncementTile.isVisible = user.isAdmin
        homeApprovalsTile.isVisible = user.isAdmin
        homeSavedTile.isVisible = !user.isAdmin
    }
}