package com.app.muselink.ui.fragments.home.viewpager.comments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.databinding.FragmentCommentsBinding
import com.app.muselink.ui.activities.home.HomeActivity
import com.app.muselink.ui.adapter.SmileyAdapter
import com.app.muselink.ui.base.fragment.BaseViewModelFragment
import com.app.muselink.ui.fragments.home.dashboard.DashBoardFragment
import kotlinx.android.synthetic.main.fragment_comments.*

class CommentsFragment(val dashBoardFragment: DashBoardFragment) :
    BaseViewModelFragment<FragmentCommentsBinding, CommentsFragmentViewModel>() {
    private var smileyAdapter: SmileyAdapter? = null
    private var rvSmiley: RecyclerView? = null
    private var smileyArraylist = ArrayList<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.vm = viewModel
        viewModel?.binding = binding
        viewModel?.initRecyclerView()
        rvSmiley = view.findViewById(R.id.rvSmiley)

        initRecyclerViewSmiley()
        setListeners()
    }

    fun hideshowComment(){
        if(SharedPrefs.isUserLogin()){
            ll_bottombar.visibility = View.VISIBLE
            rvSmiley?.visibility = View.VISIBLE
        }else{
            ll_bottombar.visibility = View.GONE
            rvSmiley?.visibility = View.GONE
        }
    }

    /**
     * Init RecyclerView
     * */
    private fun initRecyclerViewSmiley() {
        val linearLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvSmiley?.layoutManager = linearLayoutManager
        smileyAdapter = SmileyAdapter(requireActivity(), smileys())
        smileyAdapter?.listener = object : SmileyAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                var stringBuilder = StringBuilder(binding?.etComment?.text.toString())
                stringBuilder = stringBuilder.append(smileyArraylist[position])
                binding?.etComment?.setText(stringBuilder)
                binding?.etComment?.setSelection(binding?.etComment?.text.toString().length)
            }
        }
        rvSmiley?.adapter = smileyAdapter
    }

    fun setToDefaultSettings() {
        viewUpper.visibility = View.GONE
        viewAllComments.visibility = View.VISIBLE
        btnCloseComment.visibility = View.GONE
        dashBoardFragment.showHideViewForDesc(true)
        if (requireActivity() is HomeActivity) {
            (requireActivity() as HomeActivity).showHideSearchButton(true)
        }
    }

    private fun setListeners() {
        viewAllComments.setOnClickListener {
            viewAllComments.visibility = View.GONE
            btnCloseComment.visibility = View.VISIBLE
            dashBoardFragment.showHideViewForDesc(false)
            if (requireActivity() is HomeActivity) {
                (requireActivity() as HomeActivity).showHideSearchButton(false)
            }
            viewUpper.visibility = View.VISIBLE
        }
        btnCloseComment.setOnClickListener {
            viewUpper.visibility = View.GONE
            viewAllComments.visibility = View.VISIBLE
            btnCloseComment.visibility = View.GONE
            dashBoardFragment.showHideViewForDesc(true)
            if (requireActivity() is HomeActivity) {
                (requireActivity() as HomeActivity).showHideSearchButton(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideshowComment()
        viewModel?.connectionToWebSocket()
    }

    override fun onPause() {
        super.onPause()
        viewModel?.onDispose()
    }


    private fun smileys(): List<String> {
        smileyArraylist = ArrayList()
        smileyArraylist.add("😊")
        smileyArraylist.add("😆")
        smileyArraylist.add("😬")
        smileyArraylist.add("😐")
        smileyArraylist.add("🤔")
        smileyArraylist.add("😜")
        smileyArraylist.add("😏")
        smileyArraylist.add("😳")
        smileyArraylist.add("😍")
        return smileyArraylist
    }

    override fun getViewModelClass(): Class<CommentsFragmentViewModel> {
        return CommentsFragmentViewModel::class.java
    }

    override fun getLayout(): Int {
        return R.layout.fragment_comments
    }
}