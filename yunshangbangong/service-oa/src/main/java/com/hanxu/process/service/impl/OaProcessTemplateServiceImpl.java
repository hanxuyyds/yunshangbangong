package com.hanxu.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanxu.model.process.ProcessTemplate;

import com.hanxu.model.process.ProcessType;
import com.hanxu.process.mapper.OaProcessTemplateMapper;
import com.hanxu.process.service.OaProcessService;
import com.hanxu.process.service.OaProcessTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxu.process.service.OaProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author hanxu
 * @since 2023-08-06
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {
    @Autowired
    private OaProcessTypeService oaProcessTypeService;
    @Autowired
    private OaProcessService oaProcessService;
    @Override
    public IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> processTemplatePage) {
        //调用mapper的方法实现分页查询
        IPage<ProcessTemplate> page = baseMapper.selectPage(processTemplatePage,null);
        //分页查询返回分页数据，从分页数据获取列表list集合
        List<ProcessTemplate> processTemplateList = page.getRecords();
        //遍历list集合，得到每个对象的审批类型id
        for (ProcessTemplate processTemplate : processTemplateList) {
            Long processTypeId = processTemplate.getProcessTypeId();
            //根据审批类型id,查询获取对应名称
            LambdaQueryWrapper<ProcessType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessType::getId,processTypeId);
            ProcessType processType = oaProcessTypeService.getOne(wrapper);
            if(processType==null){
                continue;
            }
            processTemplate.setProcessTypeName(processType.getName());
        }
        return page;
    }

    @Override
    public void publish(Long id) {
        //修改模板发布状态 1 已发布
        ProcessTemplate processTemplate = baseMapper.selectById(id);
        processTemplate.setStatus(1);
        baseMapper.updateById(processTemplate);
//优先发布在线流程设计
        if(!StringUtils.isEmpty(processTemplate.getProcessDefinitionPath())) {
            oaProcessService.deployByZip(processTemplate.getProcessDefinitionPath());
        }
    }
}
