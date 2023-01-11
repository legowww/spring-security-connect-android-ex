package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.dto.User
import com.example.dto.response.ServerResponse
import com.example.util.prefs.App
import com.example.util.retrofit.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info_with_security)
        var textView = findViewById<TextView>(R.id.myInfoText)

        //todo 실제로 /my 접속하여 응답값 받아오기기
        val myCall = RetrofitBuilder.api.getMyInfo(App.prefs.token)
        myCall.enqueue(object : Callback<ServerResponse<User>> {
            override fun onResponse(call: Call<ServerResponse<User>>,
                                    response: Response<ServerResponse<User>>
            ) {
                val body = response.body() ?: return
                val message = body.message
                textView.append("message=$message\n\n")

                if (body.result != null) {
                    val user : User = body.result
                    textView.append("id=${user.id} username=${user.username} password=${user.password}")
                }
            }

            override fun onFailure(call: Call<ServerResponse<User>>, t: Throwable) {
                textView.append("failure=${t.message}")
            }
        })
    }
}