package com.app.muselink.ui.bottomsheets

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.retrofit.ApiRepository
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.adapter.search.AdapterSearchHistory
import com.app.muselink.util.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import soup.neumorphism.NeumorphCardView
import javax.inject.Inject

@AndroidEntryPoint
class SearchBottomSheet : BottomSheetDialogFragment() {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    var adapterSearchHistory: AdapterSearchHistory? = null
    private val requestApi = MutableLiveData<HashMap<String, String>>()

    private var searchType = "Audio"
    private var categoryType = "All"
    private var noDataFound: TextView? = null

    private var progressBar: ProgressBar? = null

    val listSearchHistory = ArrayList<ModalAudioFile>()

    @Inject
    lateinit var repository: ApiRepository
    private val search = requestApi.switchMap { requestApi ->
        repository.search(requestApi)
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        getDialog()?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val contentView = View.inflate(context, R.layout.search_bottom_sheet, null)
        val npmAll = contentView.findViewById<NeumorphCardView>(R.id.npmAll)
        val npmLocation = contentView.findViewById<NeumorphCardView>(R.id.npmLocation)
        val npmUsername = contentView.findViewById<NeumorphCardView>(R.id.npmUsername)
        val recycleSearchHistory = contentView.findViewById<RecyclerView>(R.id.recycleSearchHistory)
        val imgClose = contentView.findViewById<ImageView>(R.id.imgClose)
        val etSearch = contentView.findViewById<TextView>(R.id.etSearch)
        noDataFound = contentView.findViewById(R.id.noDataFound)
        val searchAudio = contentView.findViewById<NeumorphCardView>(R.id.searchAudio)
        progressBar = contentView.findViewById(R.id.progressBar)
        imgClose.setOnClickListener {
            dismiss()
        }
        val linearLayoutManager = LinearLayoutManager(context)
        recycleSearchHistory?.layoutManager = linearLayoutManager
        adapterSearchHistory = AdapterSearchHistory(requireActivity(), listSearchHistory)
        recycleSearchHistory!!.adapter = adapterSearchHistory
        setupObservers()
        adapterSearchHistory?.notifyDataSetChanged()
        npmAll.setOnClickListener {
            categoryType = "All"
            val hashmap = hashMapOf(
                "searchType" to searchType,
                "type" to categoryType,
                "searchContent" to etSearch.text.trim().toString()
            )
            requestApi.value = hashmap
            selectedView(npmAll, npmLocation, npmUsername)
        }
        npmLocation.setOnClickListener {
            categoryType = "location"
            val hashmap = hashMapOf(
                "searchType" to searchType,
                "type" to categoryType,
                "searchContent" to etSearch.text.trim().toString()
            )
            requestApi.value = hashmap
            selectedView(npmLocation, npmAll, npmUsername)
        }
        npmUsername.setOnClickListener {
            categoryType = "userName"
            val hashmap = hashMapOf(
                "searchType" to searchType,
                "type" to categoryType,
                "searchContent" to etSearch.text.trim().toString()
            )
            requestApi.value = hashmap
            Log.d("asdadadad",hashmap.toString())

            selectedView(npmUsername, npmLocation, npmAll)
        }
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.toString()==""){
                    val hashmap = hashMapOf(
                        "searchType" to searchType,
                        "type" to categoryType,
                        "searchContent" to s.toString()
                    )
                    requestApi.value = hashmap
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }
        dialog.setContentView(contentView)
        searchAudio.setOnClickListener {
            val hashmap = hashMapOf(
                "searchType" to searchType,
                "type" to categoryType,
                "searchContent" to etSearch.text.trim().toString()
            )
            requestApi.value = hashmap
        }
        val hashmap = hashMapOf(
            "searchType" to searchType,
            "type" to categoryType,
            "searchContent" to etSearch.text.trim().toString()
        )
        requestApi.value = hashmap
    }

    private fun selectedView(
        cardSelected: NeumorphCardView,
        card2: NeumorphCardView,
        card3: NeumorphCardView
    ) {
        cardSelected.setStrokeColor(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.color_black_100
            )
        )
        card2.setStrokeColor(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.color_white_100
            )
        )
        card3.setStrokeColor(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.color_white_100
            )
        )
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: FrameLayout? =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View?>(bottomSheet!!)
        val layoutParams: ViewGroup.LayoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        layoutParams.height = windowHeight
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = false
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    /**
     *
     * Get description observer
     * */
    private fun setupObservers() {
        search.observe(requireActivity(), { it->
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    progressBar?.visibility = View.INVISIBLE
                    listSearchHistory.clear()
                    if (it.data != null) {
                        if (it.data.status.equals("200")) {
                            listSearchHistory.addAll(it.data.data!!)
                        } else {
                            showToast(requireActivity(), it.message)
                        }
                    } else {
                        showToast(requireActivity(), it.message)
                    }
                    noDataFound?.visibility = if (listSearchHistory.size <= 0) View.VISIBLE else View.GONE
                    adapterSearchHistory?.notifyDataSetChanged()
                }
                Resource.Status.ERROR -> {
                    progressBar?.visibility = View.INVISIBLE
                }
                Resource.Status.LOADING -> {
                    progressBar?.visibility = View.VISIBLE
                }
            }
        })
    }


}