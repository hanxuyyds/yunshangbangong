package com.hanxu.auth;

import com.hanxu.model.system.SysRole;
import com.hanxu.auth.service.SysRoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Test02 {
    @Autowired
    private SysRoleService service;
    @Test
    public void test01(){
        List<SysRole> list = service.list();
        System.out.println(list);
    }
}
