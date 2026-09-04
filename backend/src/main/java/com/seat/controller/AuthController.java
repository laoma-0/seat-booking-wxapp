package com.seat.controller;

import com.seat.entity.User;
import com.seat.mapper.UserMapper;
import com.seat.service.WxService;
import com.seat.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController 
@RequestMapping ("/api/auth")
public class AuthController {
   @Autowired private WxService wxService;
   @Autowired private UserMapper userMapper;

   @PostMapping ("/login")
    public String login(@RequestBody LoginReq req) {
        String openid = wxService.code2Session(req.getCode());
        User u = userMapper.selectByOpenId(openid);
        if (u == null) {
            u = new User();
            u.setOpenId(openid);
            userMapper.insert(u);
        }
        return JwtUtil.generate(openid);
    }

}
class LoginReq {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}