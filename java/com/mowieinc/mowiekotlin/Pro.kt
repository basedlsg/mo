package com.mowieinc.mowiekotlin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pro(
    val accountType: Int = 1,
    val backgroundcheck: String? = null,
    val businessname: String? = null,
    val carphotourl: String? = null,
    val connectid: String? = null,
    val einnumber: String? = null,
    val email: String? = null,
    val firstname: String? = null,
    val id: String? = null,
    val lastname: String? = null,
    val needonboard: String? = null,
    val phonenumber: String? = null,
    val profilephotourl: String? = null,
    val rating: String? = null,
) : Parcelable