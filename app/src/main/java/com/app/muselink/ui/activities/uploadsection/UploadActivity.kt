package com.app.muselink.ui.activities.uploadsection

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.muselink.R
import com.app.muselink.base.BaseActivity
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.constants.IntentConstant
import com.app.muselink.model.responses.GetGoalsData
import com.app.muselink.retrofit.Resource
import com.app.muselink.ui.adapter.AdapterCommonPolygon
import com.app.muselink.ui.dialogfragments.UploadStep2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_upload.*
import kotlinx.android.synthetic.main.layout_select_goals.*
import kotlinx.android.synthetic.main.toolbar_black.*
import soup.neumorphism.NeumorphCardView
import soup.neumorphism.ShapeType


@AndroidEntryPoint
class UploadActivity : BaseActivity() {
    val viewModel: UploadViewModel by lazy { ViewModelProvider(this).get(UploadViewModel::class.java) }
    var adapterCommonPolygon: AdapterCommonPolygon? = null
    var adapterCommonPolygon2: AdapterCommonPolygon? = null
    val listCategories = ArrayList<GetGoalsData>()
    private val initArrayList = ArrayList<GetGoalsData>()
    val arrayListTest = ArrayList<GetGoalsData>()
    var musicPath: String? = ""
    var musicPath15Sec: String? = ""
    var descriptionColor: String = "#73CADC"
    private var arrayListRole: ArrayList<String> = ArrayList()
    private var edtDescription: EditText? = null
    private var btnUpload: Button? = null
    var lengthSet = false

    override fun getLayout(): Int {
        return R.layout.activity_upload
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicPath = intent.extras?.getString(IntentConstant.MusicPath, "")
        musicPath15Sec = intent.extras?.getString(IntentConstant.MusicPath15Sec, "")
        arrayListRole.addAll(intent.extras?.getSerializable(IntentConstant.Role) as ArrayList<String>)
        setToolbar()
        initView()
        setListeners()
        edtNumber.addTextChangedListener(textWatcher)
        initObserver()
        if (!SharedPrefs.getBoolean(AppConstants.PREFS_MUSIC_TUTORIAL_2)) {
            addFragment()
        }
    }
    fun setData(number: String) {
        edtNumber.removeTextChangedListener(textWatcher)
        edtNumber.setText(number)
        edtNumber.setSelection(number.length)
        edtNumber.addTextChangedListener(textWatcher)
    }

