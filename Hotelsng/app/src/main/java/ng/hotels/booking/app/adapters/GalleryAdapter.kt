package ng.hotels.booking.app.adapters

import android.app.Dialog
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Build
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ng.hotels.booking.app.R
import org.json.JSONArray
import org.json.JSONObject

class GalleryAdapter(var ctx: Context, var list: JSONArray?, var value: JSONObject, var dialog: Dialog, var mRelativeLayout: RelativeLayout) : RecyclerView.Adapter<GalleryAdapter.GalleryHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): GalleryHolder {
        return GalleryHolder(LayoutInflater.from(p0.context).inflate(R.layout.gallery_item, p0, false))
    }

    override fun getItemCount(): Int {
        return list!!.length()
    }

    override fun onBindViewHolder(p0: GalleryHolder, p1: Int) {
        val item = list!!.getJSONObject(p1).get("url").toString() + "?w=360"

//        p0.amenitiesImage.setImageResource(values[p1])

        if (value.get("num_of_images").toString().toInt() > 0){
            Glide.with(p0.amenitiesImage.context)
                    .load(item)
                    .placeholder(R.drawable.default_bed)
                    .into(p0.amenitiesImage)
        }else{
            p0.amenitiesImage.setImageResource(R.drawable.default_bed)
        }

        p0.amenitiesImage.setOnClickListener {
            showPopup(item)

        }
    }


    private fun showPopup(varg: String) {







//        val window = dialog.window
//        val wlp = window?.attributes
//        wlp?.gravity = Gravity.TOP
//
//        wlp?.y = 100
//        wlp?.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
//        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
//        wlp?.height = 700
//        window?.attributes = wlp
//        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//        window
//
//        dialog.show()


        val inflater =  ctx.getSystemService(LAYOUT_INFLATER_SERVICE) as (LayoutInflater)

        // Inflate the custom layout/view
        val customView = inflater.inflate(R.layout.full_gallery_image,null)

        var imagery = customView.findViewById<ImageView>(R.id.full_image)

        val mPopupWindow = PopupWindow(
                customView,
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
        )

        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.elevation = 5.0f
        }

        if (value.get("num_of_images").toString().toInt() > 0){
            Glide.with(ctx)
                    .load(varg)
                    .placeholder(R.drawable.default_bed)
                    .into(imagery)
        }else{
            imagery.setImageResource(R.drawable.default_bed)
        }

        val closeButton = customView.findViewById<RelativeLayout>(R.id.ib_close)


        closeButton.setOnClickListener {
            mPopupWindow.dismiss()
        }

        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);

    }


    class GalleryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val amenitiesImage = itemView.findViewById<ImageView>(R.id.gallery_image)



    }
}