package ng.hotels.booking.app.models

import java.util.HashMap

class Bookings() {

    var country: String? = null
    var currency_code: String? = null
    var title: String? = null
    var fname: String? = null
    var lname: String? = null
    var email: String? = null
    var phone: String? = null
    var address: String? = null
    var checkin: String? = null
    var checkout: String? = null
    var ptype: String? = null
    var property: String? = null
    var additional_info: String? = null
    var payment_type: String? = null
    var agent: String? = null
    var provider: String? = null
    var coupon_code: String? = null
    var access_token: String? = null
    var rooms: ArrayList<HashMap<String, String>>? = null
    var on_behalf_of_fname: String? = null
    var on_behalf_of_lname: String? = null
    var on_behalf_of_email: String? = null
    var on_behalf_of_phone: String? = null

}