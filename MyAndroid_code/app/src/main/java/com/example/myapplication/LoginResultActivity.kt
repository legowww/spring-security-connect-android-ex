package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.dto.request.LoginRequest
import com.example.dto.response.ServerResponse
import com.example.util.prefs.App
import com.example.util.retrofit.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        val textView = findViewById<TextView>(R.id.sub_text)
        var latestToken = App.prefs.token //앱을 꺼도 남아있는 정보, 마지막에 사용된 Bearer 토큰이 저장되어 있다.
        textView.append("저장되어 있는 최신 토큰값: $latestToken\n\n")

        if (intent == null ) return
        val extras = intent.extras ?: return
        val username = extras.getString("username")
        val password = extras.getString("password")

        //로그인하여 토큰 받아오기
        val loginRequest = LoginRequest(username, password)
        val loginCall = RetrofitBuilder.api.login(loginRequest)
        loginCall.enqueue(object : Callback<ServerResponse<String>> {
            override fun onResponse(call: Call<ServerResponse<String>>,
                                    response: Response<ServerResponse<String>>) {
                //설마 status 보고 body() 판단? -> 맞음
                //서버에서 status 를 정상 반환 200 HttpStatus.OK 를 내려줘야 한다.
                // https://week-year.tistory.com/181
                val body = response.body() ?: return

                val message = body.message
                val token = body.result

                if (token == null) {
                    textView.append("message=$message\n")
                    App.prefs.token = "default" // token 초기화
                    return
                }

                latestToken = "Bearer $token"
                App.prefs.token = latestToken //토큰 최신화

                textView.append("로그인 성공 후 최신화된 토큰값: ${App.prefs.token}\n")
                textView.append("로그인 성공\n\n")
            }
            override fun onFailure(call: Call<ServerResponse<String>>, t: Throwable) {
                textView.append("onFailire")
            }
        })

        findViewById<Button>(R.id.myInfoButton).setOnClickListener {
            val subIntent = Intent(this, MyInfoActivity::class.java)
            startActivity(subIntent)
        }
    }
}



