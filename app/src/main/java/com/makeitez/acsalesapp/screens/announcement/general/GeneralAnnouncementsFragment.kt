package com.makeitez.acsalesapp.screens.announcement.general

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.GeneralAnnouncement
import com.makeitez.acsalesapp.screens.announcement.DeleteAnnouncementDialog
import com.makeitez.acsalesapp.utils.ListContentState
import com.makeitez.acsalesapp.utils.extensions.listen
import com.makeitez.acsalesapp.utils.extensions.show
import com.makeitez.acsalesapp.utils.extensions.showProgressDialog
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_announcements.*

class GeneralAnnouncementsFragment : ACFragment.WithViewModel<GeneralAnnouncementsViewModel>(R.layout.fragment_announcements, GeneralAnnouncementsViewModel::class.java),
    AddGeneralAnnouncementDialog.Listener, DeleteAnnouncementDialog.Listener, GeneralAnnouncementViewHolder.Listener {

    override val handlesOwnLoadingIndicator: Boolean
        get() = true

    private val args: GeneralAnnouncementsFragmentArgs by navArgs()

    private val isEditing: Boolean by lazy { args.isEditing }

    private val announcementListAdapter: GeneralAnnouncementListAdapter by lazy {
        GeneralAnnouncementListAdapter(isEditing, this)
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
            AddGeneralAnnouncementDialog().listen(this).show(this)
        }
    }

    private fun setUpBaseLook() {
        setActionBar(toolbar, R.string.announcements_title)
        announcementAddCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.ac_red))
        announcementAddIconImage.setImageResource(R.drawable.ic_add)
        announcementAddLabelText.setText(R.string.announcements_add_general_label)
    }

    private fun syncProgress(inProgress: Boolean) {
        announcementListRefreshLayout.isRefreshing = inProgress
    }

    private fun syncData(announcementList: List<GeneralAnnouncement>) {
        announcementListAdapter.setList(announcementList)
    }

    private fun syncContentState(state: ListContentState) {
        announcementListRecycler.isVisible = state == ListContentState.NotEmpty
        announcementNoResultText.isVisible = state == ListContentState.EmptyData
    }

    override fun onGeneralAnnouncementAdded() {
        viewModel.onGeneralAnnouncementAdded()
    }

    override fun onDeleteAnnouncementConfirmed(announcementId: String) {
        viewModel.onDeleteAnnouncementConfirmed(announcementId)
    }

    override fun onGeneralAnnouncementDelete(announcementId: String, description: String) {
        DeleteAnnouncementDialog().withData(announcementId, description).listen(this).show(this)
    }
}