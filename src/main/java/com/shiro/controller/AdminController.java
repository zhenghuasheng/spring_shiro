package com.shiro.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zhenghuasheng on 2017/9/7.16:31
 */
@Controller
public class AdminController {


    @RequestMapping("/find/user")
    @ResponseBody
    public Object getUser() {
        return SecurityUtils.getSubject().getPrincipal();
    }
}
