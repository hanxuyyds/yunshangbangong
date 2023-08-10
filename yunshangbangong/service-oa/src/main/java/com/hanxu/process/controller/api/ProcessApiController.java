package com.hanxu.process.controller.api;

import com.hanxu.common.result.Result;
import com.hanxu.model.process.ProcessTemplate;
import com.hanxu.process.service.OaProcessService;
import com.hanxu.process.service.OaProcessTemplateService;
import com.hanxu.process.service.OaProcessTypeService;
import com.hanxu.vo.process.ProcessFormVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/admin/process")
@CrossOrigin  //跨域
public class ProcessApiController {
    @Autowired
    private OaProcessService oaProcessService;
    @Autowired
    private OaProcessTypeService oaProcessTypeService;
    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;
    @ApiOperation(value = "获取全部审批分类及模板")
    @GetMapping("findProcessType")
    public Result findProcessType(){
        return Result.ok(oaProcessTypeService.findProcessType());
    }
    @ApiOperation(value = "获取审批模板的数据")
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result getProcessTemplate(@PathVariable Long processTemplateId){
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(processTemplateId);
        return Result.ok(processTemplate);
    }
    @ApiOperation(value = "启动流程")
    @PostMapping("/startUp")
    public Result start(@RequestBody ProcessFormVo processFormVo){
        oaProcessService.startUp(processFormVo);
        return Result.ok();
    }
}
