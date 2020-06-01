package ng.hotels.booking.app.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import ng.hotels.booking.app.R
import org.json.JSONArray

class CustomPagerAdapter(var ctx: Context, var mResources: JSONArray, var imageNumber:Int) : PagerAdapter() {

     val mLayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun getCount(): Int {
        return mResources.length()
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = mLayoutInflater.inflate(R.layout.image_pager_item, container, false)
        val imageView: ImageView = itemView.findViewById(R.id.imageView_pager)

        val circularProgressDrawable = CircularProgressDrawable(ctx)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        if (imageNumber > 0) {
            Glide.with(ctx)
                    .load(mResources.getJSONObject(position).get("url"))
                    .placeholder(R.drawable.default_bed)
                    .into(imageView)
        }else{
            imageView.setImageResource(R.drawable.top)
        }
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout?)
    }

}