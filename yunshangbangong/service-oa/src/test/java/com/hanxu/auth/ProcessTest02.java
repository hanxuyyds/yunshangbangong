package com.hanxu.auth;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class ProcessTest02 {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;



    @Test
    public void deployProcess01(){
        Deployment deploy = repositoryService.
                createDeployment().
                addClasspathResource("process/process01.bpmn20.xml").
                name("加班").deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
    @Test
    public void startUpProcess01(){
        Map<String, Object> map = new HashMap<>();
        map.put("assignee01","zhangsan");
        map.put("assignee02","lisi");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban01", map);
        System.out.println("流程定义Id:"+processInstance.getProcessDefinitionId());
        System.out.println("流程实例Id:"+processInstance.getId());
    }
    @Test
    public void deployProcess02(){
        Deployment deploy = repositoryService.
                createDeployment().
                addClasspathResource("process/process02.bpmn20.xml").
                name("加班").deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
    @Test
    public void startUpProcess02(){
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban02");
        System.out.println("流程定义Id:"+processInstance.getProcessDefinitionId());
        System.out.println("流程实例Id:"+processInstance.getId());
    }
    @Test
    public void deployProcess03(){
        Deployment deploy = repositoryService.
                createDeployment().
                addClasspathResource("process/process03.bpmn20.xml").
                name("加班").deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
    @Test
    public void startUpProcess03(){
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process");
        System.out.println("流程定义Id:"+processInstance.getProcessDefinitionId());
        System.out.println("流程实例Id:"+processInstance.getId());
    }
}
