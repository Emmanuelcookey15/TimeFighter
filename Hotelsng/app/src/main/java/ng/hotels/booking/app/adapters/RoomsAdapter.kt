package ng.hotels.booking.app.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.JsonArray
import ng.hotels.booking.app.R
import ng.hotels.booking.app.utils.TinyDB
import ng.hotels.booking.app.utils.inflate
import java.lang.StringBuilder
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class RoomsAdapter(val ctx: Context?,
                    roomslist: JsonArray?, var txt: TextView): RecyclerView.Adapter<RoomsAdapter.RoomHolder>(){

    var list = roomslist

    var tinyDB = TinyDB(ctx!!)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RoomHolder {
       return  RoomHolder(p0.inflate(R.layout.rooms_items))
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size()
    }

    override fun onBindViewHolder(p0: RoomHolder, p1: Int) {
        val item = list!!.get(p1).asJsonObject

        var facility = StringBuilder()

        var roomInfro = ArrayList<String>()

        var totalRoomData = ArrayList<String>()

        var count = 0

        var price = item.get("availability").asJsonObject.get("avg_rate")

        var roomSelected = item.get("name").asString

        p0.roomName.text = roomSelected
        p0.roomPrice.text = priceWithoutDecimal(price.asDouble)


        if (item.get("images").asJsonArray.size() > 0){
            Glide.with(p0.roomImage.context)
                    .load(item.get("images").asJsonArray.get(0).asJsonObject.get("url").asString)
                    .placeholder(R.drawable.default_bed)
                    .into(p0.roomImage)
        }else{
            p0.roomImage.setImageResource(R.drawable.default_bed)
        }

        var flags = item.get("availability").asJsonObject.get("flags").asJsonArray

//        for (i in flags) {
//
//            setFacilities(i.asJsonObject.get("flag_name").asString.toLowerCase(), facility)
//        }
//
//        p0.roomFlags.text = facility

        p0.btnSelectRoom.setOnClickListener {

            p0.roomBookLinearLayout.visibility = View.VISIBLE
            p0.btnSelectRoom.visibility = View.GONE

            count++

            p0.roomCount.text = count.toString()

            tinyDB.putInt(TinyDB.ROOMPRICE, tinyDB.getInt(TinyDB.ROOMPRICE) + price.asInt )


            txt.text = priceWithoutDecimal(tinyDB.getInt(TinyDB.ROOMPRICE).toDouble())

            roomInfro = tinyDB.getListString(TinyDB.ROOMINFO)
            roomInfro.add(roomSelected)
            tinyDB.putListString(TinyDB.ROOMINFO, roomInfro)

            totalRoomData = tinyDB.getListString(TinyDB.TOTALROOMINFO)
            totalRoomData.add(item.toString())
            tinyDB.putListString(TinyDB.TOTALROOMINFO, totalRoomData)

        }


        p0.btnAdd.setOnClickListener {
            count++

            p0.roomCount.text = count.toString()

            tinyDB.putInt(TinyDB.ROOMPRICE, tinyDB.getInt(TinyDB.ROOMPRICE) + price.asInt)

            txt.text = priceWithoutDecimal(tinyDB.getInt(TinyDB.ROOMPRICE).toDouble())


            roomInfro = tinyDB.getListString(TinyDB.ROOMINFO)
            roomInfro.add(roomSelected)
            tinyDB.putListString(TinyDB.ROOMINFO, roomInfro)


            totalRoomData = tinyDB.getListString(TinyDB.TOTALROOMINFO)
            totalRoomData.add(item.toString())
            tinyDB.putListString(TinyDB.TOTALROOMINFO, totalRoomData)

        }

        p0.btnSubtract.setOnClickListener {
            if(count < 2){
                p0.roomBookLinearLayout.visibility = View.GONE
                p0.btnSelectRoom.visibility = View.VISIBLE
                count--
                p0.roomCount.text = count.toString()
            }else{
                count--
                p0.roomCount.text = count.toString()
            }

            tinyDB.putInt(TinyDB.ROOMPRICE, tinyDB.getInt(TinyDB.ROOMPRICE) - price.asInt)

            txt.text = priceWithoutDecimal(tinyDB.getInt(TinyDB.ROOMPRICE).toDouble())


            roomInfro = tinyDB.getListString(TinyDB.ROOMINFO)
            if (roomInfro.size > 0){
                roomInfro.removeAt(roomInfro.lastIndexOf(roomSelected))
                tinyDB.putListString(TinyDB.ROOMINFO, roomInfro)
            }

            if (totalRoomData.size > 0){
                totalRoomData.removeAt(totalRoomData.lastIndexOf(item.toString()))
                tinyDB.putListString(TinyDB.TOTALROOMINFO, totalRoomData)
            }

        }

    }


    private fun priceWithoutDecimal(number: Double): String {
        val number3digits:Double = Math.round(number * 1000.0) / 1000.0
        val number2digits:Double = Math.round(number3digits * 100.0) / 100.0
        val solution:Double = Math.round(number2digits * 10.0) / 10.0
        val solu = NumberFormat.getNumberInstance(Locale.US).format(solution)
        val result = solu.toString()
        return result
    }


    interface OnItemClickListener {
        fun onItemClick(v: View, pos: Int)
    }



    class RoomHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val roomImage = itemView.findViewById<ImageView>(R.id.room_image)
        val roomName = itemView.findViewById<TextView>(R.id.room_name)
        val roomPrice = itemView.findViewById<TextView>(R.id.room_current_price)
        val roomCount = itemView.findViewById<TextView>(R.id.room_count_txt)
        val roomFlags = itemView.findViewById<TextView>(R.id.room_flags)
        val btnSelectRoom = itemView.findViewById<Button>(R.id.btn_select_room)
        val btnAdd = itemView.findViewById<Button>(R.id.add_button_room)
        val btnSubtract = itemView.findViewById<Button>(R.id.subtract_button_room)

        val roomBookLinearLayout = itemView.findViewById<LinearLayout>(R.id.book_layout_room)
    }



    private fun setFacilities(facilities: String, facility: StringBuilder) {
        if (facilities.contains("internet") || facilities.contains("wifi")) {
            facility!!.append("WiFi\n")

        }

        if (facilities.contains("bar/lounge") || facilities.contains("bar")) {
            facility!!.append("Bar\n")

        }

        if (facilities.contains("gym") || facilities.contains("fitness")) {
            facility!!.append("Gym\n")
        }

        if (facilities.contains("restaurant")) {
            facility!!.append("Restaurant\n")
        }

        if (facilities.contains("swimming pool") || facilities.contains("pool")) {
            facility!!.append("Pool\n")
        }

        if (facilities.contains("24 hours electricity") || facilities.contains("electricity") ||
                facilities.contains("power")) {
            facility!!.append("Electricity\n")
        }

        if (facilities.contains("security")){
            facility!!.append("Security\n")
        }

        if (facilities.contains("parking")){
            facility!!.append("Parking\n")
        }

        if (facilities.contains("air conditioning") || facilities.contains("a/c")){
            facility!!.append("Air Conditioning\n")
        }

        if (facilities.contains("laundry")){
            facility!!.append("Laundry\n")
        }

        if (facilities.contains("room service") || facilities.contains("service")){
            facility!!.append("Room Service\n")
        }

        if (facilities.contains("refrigerator")){
            facility!!.append("Refrigerator\n")
        }

        if (facilities.contains("flatscreen tv") || facilities.contains("dstv") || facilities.contains("television")){
            facility!!.append("Television\n")
        }

        if (facilities.contains("telephone")){
            facility!!.append("Telephone\n")
        }
    }




}