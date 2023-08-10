package com.hanxu.auth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanxu.model.system.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author hanxu
 * @since 2023-07-10
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> findMenuListByUserId(@Param("userId") Long userId);

}
