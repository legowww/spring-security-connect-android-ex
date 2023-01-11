# spring-security-connect-android-ex
스프링 시큐리티 환경에서 안드로이드와 REST API 실습

# 기본 세팅

### Android
[AndroidManifest.xml 환경 세팅](https://github.com/legowww/spring-security-connect-android-ex/blob/d17485f3dd72cfa87929db3a51db8cdabd217ce8/MyAndroid_code/app/src/main/AndroidManifest.xml)

1. `retrofit2`을 사용하기 위해 안드로이드에서 인터넷 접속할 수 있도록 허용
```
    <uses-permission android:name="android.permission.INTERNET" />
```
2. JWT 토큰 저장을 위해 사용되는 `SharedPreferences`를 유틸로 사용하기 위해 추가
[REFERENCE](https://velog.io/@jinstonlee/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C%EC%97%90%EC%84%9C-JWT-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)
```
    <application
        android:name="com.example.util.prefs.App"
        ...
    </application>
```

### Springboot
`SecurityConfig.java`이 아닌 `MySecurityConfig.java`를 사용하여 안드로이드와 통신함
