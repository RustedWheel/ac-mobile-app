package com.makeitez.acsalesapp.screens.announcement.newproduct

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.NewProductAnnouncement
import com.makeitez.acsalesapp.screens.announcement.DeleteAnnouncementDialog
import com.makeitez.acsalesapp.utils.ListContentState
import com.makeitez.acsalesapp.utils.extensions.listen
import com.makeitez.acsalesapp.utils.extensions.show
import com.makeitez.acsalesapp.utils.extensions.showProgressDialog
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_announcements.*

class NewProductAnnouncementsFragment : ACFragment.WithViewModel<NewProductAnnouncementsViewModel>(R.layout.fragment_announcements, NewProductAnnouncementsViewModel::class.java),
    AnnounceNewProductDialog.Listener, DeleteAnnouncementDialog.Listener, NewProductAnnouncementViewHolder.Listener {

    override val handlesOwnLoadingIndicator: Boolean
        get() = true

    private val args: NewProductAnnouncementsFragmentArgs by navArgs()

    private val isEditing: Boolean by lazy { args.isEditing }

    private val announcementListAdapter : NewProductAnnouncementListAdapter by lazy {
        NewProductAnnouncementListAdapter(isEditing, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBaseLook()
        viewModel.init(ACService.announcementService)
        viewModel.inProgress.observe(viewLifecycleOwner, Observer { syncProgress(it) })
        viewModel.announcements.observe(viewLifecycleOwner, Observer { syncData(it) })
        viewModel.contentState.observe(viewLifecycleOwner, Observer { syncContentState(it) })
        viewModel.deleteAnnouncementProgress.observe(viewLifecycleOwner, Observer { showProgressDialog(it) })

        announcementListRefreshLayout.setOnRefreshListener {
            viewModel.fetchAnnouncements()
        }

        announcementListRecycler.adapter = announcementListAdapter

        announcementAddCard.isVisible = isEditing
        announcementAddCard.setOnClickListener {
           AnnounceNewProductDialog().listen(this).show(this)
        }
    }

    private fun setUpBaseLook() {
        setActionBar(toolbar, R.string.new_products_title)
        announcementAddCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.ac_orange))
        announcementAddIconImage.setImageResource(R.drawable.ic_new_white)
        announcementAddLabelText.setText(R.string.announcements_add_new_product_label)
    }

    private fun syncProgress(inProgress: Boolean) {
        announcementListRefreshLayout.isRefreshing = inProgress
    }

    private fun syncData(announcementList: List<NewProductAnnouncement>) {
        announcementListAdapter.setList(announcementList)
    }

    private fun syncContentState(state: ListContentState) {
        announcementListRecycler.isVisible = state == ListContentState.NotEmpty
        announcementNoResultText.isVisible = state == ListContentState.EmptyData
    }

    override fun onNewProductAnnouncementDelete(announcementId: String, description: String) {
        DeleteAnnouncementDialog().withData(announcementId, description).listen(this).show(this)
    }

    override fun onNewProductAnnouncementAdded() {
        viewModel.onNewProductAnnouncementAdded()
    }

    override fun onDeleteAnnouncementConfirmed(announcementId: String) {
        viewModel.onDeleteAnnouncementConfirmed(announcementId)
    }
}