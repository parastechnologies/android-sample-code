package com.app.muselink.ui.fragments.chatscreen.modals

import java.io.Serializable

class ModalMessage :Serializable{
    var senderName: String? = null
    var sender_id: String? = null
    var receiverName: String? = null
    var receiver_id: String? = null
    var message: String? = null
    var created_on: String? = null
    var MessageType: String? = null}


