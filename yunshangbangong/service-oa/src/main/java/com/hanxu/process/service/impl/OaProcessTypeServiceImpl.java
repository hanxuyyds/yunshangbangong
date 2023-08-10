package com.hanxu.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hanxu.model.process.ProcessTemplate;
import com.hanxu.model.process.ProcessType;

import com.hanxu.process.mapper.OaProcessTypeMapper;
import com.hanxu.process.service.OaProcessTemplateService;
import com.hanxu.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author hanxu
 * @since 2023-08-06
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {
    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;
    @Override
    public Object findProcessType() {
        //查询所有的审批分类，返回list集合
        List<ProcessType> processTypeList = baseMapper.selectList(null);
        //遍历返回所有审批分类list集合
        for (ProcessType processType : processTypeList) {
            Long typeId = processType.getId();
            //得到每个审批分类，根据审批分类id查询对应的审批模板
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId,typeId);
            List<ProcessTemplate> processTemplateList = oaProcessTemplateService.list(wrapper);
            //根据审批分类id查询对应审批模板数据（List）封装到每个审批分类对象里面
            processType.setProcessTemplateList(processTemplateList);
        }
        return processTypeList;
    }
}