    private var textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val data = edtNumber.text.toString().replace(" ", "")
            val length = data.length
            if (!lengthSet) {
                if (length > 2) {
                    lengthSet = true
                    val data1 = data.substring(0, 2)
                    val data2 = data.substring(2, length)
                    setData("$data1 $data2")
                }
            } else {
                if (length == 2) {
                    lengthSet = false
                }
            }
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun handleTagging(text: CharSequence, start: Int) {
        if (text.length <= start) return

        val formattedText = text.substring(0, start + 1).trim();
        val lastWord = formattedText.split(Regex("\\s+")).last()
        val tagIndex =
            if (lastWord.isNotEmpty() && lastWord[0] == '@') formattedText.lastIndexOf('@') else -1

        if (tagIndex >= 0) {
            val foregroundSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary))
            edtDescription?.text
                ?.setSpan(foregroundSpan, tagIndex, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun setListeners() {

        edtDescription?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(
                text: CharSequence?,
                start: Int,
                lengthBefore: Int,
                lengthAfter: Int
            ) {
                text?.let { handleTagging(text, start) }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        btnUpload?.setOnClickListener {
            val arrayList = ArrayList<String>()
            for (model in listCategories) {
                if (model.IsSelected) {
                    arrayList.add(model.Goal_Id!!)
                }
            }
            if (arrayList.isEmpty()) {
                Toast.makeText(this, getString(R.string.select_goal), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (edtDescription?.text.toString().trim().isEmpty()) {
                Toast.makeText(this, getString(R.string.enter_description), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val text: String = edtDescription?.text.toString()
            val words = text.split(" ").toTypedArray()
            val tags: MutableList<String> = ArrayList()
            if (words.size>=0){
                for (word in words) {
                    if (word.substring(0, 1) == "#" || word.substring(0, 1) == "@") {
                        tags.add(word.substring(1))
                    }
                }
            }
            val bundle = bundleOf(
                IntentConstant.MusicPath to musicPath,
                IntentConstant.MusicPath15Sec to musicPath15Sec,
                IntentConstant.Role to arrayListRole,
                IntentConstant.Goal to arrayList,
                IntentConstant.Description to edtDescription?.text.toString(),
                IntentConstant.TAGS to tags,
                IntentConstant.DescriptionColor to descriptionColor
            )
            val intent = Intent(this, UploadCompleteActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        npmCard1.setOnClickListener {
            tvDescriptionLabel.setTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            tvLabelNumbers.setTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            edtDescription?.setTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            edtDescription?.setHintTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            tvLabelBackGround.setTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            selectCard(R.color.colorSky, npmCard1, npmCard2, npmCard3, nmpCard4)
            llBackGround.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSky))
            rlToolbarBackGround.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSky))

            descriptionColor = "#73CADC"

        }

        npmCard2.setOnClickListener {
            tvDescriptionLabel.setTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            tvLabelNumbers.setTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            edtDescription?.setTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            edtDescription?.setHintTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            tvLabelBackGround.setTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            selectCard(R.color.colorPink, npmCard2, npmCard1, npmCard3, nmpCard4)
            llBackGround.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPink))
            rlToolbarBackGround.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPink))
            descriptionColor = "#EF729E"
        }

        npmCard3.setOnClickListener {
            tvDescriptionLabel.setTextColor(ContextCompat.getColor(this, R.color.color_black_100))
            tvLabelNumbers.setTextColor(ContextCompat.getColor(this, R.color.color_black_100))
            edtDescription?.setTextColor(ContextCompat.getColor(this, R.color.color_black_100))
            edtDescription?.setHintTextColor(ContextCompat.getColor(this, R.color.color_black_100))
            tvLabelBackGround.setTextColor(ContextCompat.getColor(this, R.color.color_black_100))
            selectCard(R.color.color_white_100, npmCard3, npmCard1, npmCard2, nmpCard4)
            llBackGround.setBackgroundColor(ContextCompat.getColor(this, R.color.color_white_100))
            rlToolbarBackGround.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.color_white_100
                )
            )
            descriptionColor = "#fff"
        }

        nmpCard4.setOnClickListener {
            tvDescriptionLabel.setTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            tvLabelNumbers.setTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            edtDescription?.setTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            edtDescription?.setHintTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            tvLabelBackGround.setTextColor(ContextCompat.getColor(this, R.color.color_white_100))
            selectCard(R.color.color_black_100, nmpCard4, npmCard1, npmCard2, npmCard3)
            llBackGround.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorBackgroundBlack
                )
            )
            rlToolbarBackGround.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorBackgroundBlack
                )
            )
            descriptionColor = "#000000"
        }
        npmCard1?.performClick()
    }

    private fun selectCard(
        color: Int,
        selectedCard: NeumorphCardView,
        unSelectedCard: NeumorphCardView,
        unSelectedCard1: NeumorphCardView,
        unSelectedCard2: NeumorphCardView
    ) {
        selectedCard.setShadowColorDark(ContextCompat.getColor(this, R.color.color_black_25))
        selectedCard.setShadowColorLight(ContextCompat.getColor(this, R.color.color_black_100))
        selectedCard.setShapeType(ShapeType.PRESSED)
        selectedCard.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))


        unSelectedCard.setShadowColorDark(ContextCompat.getColor(this, R.color.color_black_15))
        unSelectedCard.setShadowColorLight(ContextCompat.getColor(this, R.color.color_white_20))
        unSelectedCard.setBackgroundColor(ContextCompat.getColor(this, color))
        unSelectedCard.setShapeType(ShapeType.FLAT)


        unSelectedCard1.setShadowColorDark(ContextCompat.getColor(this, R.color.color_black_15))
        unSelectedCard1.setShadowColorLight(ContextCompat.getColor(this, R.color.color_white_20))
        unSelectedCard1.setBackgroundColor(ContextCompat.getColor(this, color))
        unSelectedCard1.setShapeType(ShapeType.FLAT)


        unSelectedCard2.setShadowColorDark(ContextCompat.getColor(this, R.color.color_black_15))
        unSelectedCard2.setShadowColorLight(ContextCompat.getColor(this, R.color.color_white_20))
        unSelectedCard2.setBackgroundColor(ContextCompat.getColor(this, color))
        unSelectedCard2.setShapeType(ShapeType.FLAT)
