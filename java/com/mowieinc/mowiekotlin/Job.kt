package com.mowieinc.mowiekotlin

import com.google.firebase.database.*
import com.google.firebase.database.PropertyName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Job(
    val streetnumber: String = "",
    val streetname: String = "",
    val cityaddress: String = "",
    val stateaddress: String = "",
    val zipcode: String = "",
    val frequency: String = "",
    @get:PropertyName("package") @set:PropertyName("package") var packageName: String = "", // Explicit getter and setter
    val day: String = "",
    val yardsize: String = "",
    val note: String = "",
    val status: String = "",
    val subid: String = "",
    val userid: String = "",
    val proid: String = "",
    val carphotourl: String = "",
    val jobstate: String = "",
    val jobid: String = "",
    val profilephotourl: String = "",
    val rating: String = ""
) : Parcelable


