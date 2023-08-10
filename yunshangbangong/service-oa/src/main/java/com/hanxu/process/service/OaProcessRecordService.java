package com.hanxu.process.service;

import com.hanxu.model.process.ProcessRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author hanxu
 * @since 2023-08-08
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {
    void record(Long processId,Integer status,String description);
}
