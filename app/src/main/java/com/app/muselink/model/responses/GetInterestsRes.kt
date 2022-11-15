package com.app.muselink.data.modals.responses

import com.app.muselink.model.BaseResponse
import com.app.muselink.model.responses.getInterest.InterestsDatum

class GetInterestsRes : BaseResponse() {

    var data = ArrayList<InterestsDatum>()

}