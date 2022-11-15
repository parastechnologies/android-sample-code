package com.app.muselink.ui.bottomsheets.musiclinkpro

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.app.muselink.R
import com.app.muselink.commonutils.SharedPrefs
import com.app.muselink.constants.AppConstants
import com.app.muselink.databinding.MuselinkproBottomsheetBinding
import com.app.muselink.helpers.InAppPurchase
import com.app.muselink.model.responses.SubscriptionRes
import com.app.muselink.ui.adapter.viewpageradapter.AdapterMuzelinkPro
import com.app.muselink.ui.base.dialogfragment.BaseViewModelDialogFragment
import com.app.muselink.util.WrappingViewPager
import dagger.hilt.android.AndroidEntryPoint
import soup.neumorphism.NeumorphButton
import soup.neumorphism.NeumorphCardView
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MuseLinkProBottomsheet (context: Context) :  BaseViewModelDialogFragment<MuselinkproBottomsheetBinding,MuselinkProViewModel>(){

    private var adapterMuzelinkPro: AdapterMuzelinkPro? = null
    var inAppPurchase : InAppPurchase?  =null
    var listProducts = ArrayList<String>()

    val inAppPurchaseNavigator = object : InAppPurchase.InAppPurchaseNavigator{
        override fun onPurchaseSuccess(purchase: Purchase) {
            if(IsMonthlyPur){
                viewModel?.callApiSubscription(purchase,listProducts[0],InAppPurchase.SubscriptionType.Monthly.value,inAppPurchase?.getPriceAccordingProductId(listProducts[0]).toString())
            }else{
                viewModel?.callApiSubscription(purchase,listProducts[1],InAppPurchase.SubscriptionType.Yearly.value,inAppPurchase?.getPriceAccordingProductId(listProducts[1]).toString())
            }
        }

        override fun onPurchaseFailed(message: String) {
            Toast.makeText(requireActivity(),message,Toast.LENGTH_SHORT).show()
        }

        override fun listSkuDetails(skuDetailsArrayList: ArrayList<SkuDetails>?) {
            setPriceInViews()
        }

    }

    private fun setPriceInViews() {
        tvPriceMonthly?.setText(inAppPurchase?.getPriceAccordingProductId(listProducts.get(0)))
        tvPriceYearly?.setText(inAppPurchase?.getPriceAccordingProductId(listProducts.get(1)))
    }

    var tvPriceYearly :TextView? =null
    var tvPriceMonthly :TextView? =null
    var btnUploadNewContent :NeumorphButton? =null
    var npmCardMonthly :NeumorphCardView? =null
    var npmCardYearly :NeumorphCardView? =null
    var IsMonthlyPur = true


    override fun onViewCreated(contentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(contentView, savedInstanceState)

        binding?.model =viewModel
        viewModel?.viewLifecycleOwner = this
        viewModel?.setSubscriptionObserver()
        viewModel?.addListener(muselinkProViewModelNavigator)

        listProducts =  ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.arrayProductIds)))
        inAppPurchase = InAppPurchase(requireActivity(),inAppPurchaseNavigator)
        inAppPurchase?.intializeBillingclient(InAppPurchase.BillingType.Subscription.value,listProducts)

        val viewpagerMuzelinkPro = contentView.findViewById<WrappingViewPager>(R.id.viewpagerMuzelinkPro)
        val imgClose = contentView.findViewById<ImageView>(R.id.imgClose)

        imgClose.setOnClickListener {
            dismiss()
        }

        val image1 = contentView.findViewById<ImageView>(R.id.image1)
        val image2 = contentView.findViewById<ImageView>(R.id.image2)
        val image3 = contentView.findViewById<ImageView>(R.id.image3)
        val image4 = contentView.findViewById<ImageView>(R.id.image4)
        val image5 = contentView.findViewById<ImageView>(R.id.image5)
        val image6 = contentView.findViewById<ImageView>(R.id.image6)
        val image7 = contentView.findViewById<ImageView>(R.id.image7)

        tvPriceYearly = contentView.findViewById<TextView>(R.id.tvPriceYearly)
        tvPriceMonthly = contentView.findViewById<TextView>(R.id.tvPriceMonthly)
        btnUploadNewContent = contentView.findViewById<NeumorphButton>(R.id.btnUploadNewContent)
        npmCardMonthly = contentView.findViewById<NeumorphCardView>(R.id.npmCardMonthly)
        npmCardYearly = contentView.findViewById<NeumorphCardView>(R.id.npmCardYearly)

        npmCardMonthly?.setOnClickListener {
            IsMonthlyPur = true
            npmCardMonthly?.setStrokeColor(ContextCompat.getColorStateList(
                requireActivity(),
                R.color.color_pink_100
            ))
            npmCardYearly?.setStrokeColor(ContextCompat.getColorStateList(
                requireActivity(),
                android.R.color.transparent
            ))
        }

        npmCardYearly?.setOnClickListener {
            IsMonthlyPur = false
            npmCardYearly?.setStrokeColor(ContextCompat.getColorStateList(
                requireActivity(),
                R.color.color_pink_100
            ))
            npmCardMonthly?.setStrokeColor(ContextCompat.getColorStateList(
                requireActivity(),
                android.R.color.transparent
            ))
        }

        npmCardMonthly?.performClick()

        btnUploadNewContent?.setOnClickListener {
            if(IsMonthlyPur){
                inAppPurchase?.purchase(listProducts[0])
            }else{
                inAppPurchase?.purchase(listProducts[1])
            }
        }


        adapterMuzelinkPro = AdapterMuzelinkPro(childFragmentManager)
        viewpagerMuzelinkPro.adapter = adapterMuzelinkPro

        viewpagerMuzelinkPro.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(pos: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if(position==0){
                    selectedImage(image1,image2,image3,image4,image5,image6,image7)
                }else if(position ==1){
                    selectedImage(image2,image1,image3,image4,image5,image6,image7)
                }else if(position ==2){
                    selectedImage(image3,image1,image2,image4,image5,image6,image7)
                }else if(position ==3){
                    selectedImage(image4,image1,image3,image2,image5,image6,image7)
                }else if(position ==4){
                    selectedImage(image5,image2,image3,image4,image1,image6,image7)
                }else if(position ==5){
                    selectedImage(image6,image2,image3,image4,image5,image1,image7)
                }else if(position ==6){
                    selectedImage(image7,image2,image3,image4,image5,image6,image1)
                }
            }

        })

    }


    val muselinkProViewModelNavigator = object :
        MuselinkProViewModel.MuselinkProViewModelNavigator {
        override fun onSuccessApi(subscriptionRes: SubscriptionRes) {
            Toast.makeText(requireActivity(),getString(R.string.purchased_successfully),Toast.LENGTH_SHORT).show()
            SharedPrefs.save(
                AppConstants.PREFS_SUBSCRIPTION_STATUS,
                1
            )
            dismiss()
        }
    }

    private fun selectedImage(
        selectedImage: ImageView?,
        unselected1: ImageView?,
        unselected2: ImageView?,unselected3: ImageView?,unselected4: ImageView?,unselected5: ImageView?,unselected6: ImageView?
    ) {
        selectedImage?.setBackgroundResource(R.drawable.drawable_circle_pink)
        unselected1?.setBackgroundResource(android.R.color.transparent)
        unselected2?.setBackgroundResource(android.R.color.transparent)
        unselected3?.setBackgroundResource(android.R.color.transparent)
        unselected4?.setBackgroundResource(android.R.color.transparent)
        unselected5?.setBackgroundResource(android.R.color.transparent)
        unselected6?.setBackgroundResource(android.R.color.transparent)
    }

    override fun getViewModelClass(): Class<MuselinkProViewModel> {
        return MuselinkProViewModel::class.java
    }

    override fun getLayout(): Int {
        return R.layout.muselinkpro_bottomsheet
    }

}