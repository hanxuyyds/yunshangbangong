package com.hanxu.auth;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask task) {
        if(task.getName().equals("经理审批")){
            task.setAssignee("zhangsan");
        }else {
            task.setAssignee("lisi");
        }
    }
}
