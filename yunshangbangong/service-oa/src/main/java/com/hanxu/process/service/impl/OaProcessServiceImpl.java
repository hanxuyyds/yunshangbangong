package com.hanxu.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanxu.auth.service.SysUserService;
import com.hanxu.model.process.Process;
import com.hanxu.model.process.ProcessRecord;
import com.hanxu.model.process.ProcessTemplate;
import com.hanxu.model.system.SysUser;
import com.hanxu.process.mapper.OaProcessMapper;
import com.hanxu.process.service.OaProcessRecordService;
import com.hanxu.process.service.OaProcessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxu.process.service.OaProcessTemplateService;
import com.hanxu.security.custom.LoginUserInfoHelper;
import com.hanxu.vo.process.ApprovalVo;
import com.hanxu.vo.process.ProcessFormVo;
import com.hanxu.vo.process.ProcessQueryVo;
import com.hanxu.vo.process.ProcessVo;
import io.swagger.annotations.ApiOperation;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author hanxu
 * @since 2023-08-07
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;
    @Autowired
    private OaProcessRecordService oaProcessRecordService;
    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> page = baseMapper.selectPage(pageParam, processQueryVo);
        return page;
    }

    @Override
    public void deployByZip(String deployPath) {
        // 定义zip输入流
        InputStream inputStream = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream(deployPath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 流程部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
    }
    //已处理
    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
        //封装查询的条件
        HistoricTaskInstanceQuery query = historyService.
                createHistoricTaskInstanceQuery().
                taskAssignee(LoginUserInfoHelper.getUsername()).
                finished().
                orderByTaskCreateTime().
                desc();
        //调用方法进行条件分页查询，返回list集合
        int begin=(int)((pageParam.getCurrent()-1)*pageParam.getSize());
        int size=(int)pageParam.getSize();
        List<HistoricTaskInstance> list = query.listPage(begin, size);
        long totalCount = query.count();
        //遍历返回list集合，封装List<ProcessVo>
        List<ProcessVo> processVoList=new ArrayList<>();
        for (HistoricTaskInstance historicTaskInstance : list) {
            //获取流程实例的id
            String processInstanceId = historicTaskInstance.getProcessInstanceId();
            //根据流程实例id查询获取process信息
            LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Process::getProcessInstanceId,processInstanceId);
            Process process = baseMapper.selectOne(wrapper);
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process,processVo);
            //放到list
            processVoList.add(processVo);
        }
        //IPage封装分页查询所有数据，返回
        Page<ProcessVo> pageModel = new Page<>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        pageModel.setRecords(processVoList);
        return pageModel;
    }

    @Override
    public void startUp(ProcessFormVo processFormVo) {
        //1 根据当前用户id获取用户信息
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        //2 根据审批模板id把模板信息查询
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(processFormVo.getProcessTemplateId());
        //3 保存提交审批信息到业务表，oa_process
        Process process = new Process();
        BeanUtils.copyProperties(processFormVo,process);
        process.setStatus(1);
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请");
        baseMapper.insert(process);
        //4 启动流程实例 - RuntimeService
        //4.1 流程定义key
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();
        //4.2 业务key processId
        String bussinessKey = String.valueOf(process.getId());
        //4.3 流程参数form表单json数据，转换map集合
        String formValues = processFormVo.getFormValues();
        //formData
        JSONObject jsonObject = JSON.parseObject(formValues);
        JSONObject formData = jsonObject.getJSONObject("formData");
        //遍历formData得到内容，封装map集合
        Map<String, Object> map=new HashMap<>();
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            map.put(entry.getKey(),entry.getValue());
        }
        Map<String, Object> variables=new HashMap<>();
        variables.put("data",map);
        //启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, bussinessKey, variables);
        //5 查询下一个审批人
        //审批人可能有多个
        List<Task> list=this.getCurrentTaskList(processInstance.getId());
        List<String> nameList=new ArrayList<>();
        for (Task task : list) {
            String assigneeName = task.getAssignee();
            SysUser user = sysUserService.getUserByUserName(assigneeName);
            String name = user.getName();
            nameList.add(name);
            //TODO 6 推送消息
        }
        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待"+ StringUtils.join(nameList.toArray(),",")+"审批");
        //7 业务和流程关联 更新oa_process数据
        baseMapper.updateById(process);
        //记录操作审批信息记录
        oaProcessRecordService.record(process.getId(),1,"发起申请");

    }

    @Override
    public Page<ProcessVo> findPending(Page<Process> pageParam) {
        //1 封装查询条件，根据当前登录的用户登录
        TaskQuery query = taskService.
                createTaskQuery().
                taskAssignee(LoginUserInfoHelper.getUsername()).orderByTaskCreateTime().desc();
        //2 调用方法分页条件查询，返回list集合，代办任务集合
        //第一个参数：开始位置   第二个参数：每页显示记录数
        int begin=(int)((pageParam.getCurrent()-1)*pageParam.getSize());
        int size=(int)pageParam.getSize();
        List<Task> taskList = query.listPage(begin, size);
        long count = query.count();
        //3 封装返回list集合数据到List<ProcessVo>里面
        List<ProcessVo> processVoList = new ArrayList<>();
        for (Task task : taskList) {
            //从task获取流程实例id
            String processInstanceId = task.getProcessInstanceId();
            //根据流程实例id获取实例对象
            ProcessInstance processInstance = runtimeService.
                    createProcessInstanceQuery().
                    processInstanceId(processInstanceId).
                    singleResult();
            //从流程实例对象获取业务key
            String businessKey = processInstance.getBusinessKey();
            if (businessKey==null){
                continue;
            }
            //根据业务key获取Process对象
            Process process = baseMapper.selectById(businessKey);
            //Process对象复制到ProcessVo对象
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process,processVo);
            processVo.setTaskId(task.getId());
            //放入到processVoList集合中去
            processVoList.add(processVo);
        }
        //4 封装返回Page对象
        Page<ProcessVo> processVoPage = new Page<>(pageParam.getCurrent(), pageParam.getSize(), count);
        processVoPage.setRecords(processVoList);
        return processVoPage;
    }
    //查看审批详情信息
    @Override
    public Map<String, Object> show(Long id) {
        //1 根据流程id获取流程信息
        Process process = baseMapper.selectById(id);
        //2 根据流程id获取流程记录信息
        LambdaQueryWrapper<ProcessRecord> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getProcessId,id);
        List<ProcessRecord> processRecordList = oaProcessRecordService.list(wrapper);
        //3 根据模板id查询模板信息
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(process.getProcessTemplateId());
        //4 判断当前用户是否可以审批
        // 可以看到信息不一定能审批，不能重复审批
        boolean isApprove=false;
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        for (Task task : taskList) {
            //判断任务审批人是否是当前用户
            String username = LoginUserInfoHelper.getUsername();
            if(task.getAssignee().equals(username)){
                isApprove=true;
            }
        }
        //5 查询数据封装到map集合，返回
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        map.put("isApprove", isApprove);
        return map;
    }

    @Override
    public void approve(ApprovalVo approvalVo) {
        //1 从approvalVo获取任务id，根据任务id获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        //2 判断审批状态值
        //2.1 状态值=1 审批通过
        if(approvalVo.getStatus()==1){
            taskService.complete(taskId);
        }else {
            //2.2 状态值=-1 驳回，流程直接结束
            this.endTask(taskId);
        }
        //3 记录审批相关过程信息 oa_process_record
        String description=approvalVo.getStatus().intValue()==1?"通过":"驳回";
        oaProcessRecordService.record(approvalVo.getProcessId(), approvalVo.getStatus(),description);
        //4 查询下一个审批人，更新流程表记录process表记录
        Process process = baseMapper.selectById(approvalVo.getProcessId());
        //查询任务
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if(!CollectionUtils.isEmpty(taskList)){
            List<String> assigneeList=new ArrayList<>();
            for (Task task : taskList) {
                String assignee = task.getAssignee();
                SysUser user = sysUserService.getUserByUserName(assignee);
                assigneeList.add(user.getUsername());
                //TODO 公众号消息推送
            }
            process.setDescription("等待"+ StringUtils.join(assigneeList.toArray(),",")+"审批");
            process.setStatus(1);
        }else {
            if(approvalVo.getStatus().intValue()==1){
                process.setDescription("审批完成（通过）");
                process.setStatus(2);
            }else {
                process.setDescription("审批完成（驳回）");
                process.setStatus(-1);
            }
        }
        baseMapper.updateById(process);
    }

    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> page = baseMapper.selectPage(pageParam, processQueryVo);
        for (ProcessVo item : page.getRecords()) {
            item.setTaskId("0");
        }
        return page;
    }

    private void endTask(String taskId) {
        //  当前任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 获取流程定义模型BpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        // 获取结束流向节点
        List<EndEvent> endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        // 并行任务可能为null
        if(CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode) endEventList.get(0);
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());
        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //  清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //  建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        //  当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //  完成当前任务
        taskService.complete(taskId);
    }

    //当前任务列表
    private List<Task> getCurrentTaskList(String id) {
        List<Task> list = taskService.createTaskQuery().processInstanceId(id).list();
        return list;
    }
}
