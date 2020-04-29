package com.bobs.mapque.nmapver.util.ext

import androidx.fragment.app.Fragment
import com.bobs.baselibrary.ext.hideKeyboard
import com.bobs.baselibrary.ext.showKeyboard
import com.bobs.baselibrary.ext.toast
import com.bobs.mapque.nmapver.ui.MainActivity

fun Fragment.showLoading(){
    val mainActivity = activity as MainActivity
    mainActivity.showLoading()
}

fun Fragment.hideLoading(){
    val mainActivity = activity as MainActivity
    mainActivity.hideLoading()
}

fun Fragment.showKeyboard(){
    val mainActivity = activity as MainActivity
    mainActivity.showKeyboard()
}

fun Fragment.hideKeyboard(){
    val mainActivity = activity as MainActivity
    mainActivity.hideKeyboard()
}

fun Fragment.toast(msg: String){
    val mainActivity = activity as MainActivity
    mainActivity.toast(msg)
}