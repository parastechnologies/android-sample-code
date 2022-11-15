package com.app.muselink.ui.fragments.home.viewpager.soundfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.muselink.R
import com.app.muselink.base.BaseFragment
import kotlinx.android.synthetic.main.layour_project_roles.*
import kotlinx.android.synthetic.main.layout_project_goals.*

class DescriptionFragment : BaseFragment(){

    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_description, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }
    private fun setListeners() {
        llIntrumentalist.setOnClickListener {
            tvInstrulist.isSelected = true
            tvProducer.isSelected = false
            tvSongWriter.isSelected = false
            tvTopLiner.isSelected = false
            tvViewAll.isSelected = false
            tvVocalist.isSelected = false
            llIntrumentalist.isSelected = true
            llProducer.isSelected = false
            llSongWriter.isSelected = false
            llVocalist.isSelected = false
            lltopLiner.isSelected = false
            llViewAll.isSelected = false
        }
        llProducer.setOnClickListener {
            tvInstrulist.isSelected = false
            tvProducer.isSelected = true
            tvSongWriter.isSelected = false
            tvTopLiner.isSelected = false
            tvViewAll.isSelected = false
            tvVocalist.isSelected = false
            llIntrumentalist.isSelected = false
            llProducer.isSelected = true
            llSongWriter.isSelected = false
            llVocalist.isSelected = false
            lltopLiner.isSelected = false
            llViewAll.isSelected = false
        }
        llSongWriter.setOnClickListener {
            tvInstrulist.isSelected = false
            tvProducer.isSelected = false
            tvSongWriter.isSelected = true
            tvTopLiner.isSelected = false
            tvViewAll.isSelected = false
            tvVocalist.isSelected = false
            llIntrumentalist.isSelected = false
            llProducer.isSelected = false
            llSongWriter.isSelected = true
            llVocalist.isSelected = false
            lltopLiner.isSelected = false
            llViewAll.isSelected = false
        }
        llVocalist.setOnClickListener {
            tvInstrulist.isSelected = false
            tvProducer.isSelected = false
            tvSongWriter.isSelected = false
            tvTopLiner.isSelected = false
            tvViewAll.isSelected = false
            tvVocalist.isSelected = true
            llIntrumentalist.isSelected = false
            llProducer.isSelected = false
            llSongWriter.isSelected = false
            llVocalist.isSelected = true
            lltopLiner.isSelected = false
            llViewAll.isSelected = false
        }
        lltopLiner.setOnClickListener {
            tvInstrulist.isSelected = false
            tvProducer.isSelected = false
            tvSongWriter.isSelected = false
            tvTopLiner.isSelected = true
            tvViewAll.isSelected = false
            tvVocalist.isSelected = false
            llIntrumentalist.isSelected = false
            llProducer.isSelected = false
            llSongWriter.isSelected = false
            llVocalist.isSelected = false
            lltopLiner.isSelected = true
            llViewAll.isSelected = false
        }
        llViewAll.setOnClickListener {
            tvInstrulist.isSelected = false
            tvProducer.isSelected = false
            tvSongWriter.isSelected = false
            tvTopLiner.isSelected = false
            tvViewAll.isSelected = true
            tvVocalist.isSelected = false
            llIntrumentalist.isSelected = false
            llProducer.isSelected = false
            llSongWriter.isSelected = false
            llVocalist.isSelected = false
            lltopLiner.isSelected = false
            llViewAll.isSelected = true
        }
        llGoal1.setOnClickListener {
            llGoal1.isSelected =true
            llGoal2.isSelected =false
            llGoal3.isSelected =false
            llGoal4.isSelected =false
            llGoal5.isSelected =false
            llGoal6.isSelected =false
            tvGoal1.isSelected = true
            tvGoal2.isSelected = false
            tvGoal3.isSelected = false
            tvGoal4.isSelected = false
            tvGoal5.isSelected = false
            tvGoal6.isSelected = false
        }
        llGoal2.setOnClickListener {
            llGoal1.isSelected =false
            llGoal2.isSelected =true
            llGoal3.isSelected =false
            llGoal4.isSelected =false
            llGoal5.isSelected =false
            llGoal6.isSelected =false
            tvGoal1.isSelected = false
            tvGoal2.isSelected = true
            tvGoal3.isSelected = false
            tvGoal4.isSelected = false
            tvGoal5.isSelected = false
            tvGoal6.isSelected = false
        }
        llGoal3.setOnClickListener {
            llGoal1.isSelected =false
            llGoal2.isSelected =false
            llGoal3.isSelected =true
            llGoal4.isSelected =false
            llGoal5.isSelected =false
            llGoal6.isSelected =false
            tvGoal1.isSelected = false
            tvGoal2.isSelected = false
            tvGoal3.isSelected = true
            tvGoal4.isSelected = false
            tvGoal5.isSelected = false
            tvGoal6.isSelected = false
        }
        llGoal4.setOnClickListener {
            llGoal1.isSelected =false
            llGoal2.isSelected =false
            llGoal3.isSelected =false
            llGoal4.isSelected =true
            llGoal5.isSelected =false
            llGoal6.isSelected =false
            tvGoal1.isSelected = false
            tvGoal2.isSelected = false
            tvGoal3.isSelected = false
            tvGoal4.isSelected = true
            tvGoal5.isSelected = false
            tvGoal6.isSelected = false
        }
        llGoal5.setOnClickListener {
            llGoal1.isSelected =false
            llGoal2.isSelected =false
            llGoal3.isSelected =false
            llGoal4.isSelected =false
            llGoal5.isSelected =true
            llGoal6.isSelected =false
            tvGoal1.isSelected = false
            tvGoal2.isSelected = false
            tvGoal3.isSelected = false
            tvGoal4.isSelected = false
            tvGoal5.isSelected = true
            tvGoal6.isSelected = false
        }
        llGoal6.setOnClickListener {
            llGoal1.isSelected =false
            llGoal2.isSelected =false
            llGoal3.isSelected =false
            llGoal4.isSelected =false
            llGoal5.isSelected =false
            llGoal6.isSelected =true
            tvGoal1.isSelected = false
            tvGoal2.isSelected = false
            tvGoal3.isSelected = false
            tvGoal4.isSelected = false
            tvGoal5.isSelected = false
            tvGoal6.isSelected = true
        }
    }
}