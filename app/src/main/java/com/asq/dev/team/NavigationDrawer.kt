package com.asq.dev.team

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.navigation_drawer.view.*

class NavigationDrawer : BottomSheetDialogFragment(){
    companion object {
        fun newInstance() = NavigationDrawer()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.navigation_drawer, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.appVersion.text = "Version \u2022 ${BuildConfig.VERSION_NAME}"
    }
}