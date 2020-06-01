package ng.hotels.booking.app.models

import com.google.gson.annotations.SerializedName

class Token {

    @SerializedName("access_token")
    var token: String? = null
    @SerializedName("token_type")
    var tokenType: String? = null
    @SerializedName("Bearer")
    var expireTime: Int = 0


}