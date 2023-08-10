package com.hanxu.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanxu.model.process.Process;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxu.vo.process.ApprovalVo;
import com.hanxu.vo.process.ProcessFormVo;
import com.hanxu.vo.process.ProcessQueryVo;
import com.hanxu.vo.process.ProcessVo;

import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author hanxu
 * @since 2023-08-07
 */
public interface OaProcessService extends IService<Process> {

    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);
    //部署流程定义
    void deployByZip(String deployPath);

    IPage<ProcessVo> findProcessed(Page<Process> pageParam);
    //启动流程
    void startUp(ProcessFormVo processFormVo);


    Page<ProcessVo> findPending(Page<Process> pageParam);

    Map<String, Object> show(Long id);

    void approve(ApprovalVo approvalVo);

    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);
}
