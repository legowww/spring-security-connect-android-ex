package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText


class LoginFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)

        findViewById<Button>(R.id.button).setOnClickListener {
            val subIntent = Intent(this@LoginFormActivity, LoginResultActivity::class.java)
            subIntent.putExtra("username", username.text.toString())
            subIntent.putExtra("password", password.text.toString())
            startActivity(subIntent)
        }
    }
}


