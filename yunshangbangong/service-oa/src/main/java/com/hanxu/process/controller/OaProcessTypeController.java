package com.hanxu.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanxu.common.result.Result;
import com.hanxu.model.process.ProcessType;
import com.hanxu.process.service.OaProcessTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author hanxu
 * @since 2023-08-06
 */
@RestController
@RequestMapping(value = "/admin/process/processType")
public class OaProcessTypeController {
    @Autowired
    private OaProcessTypeService oaProcessTypeService;
    @ApiOperation(value = "获取分页数据")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,@PathVariable Long limit){
        Page<ProcessType> processTypePage = new Page<>(page,limit);
        IPage<ProcessType> pageModel = oaProcessTypeService.page(processTypePage);
        return Result.ok(pageModel);
    }
//    @PreAuthorize("hasAuthority('bnt.processType.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessType processType = oaProcessTypeService.getById(id);
        return Result.ok(processType);
    }

//    @PreAuthorize("hasAuthority('bnt.processType.add')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessType processType) {
        oaProcessTypeService.save(processType);
        return Result.ok();
    }

//    @PreAuthorize("hasAuthority('bnt.processType.update')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessType processType) {
        oaProcessTypeService.updateById(processType);
        return Result.ok();
    }

//    @PreAuthorize("hasAuthority('bnt.processType.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        oaProcessTypeService.removeById(id);
        return Result.ok();
    }
    @ApiOperation(value ="查询所有审批分类" )
    @GetMapping("findAll")
    public Result findAll(){
        List<ProcessType> list = oaProcessTypeService.list();
        return Result.ok(list);
    }
}

