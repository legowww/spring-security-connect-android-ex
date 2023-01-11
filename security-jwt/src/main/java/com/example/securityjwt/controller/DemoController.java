package com.example.securityjwt.controller;

import com.example.securityjwt.controller.request.UserJoinRequest;
import com.example.securityjwt.controller.request.UserLoginRequest;
import com.example.securityjwt.controller.response.Response;
import com.example.securityjwt.model.User;
import com.example.securityjwt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DemoController {

    private final UserService userService;

    @GetMapping("/")
    public String home(HttpServletRequest request) {
        Enumeration headers = request.getHeaderNames();

        while(headers.hasMoreElements()) {
            String headerName = (String)headers.nextElement();
            String value = request.getHeader(headerName);
            log.info("headerName={}: {}", headerName, value);
            System.out.println("headerName:"+headerName+","+value);
        }
        return "home";
    }

    @PostMapping("/join")
    public String join(@RequestBody UserJoinRequest request) {
        User joinedUser = userService.join(request.getUsername(), request.getPassword());
        return joinedUser.toString();
    }

//    @PostMapping("/login")
//    public Response<String> login(@RequestBody UserLoginRequest request) {
//        String token = userService.login(request.getUsername(), request.getPassword());
//        return Response.success(token);
//    }


    @GetMapping("/my")
    public Response<User> my(Authentication authentication) {
        log.info("==접속==");
        User authenticationUser = (User) authentication.getPrincipal();
        return Response.success(authenticationUser);
    }
}
