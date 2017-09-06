package com.shiro.controller;

import com.shiro.model.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhenghuasheng on 2017/9/5.19:17
 */
@Controller
@RequestMapping("/user")
public class UserController {

    //用户登录
    @RequestMapping("/login")
    public String login(User user, HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        try{
            subject.login(token);//会跳到我们自定义的realm中
            request.getSession().setAttribute("user", user);
            return "success";
        }catch(Exception e){
            e.printStackTrace();
            request.getSession().setAttribute("user", user);
            request.setAttribute("error", "用户名或密码错误！");
            return "login";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "index";
    }

    @RequestMapping("/admin")
    public String admin(HttpServletRequest request) {
        return "success";
    }

    @RequestMapping("/student")
    public String student(HttpServletRequest request) {
        return "success";
    }

    @RequestMapping("/teacher")
    public String teacher(HttpServletRequest request) {
        return "success";
    }
}
