package ng.hotels.booking.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import ng.hotels.booking.app.R

class AvailableRoomsAdapter(var ctx: Context, hotelRoomList: JsonArray?) : RecyclerView.Adapter<AvailableRoomsAdapter.AvailableRoomsHolder>(){

    var list = hotelRoomList

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AvailableRoomsHolder {
        return AvailableRoomsHolder(LayoutInflater.from(p0.context).inflate(R.layout.room_category_item, p0, false))
    }

    override fun getItemCount(): Int {
        return list!!.size()
    }

    override fun onBindViewHolder(p0: AvailableRoomsHolder, p1: Int) {
        val item = list!![p1].asJsonObject
        p0.roomName.text = item.get("name").asString
    }


    class AvailableRoomsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val roomName = itemView.findViewById<TextView>(R.id.room_category)
        val roomPrice = itemView.findViewById<TextView>(R.id.room_price)
        val addRoom = itemView.findViewById<Button>(R.id.add_button)
        val removeRoom = itemView.findViewById<Button>(R.id.subtract_button)
        val selectRoom = itemView.findViewById<Button>(R.id.select_room_button)
        val selectionMade = itemView.findViewById<TextView>(R.id.selection)
        val modify = itemView.findViewById<TextView>(R.id.select_room)


    }
}