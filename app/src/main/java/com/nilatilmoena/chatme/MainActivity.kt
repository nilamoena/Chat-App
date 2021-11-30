package com.nilatilmoena.chatme

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.nilatilmoena.chatme.adapters.ChatAdapter
import com.nilatilmoena.chatme.models.ChatModel
import com.nilatilmoena.chatme.network.ApiClient
import com.nilatilmoena.chatme.notification.Data
import com.nilatilmoena.chatme.notification.MyRequest
import com.nilatilmoena.chatme.notification.MyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var btn_send: ImageButton? = null
    private var typeMessage: EditText? = null
    private var pesan: String? = null

    private var topic = "mobcomp"
    var token: String? = null
    var messageAdapter: ChatAdapter? = null
    var recyclerView: RecyclerView? = null
    private var mDatabaseReference: DatabaseReference? = null
    var messageArrayList: ArrayList<ChatModel?> = ArrayList<ChatModel?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_send = findViewById(R.id.btn_send)
        typeMessage = findViewById(R.id.type_text)
        recyclerView = findViewById(R.id.recyclerview_chat)
        recyclerView?.run { setHasFixedSize(true) }
        val linearLayoutManager = LinearLayoutManager(this@MainActivity)
        linearLayoutManager.stackFromEnd = true
        with(recyclerView) {
            linearLayoutManager.stackFromEnd = true
            this?.setLayoutManager(linearLayoutManager)
        }

        mDatabaseReference =
            FirebaseDatabase.getInstance("https://chat-app-kotlin-2c9de-default-rtdb.firebaseio.com/").reference
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d(TAG, "Failed Get Token: ", task.exception)
                    return@OnCompleteListener
                }

                //Get new FCM Token
                token = task.result
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d(TAG, msg)
            })
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d(TAG, "Failed to subscribe " + topic + task.exception)
                    return@OnCompleteListener
                }
                val msg = "Now you can chat about $topic with your friends"
                Log.d(TAG, msg)
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            })
        (btn_send as ImageButton).setOnClickListener(View.OnClickListener {
            sendMessageToDatabase()
            sendNotification(pesan)
            with(typeMessage) { this?.setText("") }
        })
        readFromDatabase()
    }

    private fun sendMessageToDatabase() {
        val myRef = mDatabaseReference!!.child("Chats")
        val date = Date()
        val dateFormat = SimpleDateFormat("HH:mm")
        val date_format = dateFormat.format(date)
        pesan = typeMessage!!.text.toString().trim { it <= ' ' }
        val hashMap = HashMap<String, String?>()
        hashMap["sender"] = token
        hashMap["receiver"] = "user"
        hashMap["message"] = pesan
        hashMap["time_stamp"] = date_format
        myRef.push().setValue(hashMap)
    }

    private fun sendNotification(pesan: String?) {
        val data = Data("You have a new message.", "" + pesan)
        val myRequest = MyRequest("/topics/$topic", data)
        ApiClient.apiInterface().sendNotificationMessage(myRequest)
            ?.enqueue(object : Callback<MyResponse?> {
                override fun onResponse(call: Call<MyResponse?>, response: Response<MyResponse?>) {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "onResponse: Failed " + response.code())
                        Log.d(TAG, "onResponse: Message " + response.message())
                        return
                    }
                    Log.d(TAG, "onResponse: Success " + response.code())
                }

                override fun onFailure(call: Call<MyResponse?>, t: Throwable) {
                    Log.d(TAG, "onFailure: " + t.message)
                }
            })
    }

    private fun readFromDatabase() {
        val messageQuery = mDatabaseReference!!.child("Chats").orderByChild("time_stamp")
        messageQuery.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message: ChatModel? = snapshot.getValue(ChatModel::class.java)

                messageArrayList.add(message)
                val pesanArrayList: ArrayList<ChatModel?> = ArrayList<ChatModel?>()
                for (i in messageArrayList.indices) {
                    pesanArrayList.add(messageArrayList[i])
                }
                messageAdapter = token?.let { ChatAdapter(pesanArrayList, it) }
                recyclerView!!.adapter = messageAdapter

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}