package com.hanxu.auth;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SpringBootTest
public class ProcessReview {
    //资源管理类，负责部署流程定义，管理流程资源
    @Autowired
    private RepositoryService repositoryService;
    //流程运行管理类
    @Autowired
    private RuntimeService runtimeService;
    //任务管理类
    @Autowired
    private TaskService taskService;
    //历史管理类
    @Autowired
    private HistoryService historyService;

    //流程定义
    @Test
    public void test01() {
        Deployment deploy = repositoryService.
                createDeployment().
                addClasspathResource("process/review01.bpmn20.xml").
                name("请假审批流程").
                deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
    //启动实例
    @Test
    public void test02(){
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("review01");
        System.out.println("流程定义的id:"+processInstance.getProcessDefinitionId());
        System.out.println("流程实例的id:"+processInstance.getId());
        System.out.println("当前活动的id:"+processInstance.getActivityId());
    }
    //查询任务
    @Test
    public void test03(){
        String assignee="zhangsan";
        List<Task> list = taskService.
                createTaskQuery().
                taskAssignee(assignee).
                list();
        for (Task task : list) {
            System.out.println("流程实例id:"+task.getProcessInstanceId());
            System.out.println("任务id:"+task.getId());
            System.out.println("任务负责人:"+task.getAssignee());
            System.out.println("任务名称:"+task.getName());
            taskService.complete(task.getId());
        }
    }
    //处理当前任务
    @Test
    public void test04(){
        Task task = taskService.
                createTaskQuery().
                taskAssignee("zhangsan").
                singleResult();
        System.out.println("流程实例id:"+task.getProcessInstanceId());
        System.out.println("任务id:"+task.getId());
        System.out.println("任务负责人:"+task.getAssignee());
        System.out.println("任务名称:"+task.getName());
        taskService.complete(task.getId());
    }
    //查看已处理历史任务
    @Test
    public void test05(){
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssignee("zhangsan").finished().list();
        for (HistoricTaskInstance historicTaskInstance : list) {
            System.out.println("流程实例id:"+historicTaskInstance.getProcessInstanceId());
            System.out.println("任务id:"+historicTaskInstance.getId());
            System.out.println("任务负责人:"+historicTaskInstance.getAssignee());
            System.out.println("任务名称:"+historicTaskInstance.getName());
        }
    }
    //启动流程实例，添加key
    @Test
    public void test06(){
        String key="1";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("review01", key);
        System.out.println("流程实例id:"+processInstance.getId());
        System.out.println("业务id:"+processInstance.getBusinessKey());
    }
    //
    @Test
    public void test07(){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("review01").singleResult();
        boolean suspended = processDefinition.isSuspended();
    }

    @Test
    public void test08(){
        Deployment deploy = repositoryService.
                createDeployment().
                addClasspathResource("process/review02.bpmn20.xml").
                name("加班").
                deploy();
        Map<String, Object> map = new HashMap<>();
        map.put("assignee01","zhangsan");
        map.put("assignee02","lisi");
        ProcessInstance review02 = runtimeService.startProcessInstanceByKey("review02", map);
        System.out.println("流程定义id:"+review02.getProcessDefinitionId());
        System.out.println("流程实例id:"+review02.getId());
    }
    @Test
    public void test09(){
        Deployment deploy = repositoryService.
                createDeployment().
                addClasspathResource("process/review05.bpmn20.xml").
                name("请假申请").
                deploy();
        System.out.println(deploy.getName());
        ProcessInstance review04 = runtimeService.startProcessInstanceByKey("review05");
        System.out.println(review04.getId());
    }
    //查询组任务
    @Test
    public void test10(){
        List<Task> list = taskService.
                createTaskQuery().
                taskCandidateUser("zhangsan01").
                list();
        for (Task task : list) {
            System.out.println("流程实例id:"+task.getProcessInstanceId());
            System.out.println("任务id:"+task.getId());
            System.out.println("任务负责人:"+task.getAssignee());
            System.out.println("任务名称:"+task.getName());
        }
    }
    //拾取组任务
    @Test
    public void test11(){
        Task task = taskService.
                createTaskQuery().
                taskCandidateUser("zhangsan01").
                singleResult();
        if(task!=null){
            taskService.claim(task.getId(),"zhangsan01");
            System.out.println("任务拾取成功");
        }
    }
    //查询个人代办任务
    @Test
    public void test12(){
        List<Task> list = taskService.createTaskQuery().taskAssignee("zhangsan01").list();
        for (Task task : list) {
            System.out.println("流程实例id:"+task.getProcessInstanceId());
            System.out.println("任务id:"+task.getId());
            System.out.println("任务负责人:"+task.getAssignee());
            System.out.println("任务名称:"+task.getName());
            taskService.complete(task.getId());
        }
    }
    //办理个人任务
    @Test
    public void test13(){
        
    }

}
