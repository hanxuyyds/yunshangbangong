package com.hanxu.process.service;

import com.hanxu.model.process.ProcessType;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author hanxu
 * @since 2023-08-06
 */
public interface OaProcessTypeService extends IService<ProcessType> {

    Object findProcessType();
}
