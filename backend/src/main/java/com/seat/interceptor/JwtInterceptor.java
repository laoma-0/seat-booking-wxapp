package com.seat.interceptor;
// 拦截器
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.seat.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component 
public class JwtInterceptor implements HandlerInterceptor{

    @Override 
    public boolean preHandle(HttpServletRequest request, 
        HttpServletResponse response, Object handler) {
       
         String token = request.getHeader("Authorization");
           if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
          }
          token = token.substring(7);
          try{
            String openid = JwtUtil.getSubject(token);
            request.setAttribute("openid", openid);
             return true;
          } catch (Exception e) {
            response.setStatus(401);
            return false;    
        }
    }
    
}


