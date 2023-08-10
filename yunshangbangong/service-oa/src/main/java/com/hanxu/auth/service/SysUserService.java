package com.hanxu.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxu.model.system.SysUser;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author hanxu
 * @since 2023-07-08
 */
public interface SysUserService extends IService<SysUser> {

    void updateStatus(Long id, Integer status);

    SysUser getUserByUserName(String username);

    Map<String, Object> getCurrentUser();
}
