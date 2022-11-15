package com.app.muselink.data.modals.responses

import com.app.muselink.model.BaseResponse
import com.app.muselink.model.responses.getInterest.InterestsDatum
import com.app.muselink.model.responses.GetGoalsData

class GetInterestsGoalsRes : BaseResponse(){

    var data : GetInterestData?  =null


}

class GetInterestData{
    var personalInterest : ArrayList<InterestsDatum>? = null
    var personalCareerGoal : ArrayList<GetGoalsData>? = null
    var biography : String? = ""
}

