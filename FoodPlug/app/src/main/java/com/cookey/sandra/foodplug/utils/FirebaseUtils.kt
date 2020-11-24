package com.cookey.sandra.foodplug.utils

import com.cookey.sandra.foodplug.data.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FirebaseUtils {


    companion object {


        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseCurrentUser = firebaseAuth.currentUser
        val userUID = firebaseCurrentUser?.uid
        val firebaseReference = FirebaseDatabase.getInstance().reference

        fun getFirebaseFoodDays(foodDay: String, foodItemList: ArrayList<FoodItem>): ArrayList<FoodItem> {

            var foodName: String
            var foodDesc: String
            var foodPrice: String
            var foodImg: String

            FirebaseDatabase.getInstance()
                .reference
                .child(FirebaseConstants.VENDOR)
                .child(FirebaseConstants.FOOD_VENDOR)
                .child(FirebaseConstants.FOOD_DAY)
                .child(foodDay).child("1").addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        foodItemList.clear()
                        p0.children.forEach { eachUserPost ->

                            eachUserPost.children.forEachIndexed { index, it ->

                                foodName = it.child(FirebaseConstants.FOOD_POST)
                                    .child(FirebaseConstants.FOOD_NAME).value.toString()
                                foodDesc = it.child(FirebaseConstants.FOOD_POST)
                                    .child(FirebaseConstants.FOOD_DESC).value.toString()

                                foodPrice = it.child(FirebaseConstants.FOOD_POST)
                                    .child(FirebaseConstants.FOOD_PRICE).value.toString()
                                foodImg = it.child(FirebaseConstants.FOOD_POST)
                                    .child(FirebaseConstants.FOOD_IMG).value.toString()


                                if (foodName != "null") {
                                    foodItemList.add(
                                        FoodItem(

                                            foodName = foodName,
                                            foodPrice = foodPrice,
                                            foodImg = foodImg,
                                            foodDesc = foodDesc
                                        )
                                    )

                                }
                            }


                        }
                    }

                })

            return foodItemList
            }
    }

}