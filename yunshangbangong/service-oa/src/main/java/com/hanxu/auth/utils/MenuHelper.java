package com.hanxu.auth.utils;

import com.hanxu.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        List<SysMenu> trees=new ArrayList<>();
        for (SysMenu sysMenu : sysMenuList) {
            if(sysMenu.getParentId().longValue()==0){
                trees.add(getChildren(sysMenu,sysMenuList));
            }
        }
        return trees;
    }
    private static SysMenu getChildren(SysMenu sysMenu,List<SysMenu> sysMenuList){
        sysMenu.setChildren(new ArrayList<SysMenu>());
        for (SysMenu menu : sysMenuList) {
            if(sysMenu.getChildren()==null){
                sysMenu.setChildren(new ArrayList<SysMenu>());
            }
            if(sysMenu.getId().longValue()==menu.getParentId().longValue()){
                sysMenu.getChildren().add(getChildren(menu,sysMenuList));
            }
        }
        return sysMenu;
    }
}
