package com.hanxu.wechat.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxu.model.wechat.Menu;
import com.hanxu.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author hanxu
 * @since 2023-08-09
 */
public interface MenuService extends IService<Menu> {

    List<MenuVo> findMenuInfo();

    void syncMenu();

    void removeMenu();
}
