package com.makeitez.acsalesapp.screens.announcement

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_announcement_type.*

class AnnouncementTypeFragment : ACFragment(R.layout.fragment_announcement_type) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar, R.string.announcement_type_title)

        announcementGeneralTypeCard.setOnClickListener {
            findNavController().navigate(AnnouncementTypeFragmentDirections
                .actionAnnouncementTypeFragmentToGeneralAnnouncementsFragment(isEditing = true))
        }

        announcementNewProductTypeCard.setOnClickListener {
            findNavController().navigate(AnnouncementTypeFragmentDirections
                .actionAnnouncementTypeFragmentToNewProductAnnouncementsFragment(isEditing = true))
        }
    }
}