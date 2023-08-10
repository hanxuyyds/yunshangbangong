package com.hanxu.auth.service.impl;

import com.hanxu.auth.service.SysMenuService;
import com.hanxu.auth.service.SysUserService;
import com.hanxu.model.system.SysUser;
import com.hanxu.security.custom.CustomUser;
import com.hanxu.security.custom.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService service;
    @Autowired
    private SysMenuService sysMenuService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user =service.getUserByUserName(username);
        if(null == user) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        if(user.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用");
        }
        //根据用户id查询用户操作权限的数据
        List<String> perms = sysMenuService.findUserPermsByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities=new ArrayList<>();
        perms.stream().forEach(perm->{authorities.add(new SimpleGrantedAuthority(perm.trim()));});
        return new CustomUser(user, authorities);
    }
}
