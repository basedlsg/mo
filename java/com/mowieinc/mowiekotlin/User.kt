package com.mowieinc.mowiekotlin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val accountType: Int = 0,
    val customerid: String? = null,
    val email: String? = null,
    val firstname: String? = null,
    val id: String? = null,
    val lastname: String? = null,
    val phonenumber: String? = null,
    val profilephotourl: String? = null
) : Parcelable
