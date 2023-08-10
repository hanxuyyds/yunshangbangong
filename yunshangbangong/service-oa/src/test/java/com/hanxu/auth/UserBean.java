package com.hanxu.auth;

import org.springframework.stereotype.Component;

@Component
public class UserBean {
    public String getUsername(int id){
        if(id==1){
            return "keqing";
        }else {
            return "lisi";
        }
    }
}
