package com.hanxu.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanxu.auth.service.SysUserService;
import com.hanxu.common.result.Result;
import com.hanxu.model.process.Process;
import com.hanxu.model.process.ProcessTemplate;
import com.hanxu.process.service.OaProcessService;
import com.hanxu.process.service.OaProcessTemplateService;
import com.hanxu.vo.process.ApprovalVo;
import com.hanxu.vo.process.ProcessQueryVo;
import com.hanxu.vo.process.ProcessVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author hanxu
 * @since 2023-08-07
 */
@RestController
@RequestMapping("/admin/process")
@CrossOrigin
public class OaProcessController {
    @Autowired
    private OaProcessService oaProcessService;
    @Autowired
    private SysUserService sysUserService;


//    @PreAuthorize("hasAuthority('bnt.process.list')")

    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page, @PathVariable Long limit, ProcessQueryVo processQueryVo){
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = oaProcessService.selectPage(pageParam, processQueryVo);
        return Result.ok(pageModel);
    }
    @ApiOperation(value = "已处理")
    @GetMapping("/findProcessed/{page}/{limit}")
    public Result findProcessed(@PathVariable Long page, @PathVariable Long limit){
        Page<Process> pageParam = new Page<>(page,limit);
        IPage<ProcessVo> pageModel=oaProcessService.findProcessed(pageParam);
        return Result.ok(pageModel);
    }
    @ApiOperation(value = "待处理")
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPending(@PathVariable Long page, @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        Page<ProcessVo> pageModel=oaProcessService.findPending(pageParam);
        return Result.ok(pageModel);
    }
    @ApiOperation(value = "已发起")
    @GetMapping("/findStarted/{page}/{limit}")
    public Result findStarted(@PathVariable Long page, @PathVariable Long limit) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        return Result.ok(oaProcessService.findStarted(pageParam));
    }
    @ApiOperation(value = "查看审批详情")
    @GetMapping("show/{id}")
    public Result show(@PathVariable Long id){
        Map<String,Object> map= oaProcessService.show(id);
        return Result.ok(map);
    }
    @ApiOperation(value = "审批")
    @PostMapping("approve")
    public Result approve(@RequestBody ApprovalVo approvalVo){
        oaProcessService.approve(approvalVo);
        return Result.ok();
    }
    @ApiOperation(value = "获取当前的用户信息")
    @GetMapping("getCurrentUser")
    private Result getCurrentUser(){
        Map<String,Object> map=sysUserService.getCurrentUser();
        return Result.ok(map);
    }
}

