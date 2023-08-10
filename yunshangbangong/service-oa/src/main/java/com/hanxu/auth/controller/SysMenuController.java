package com.hanxu.auth.controller;


import com.hanxu.auth.service.SysMenuService;
import com.hanxu.common.result.Result;
import com.hanxu.model.system.SysMenu;
import com.hanxu.vo.system.AssginMenuVo;
import com.hanxu.vo.system.AssginRoleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author hanxu
 * @since 2023-07-10
 */
@Api(tags = "菜单管理接口")
@RestController
@RequestMapping("/admin/system/sysMenu")
public class SysMenuController {
    @Autowired
    private SysMenuService service;
    @ApiOperation(value = "获取菜单")
    @GetMapping("findNodes")
    public Result findNodes() {
        List<SysMenu> list = service.findNodes();
        return Result.ok(list);
    }

    @ApiOperation(value = "新增菜单")
    @PostMapping("save")
    public Result save(@RequestBody SysMenu permission) {
        service.save(permission);
        return Result.ok();
    }

    @ApiOperation(value = "修改菜单")
    @PutMapping("update")
    public Result updateById(@RequestBody SysMenu permission) {
        service.updateById(permission);
        return Result.ok();
    }

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        service.removeById(id);
        return Result.ok();
    }
    @ApiOperation("查询所有菜单和角色分配的菜单")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId){
        List<SysMenu> list=service.findMenuByRoleId(roleId);
        return Result.ok(list);
    }
    @ApiOperation("角色分配菜单")
    @PostMapping ("doAssign")
    public Result doAssign(@RequestBody AssginMenuVo assginMenuVo){
        service.doAssign(assginMenuVo);
        return Result.ok();
    }
}

