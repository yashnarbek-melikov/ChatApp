package com.example.chatapp.models

import java.util.Date

class Account: java.io.Serializable {

    var displayName: String? = null
    var email: String? = null
    var uid: String? = null
    var photoUrl: String? = null
    var status: String? = null
    var date: Date? = null

    constructor(displayName: String?, email: String?, uid: String?, photoUrl: String?, status: String?, date: Date) {
        this.displayName = displayName
        this.email = email
        this.uid = uid
        this.photoUrl = photoUrl
        this.status = status
        this.date = date
    }

    constructor()
}