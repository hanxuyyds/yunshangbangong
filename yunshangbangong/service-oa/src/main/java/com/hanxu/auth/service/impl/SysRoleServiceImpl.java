package com.hanxu.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxu.auth.mapper.SysRoleMapper;
import com.hanxu.auth.service.SysUserRoleService;
import com.hanxu.model.system.SysRole;
import com.hanxu.auth.service.SysRoleService;
import com.hanxu.model.system.SysUserRole;
import com.hanxu.vo.system.AssginRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysUserRoleService service;
    @Override
    public Map<String, Object> findRoleDataByUserId(Long userId) {
        //查询所有角色，返回list集合
        List<SysRole> roles = baseMapper.selectList(null);
        //根据userId查询角色用户表，查询userId对应的所有角色id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,userId);
        List<SysUserRole> existUserRoleList = service.list(wrapper);
        //从查询出来的用户id对应角色list集合，获取所有用户的id
        List<Long> ids = new ArrayList<>();
        for (SysUserRole sysUserRole : existUserRoleList) {
            ids.add(sysUserRole.getRoleId());
        }
        //根据角色id到所有的角色的list集合进行比较
        List<SysRole> assignRoleList = new ArrayList<>();
        for (SysRole role : roles) {
            if(ids.contains(role.getId())){
                assignRoleList.add(role);
            }
        }
        System.out.println(assignRoleList);
        //把得到的两部分数据封装到map集合
        HashMap<String, Object> roleMap = new HashMap<>();
        roleMap.put("assignRoleList",assignRoleList);
        roleMap.put("allRolesList",roles);
        return roleMap;
    }

    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        //把用户之前分配的用户数据删除，用户角色关系表里面，根据userId删除
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,assginRoleVo.getUserId());
        service.remove(wrapper);
        //重新分配
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        for (Long roleId : roleIdList) {
            if(StringUtils.isEmpty(roleId)){
                continue;
            }
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assginRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            service.save(sysUserRole);
        }
    }
}
