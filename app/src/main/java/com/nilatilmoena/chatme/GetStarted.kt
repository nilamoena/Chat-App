package com.nilatilmoena.chatme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class GetStarted : AppCompatActivity(), View.OnClickListener {
    private lateinit var btnStart : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        btnStart = findViewById(R.id.btn_start)
        btnStart.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_start -> {
                val buttonStart = Intent(this@GetStarted, MainActivity::class.java)
                startActivity(buttonStart)
            }
        }
    }
}
