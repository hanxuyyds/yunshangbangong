package com.hanxu.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanxu.common.result.Result;
import com.hanxu.model.process.ProcessTemplate;
import com.hanxu.process.service.OaProcessTemplateService;
import com.hanxu.process.service.OaProcessTypeService;
import io.netty.util.internal.ResourcesUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author hanxu
 * @since 2023-08-06
 */
@RestController
@RequestMapping(value = "/admin/process/processTemplate")
public class OaProcessTemplateController {
    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;
    @ApiOperation("获取分页审批模板数据")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,@PathVariable Long limit){
        Page<ProcessTemplate> processTemplatePage = new Page<>(page,limit);
        IPage<ProcessTemplate> pageModel=oaProcessTemplateService.selectPageProcessTemplate(processTemplatePage);
        return Result.ok(pageModel);
    }
    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate) {
        oaProcessTemplateService.save(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessTemplate processTemplate) {
        oaProcessTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        oaProcessTemplateService.removeById(id);
        return Result.ok();
    }
    @PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "上传流程定义")
    @PostMapping("/uploadProcessDefinition")
    public Result uploadProcessDefinition(MultipartFile file) throws FileNotFoundException {
        //获取classes目录的位置
        String path = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath();
        //设置上传文件夹
        File tempFile= new File(path + "/processes/");
        if(!tempFile.exists()){
            tempFile.mkdirs();
        }
        //创建空文件，实现文件写入
        String filename = file.getOriginalFilename();
        File zipFile = new File(path + "/processes/" + filename);
        try {
            file.transferTo(zipFile);
        } catch (IOException e) {
            return Result.fail();
        }
        Map<String, Object> map = new HashMap<>();
        //根据上传地址后续部署流程定义，文件名称为流程定义的默认key
        map.put("processDefinitionPath", "processes/" + filename);
        map.put("processDefinitionKey", filename.substring(0, filename.lastIndexOf(".")));
        return Result.ok(map);
    }
    @ApiOperation(value = "发布")
    @GetMapping("/publish/{id}")
    public Result publish(@PathVariable Long id){
        oaProcessTemplateService.publish(id);
        return Result.ok();
    }

}

