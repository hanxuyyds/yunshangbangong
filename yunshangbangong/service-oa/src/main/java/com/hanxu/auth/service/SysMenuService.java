package com.hanxu.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxu.model.system.SysMenu;
import com.hanxu.vo.system.AssginMenuVo;
import com.hanxu.vo.system.AssginRoleVo;
import com.hanxu.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author hanxu
 * @since 2023-07-10
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();
    void removeMenuById(Long id);

    List<SysMenu> findMenuByRoleId(Long roleId);

    void doAssign(AssginMenuVo assginMenuVo);

    List<RouterVo> findUserMenuListByUserId(Long userId);

    List<String> findUserPermsByUserId(Long userId);
}
