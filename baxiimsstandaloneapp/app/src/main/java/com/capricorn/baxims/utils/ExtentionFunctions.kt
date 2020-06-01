package com.capricorn.baxims.utils

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.NonNull
import com.afollestad.materialdialogs.MaterialDialog
import com.capricorn.baxims.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

/**
 * this are extension functions to aid rapid development and abstractions
 * */
inline fun<reified T:Any> newIntent(activity: Activity): Intent = Intent(activity,T::class.java)

inline  fun <reified T:Any> Activity.navigateTo(
    noinline init: Intent.()->Unit={}){
    val intent=newIntent<T>(this)
    intent.init()
    startActivity(intent)
}

/** connection manager **/
@NonNull
fun Context.isConnectedToTheInternet(): Boolean{
    val cnxManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    try{
        val netInfo : NetworkInfo? = cnxManager.activeNetworkInfo
        return netInfo?.isConnectedOrConnecting ?: false
    }catch (e: Exception){
        Log.e(ContentValues.TAG, "isConnectedToTheInternet: ${e.message}")
    }
    return false
}

/** material dialog extension **/
fun Activity.showDialog(title:String,message:String){
    MaterialDialog(this).show {
        cornerRadius(5F)
        title(text = title)
        message(text = message)

        positiveButton(R.string.agree) { dialog ->
            hide()
        }

        negativeButton {  }

    }

}

fun Context.showToast(message:String){
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
}



fun Activity.AlertDialog(message:String){
    MaterialDialog(this).show {
        cornerRadius(5F)
        message(text = message)
        positiveButton(R.string.yes) {
            hide()
        }

    }

}


fun SharedPreferences.savepref(key:String,data:String){
    this.edit().putString(key,data).apply()
}

//A Button extension for loading state : used in Auth
/**it has to state load state and default state 0-load 1-default*/
fun Button.loadState(state:Int,message: String){
    this.apply {
        when(state){
            0->{
                alpha=0.6f
                text=message
                isClickable=false
            }
            1->{
                alpha=1.0f
                text=message
                isClickable=true
            }
        }

    }
}

fun Activity.hideSoftKeyboard(){
    val view=this.currentFocus
    view?.let {
        val imm=getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(it.windowToken,0)
    }
}