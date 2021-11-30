package com.nilatilmoena.chatme.network

import com.nilatilmoena.chatme.notification.MyRequest
import com.nilatilmoena.chatme.notification.MyResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    @POST("send")
    @Headers(
        "Content-Type: application/json",
        "Authorization: key=AAAAncmNjio:APA91bFjOLQeiKZqpDsduMglP9Rob7UpWG9Ao1yD4rY-OhHBUQS1v8m9jgN_CNRYBky5F6615yYw7nC2QmZbR5mBNZiTN_SqK4d3ux1O2vgMZvWUIkxdsOIsjamJ7x_H9nM9iETUgzFc"
    )
    fun sendNotificationMessage(@Body myRequest: MyRequest?): Call<MyResponse?>?
}