package com.hanxu.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hanxu.auth.service.SysMenuService;
import com.hanxu.auth.service.SysUserRoleService;
import com.hanxu.auth.service.SysUserService;
import com.hanxu.common.exception.AException;
import com.hanxu.common.jwt.JwtHelper;
import com.hanxu.common.result.Result;
import com.hanxu.common.utils.MD5;
import com.hanxu.model.system.SysUser;
import com.hanxu.model.system.SysUserRole;
import com.hanxu.vo.system.LoginVo;
import com.hanxu.vo.system.RouterVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
    @Autowired
    private SysUserService service;
    @Autowired
    private SysMenuService sysMenuService;
    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo){
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("token","admin-token");
//        return Result.ok(map);
        //1.获取输入用户名和密码
        String username = loginVo.getUsername();
        String password = loginVo.getPassword();
        //2.根据用户名查询数据库
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername,username);
        SysUser sysUser = service.getOne(wrapper);
        //3.用户信息是否存在
        if(sysUser==null){
            throw new AException(201,"用户不存在");
        }
        //4.判断密码
        //数据库内的密码
        String passwordDB = sysUser.getPassword();
        //获取输入加密后的密码
        String passwordInputMD5 = MD5.encrypt(password);

        if(!passwordDB.equals(passwordInputMD5)){
            throw new AException(201,"密码错误");
        }
        //5.判断用户是否被禁用
        if(sysUser.getStatus()==0){
            throw new AException(201,"用户被禁用");
        }
        //6.使用jwt根据用户id和用户名称生成token字符串
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        //7.返回
        HashMap<String, Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        //从请求头获取用户信息（获取请求头token字符串）
        String token = request.getHeader("token");
        //从token字符串获取用户id或者用户名称
        Long userId = JwtHelper.getUserId(token);
        //根据用户id查询数据库，把用户信息获取出来
        SysUser sysUser = service.getById(userId);
        //根据用户id获取用户可以操作菜单列表
        //查询数据库动态构建路由结构进行显示
        List<RouterVo>  routerList= sysMenuService.findUserMenuListByUserId(userId);
        //根据用户id获取用户可以操作按钮列表
        List<String> permsList =sysMenuService.findUserPermsByUserId(userId);
        //返回响应的数据
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",sysUser.getName());
        map.put("avatar","https://ts1.cn.mm.bing.net/th/id/R-C.f897d540f3f0cddef94a586c73172d51?rik=49n5CvDEWX%2bcqw&riu=http%3a%2f%2finews.gtimg.com%2fnewsapp_bt%2f0%2f15103659382%2f641&ehk=RX%2fHYW4Tg59fGPkpPUt2ygnVvFH5dUel9fagm1rc2D4%3d&risl=&pid=ImgRaw&r=0");
        //返回用户可以操作的菜单
        map.put("routers",routerList);
        //返回用户可以操作按钮
        map.put("buttons",permsList);
        return Result.ok(map);
    }
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }

}
