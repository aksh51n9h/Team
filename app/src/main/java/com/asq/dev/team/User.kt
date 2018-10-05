package com.asq.dev.team

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
        var name: String,
        var photoUrl: String
)