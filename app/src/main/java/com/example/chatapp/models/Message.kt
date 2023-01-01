package com.example.chatapp.models

import java.util.Date

class Message {
    var from: String? = null
    var to: String? = null
    var message: String? = null
    var isSeen: String? = null
    var date: Date? = null

    constructor(from: String?, to: String?, message: String?, isSeen: String?, date: Date?) {
        this.from = from
        this.to = to
        this.message = message
        this.isSeen = isSeen
        this.date = date
    }

    constructor()
}