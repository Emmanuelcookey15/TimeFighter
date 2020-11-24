package ng.jolag.app.utils

import android.widget.Button



fun Button.loadState(state:Int, message: String){
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
