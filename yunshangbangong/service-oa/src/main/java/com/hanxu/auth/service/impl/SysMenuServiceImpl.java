package com.hanxu.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hanxu.auth.mapper.SysMenuMapper;
import com.hanxu.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxu.auth.service.SysRoleMenuService;
import com.hanxu.auth.utils.MenuHelper;
import com.hanxu.common.exception.AException;
import com.hanxu.model.system.SysMenu;
import com.hanxu.model.system.SysRoleMenu;
import com.hanxu.vo.system.AssginMenuVo;
import com.hanxu.vo.system.AssginRoleVo;
import com.hanxu.vo.system.MetaVo;
import com.hanxu.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author hanxu
 * @since 2023-07-10
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Autowired
    private SysRoleMenuService service;
    @Override
    public List<SysMenu> findNodes() {
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        List<SysMenu> resultList = MenuHelper.buildTree(sysMenuList);

        return resultList;
    }

    @Override
    public void removeMenuById(Long id) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId,id);
        Integer count = baseMapper.selectCount(wrapper);
        if(count>0){
            throw new AException(201,"菜单不能删除");
        }
        baseMapper.deleteById(id);
    }

    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        //查询所有菜单,添加条件 status==1
        LambdaQueryWrapper<SysMenu> sysMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysMenuLambdaQueryWrapper.eq(SysMenu::getStatus,1);
        List<SysMenu> sysMenuList = baseMapper.selectList(sysMenuLambdaQueryWrapper);
        //根据roleId，查询对应的menuId
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> list = service.list(wrapper);
        List<Long> ids = new ArrayList<>();
        for (SysRoleMenu sysRoleMenu : list) {
            ids.add(sysRoleMenu.getMenuId());
        }

        //再在所有菜单中找寻对应的，如果包含就封装
        for (SysMenu sysMenu : sysMenuList) {
            if(ids.contains(sysMenu.getId())){
                sysMenu.setSelect(true);
            }else{
                sysMenu.setSelect(false);
            }
        }
        //返回规定树形显示格式菜单列表
        List<SysMenu> sysMenus = MenuHelper.buildTree(sysMenuList);
        return sysMenus;
    }

    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {
        //根据角色id删除菜单角色表  分配数据
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,assginMenuVo.getRoleId());
        service.remove(wrapper);
        //从参数里面获取角色新分配菜单id列表
        List<Long> menuIdList = assginMenuVo.getMenuIdList();
        for (Long menuId : menuIdList) {
            if(StringUtils.isEmpty(menuId)){
                continue;
            }
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(assginMenuVo.getRoleId());
            service.save(sysRoleMenu);
        }
    }

    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenus=new ArrayList<>();
        //判断当前用户是否是管理员
        //1.如果是管理员，查询所有菜单列表
        if(userId.longValue()==1){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            wrapper.orderByAsc(SysMenu::getSortValue);
             sysMenus= baseMapper.selectList(wrapper);
        }else {
            //2.如果不是管理员，根据userId查询可以操作菜单列表
            //多表关联查询：用户角色关系表、角色菜单关系表、菜单表
            sysMenus=baseMapper.findMenuListByUserId(userId);
        }
        //把查询出来数据列表构建成框架要求的路由数据结构
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenus);
        //构建成框架要求的路由结构
        List<RouterVo> routerList= this.buildRouter(sysMenuTreeList);
        return routerList;
    }

    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        //判断当前用户是否是管理员
        List<SysMenu> sysMenuList=null;
        //1.如果是管理员，查询所有按钮列表
        if(userId.longValue()==1){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            sysMenuList= baseMapper.selectList(wrapper);
        }else {
            //2.如果不是管理员，根据userId查询可以操作按钮列表
            //多表关联查询：用户角色关系表、角色菜单关系表、菜单表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }

        //从查询出来的数据里面，获取可以操作按钮值的list集合
        List<String> permsList=new ArrayList<>();
        for (SysMenu sysMenu : sysMenuList) {
            if(sysMenu.getType()==2){
                permsList.add(sysMenu.getPerms());
            }
        }
        return permsList;
    }
    private List<RouterVo> buildRouter(List<SysMenu> menus){
        //创建list集合，存储最终数据
        List<RouterVo> routers=new ArrayList<>();
        //menus遍历
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            //下一层数据部分
            List<SysMenu> children = menu.getChildren();
            if(menu.getType().intValue()==1){
                //加载出来下面隐藏路由
                List<SysMenu> hiddenMenuList=new ArrayList<>();
                for (SysMenu child : children) {
                    if(!StringUtils.isEmpty(child.getComponent())){
                        hiddenMenuList.add(child);
                    }
                }
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            }else {
                if(!CollectionUtils.isEmpty(children)){
                    if(children.size()>0){
                        router.setAlwaysShow(true);
                    }
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }
        return routers;
    }
    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }
}
