package com.hanxu.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanxu.auth.mapper.SysRoleMapper;
import com.hanxu.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class Test01 {
    @Autowired
    private SysRoleMapper mapper;
    //查询所有记录
    @Test
    public void getAll(){
        List<SysRole> sysRoles = mapper.selectList(null);
        System.out.println(sysRoles);

    }
    @Test
    public void add(){
        SysRole sysRole = new SysRole();
        sysRole.setRoleName("角色管理员");
        sysRole.setRoleCode("101");
        sysRole.setDescription("人");
        System.out.println(sysRole.getId());
        System.out.println(sysRole);
        int insert = mapper.insert(sysRole);
        System.out.println(sysRole.getId());
        System.out.println(insert);
    }
    @Test
    public void update(){
        SysRole sysRole = mapper.selectById(10);
        sysRole.setRoleName("张三");
        int update = mapper.updateById(sysRole);
        System.out.println(update);
    }
    @Test
    public void deleteById(){
        int i = mapper.deleteById(10);
        System.out.println(i);
    }
    @Test
    public void deleteByBatchIds(){
        int i = mapper.deleteBatchIds(Arrays.asList(1,2));
        System.out.println(i);
    }
    @Test
    public void testQuery1(){
        QueryWrapper<SysRole> sysRoleQueryWrapper = new QueryWrapper<>();
        sysRoleQueryWrapper.eq("role_name", "张三");
        List<SysRole> sysRoles = mapper.selectList(sysRoleQueryWrapper);
        System.out.println(sysRoles);
    }
    @Test
    public void testQuery2(){
        LambdaQueryWrapper<SysRole> sysRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysRoleLambdaQueryWrapper.eq(SysRole::getRoleName,"总经理");
        List<SysRole> sysRoles = mapper.selectList(sysRoleLambdaQueryWrapper);
        System.out.println(sysRoles);
    }
}