//        unSelectedCard2.setStrokeColor(ContextCompat.getColorStateList(this,color))

    }

    private fun setToolbar() {
        backPresstoolbar.setOnClickListener {
            onBackPressed()
        }

    }

    private fun initObserver() {
        viewModel.getGoals.observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    if (it.data?.data!!.isNotEmpty()) {
                        listCategories.clear()
                        for (i in it.data.data!!.indices) {
                            if (i <= 4) {
                                initArrayList.add(it.data.data!![i])
                            } else {
                                arrayListTest.add(it.data.data!![i])
                            }
                        }
                        if (it.data.data!!.size > 5) {
                            val modalCategory = GetGoalsData()
                            modalCategory.IsSelected = false
                            modalCategory.Goal_Name = getString(R.string.more)
                            initArrayList.add(modalCategory)
                        }
                        setRecyclerList(initArrayList)
                    }
                }
                Resource.Status.ERROR -> {
                    progressBar.visibility = View.GONE
                }
                Resource.Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initView() {

        edtDescription = findViewById(R.id.edtDescription)
        btnUpload = findViewById(R.id.btnUpload)


        showHideMoreView(false)
        rvGoals?.layoutManager = GridLayoutManager(this, 2)
        adapterCommonPolygon = AdapterCommonPolygon(this, adapterCategoriesNavigator)
        rvGoals!!.adapter = adapterCommonPolygon


        adapterCommonPolygon2 = AdapterCommonPolygon(this, adapterCategoriesMoreNavigator)
        rvGoalsAll?.layoutManager = GridLayoutManager(this, 2)
        rvGoalsAll!!.adapter = adapterCommonPolygon2

        imgcloseMoreview?.setOnClickListener {
            setRecyclerList(initArrayList)
        }
    }

    private fun setRecyclerList(data: ArrayList<GetGoalsData>?) {
        showHideMoreView(false)
        listCategories.clear()
        listCategories.addAll(data!!)
        adapterCommonPolygon?.setData(listCategories)
    }

    private fun showHideMoreView(IsShow: Boolean) {
        if (IsShow) {
            rlMoreViewGoals.visibility = View.VISIBLE
            llSelectGoalView.visibility = View.GONE
        } else {
            rlMoreViewGoals.visibility = View.GONE
            llSelectGoalView.visibility = View.VISIBLE
        }
    }

    private fun showMoreRoleView(data: ArrayList<GetGoalsData>?) {
        showHideMoreView(true)
        listCategories.removeAt(listCategories.size - 1)
        listCategories.addAll(data!!)
        adapterCommonPolygon2?.setData(listCategories)
    }

    val adapterCategoriesMoreNavigator =
        object : AdapterCommonPolygon.AdapterCommonPolygonNavigator {
            override fun onClickCategory(position: Int) {
                if (listCategories[position].Goal_Name!! == getString(R.string.more)) {
                    showMoreRoleView(arrayListTest)
                } else {
                    listCategories[position].IsSelected = !listCategories[position].IsSelected
                    adapterCommonPolygon2?.notifyDataSetChanged()
                }
            }

        }


    private val adapterCategoriesNavigator =
        object : AdapterCommonPolygon.AdapterCommonPolygonNavigator {
            override fun onClickCategory(position: Int) {
                if (listCategories[position].Goal_Name!! == getString(R.string.more)) {
                    showMoreRoleView(arrayListTest)
                } else {
                    listCategories[position].IsSelected = !listCategories[position].IsSelected
                    adapterCommonPolygon?.notifyDataSetChanged()
                }
            }

        }

    fun addFragment() {
        supportFragmentManager.beginTransaction().add(UploadStep2(), "Upload2").commit()
    }
}