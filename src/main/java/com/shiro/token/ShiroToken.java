package com.shiro.token;

import org.apache.shiro.authc.UsernamePasswordToken;

import java.io.Serializable;

/**
 * Created by zhenghuasheng on 2017/9/7.11:34
 */
public class ShiroToken extends UsernamePasswordToken implements Serializable {
    /** 登录密码[字符串类型] 因为父类是char[] ] **/
    private String pswd;

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public ShiroToken(String username,String pswd) {
        super(username, pswd);
        this.pswd = pswd;
    }
}
