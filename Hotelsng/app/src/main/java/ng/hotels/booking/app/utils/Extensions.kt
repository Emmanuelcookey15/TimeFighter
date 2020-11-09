package ng.hotels.booking.app.utils

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import ng.hotels.booking.app.R
import org.json.JSONObject

/**
 * Created by Emem on 7/4/18.
 */
fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun Context.customTabText(text:String, tab: TabLayout.Tab?){
    val tabText = LayoutInflater.from(this).inflate(R.layout.design_layout_tab_text, null) as TextView
    tabText.setTextColor(ContextCompat.getColor(this, android.R.color.white))
    tabText.text = text
    tab?.customView = tabText
}

fun Activity.showAppSnackbar(mainTextStringId: String, showTime:Int = Snackbar.LENGTH_SHORT, actionString: String = "",
                             listener: View.OnClickListener? = null){
    Snackbar.make(
            this.findViewById(android.R.id.content),
            mainTextStringId,
            Snackbar.LENGTH_INDEFINITE)
            .setAction(actionString, listener).show()

}

fun favouritehotelCheck(propertyName: String, propertyNameKey: String, tinydb: TinyDB): Boolean{

    var alreadyAdded = false
    for (value in tinydb.getListString(TinyDB.FAVOURITES)){
        val jsonFormat = JSONObject(value)
        val name = jsonFormat.getString(propertyNameKey)
        if (name == propertyName){
            alreadyAdded = true
        }
    }
    return alreadyAdded
}

