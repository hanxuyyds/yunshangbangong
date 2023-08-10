package com.hanxu.security.filiter;

import com.alibaba.fastjson.JSON;
import com.hanxu.common.jwt.JwtHelper;
import com.hanxu.common.result.ResponseUtil;
import com.hanxu.common.result.Result;
import com.hanxu.common.result.ResultCodeEnum;
import com.hanxu.security.custom.LoginUserInfoHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        logger.info("uri:"+httpServletRequest.getRequestURI());
        //如果是登录接口，直接放行
        if("/admin/system/index/login".equals(httpServletRequest.getRequestURI())) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(httpServletRequest);
        if(null != authentication) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            ResponseUtil.out(httpServletResponse, Result.build(null, ResultCodeEnum.PERMISSION));
        }
    }
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // token置于header里
        String token = request.getHeader("token");
        logger.info("token:"+token);
        if (!StringUtils.isEmpty(token)) {
            String username = JwtHelper.getUsername(token);
            logger.info("username:"+username);
            if (!StringUtils.isEmpty(username)) {
                //当前用户信息放到ThreadLocal里面
                LoginUserInfoHelper.setUserId(JwtHelper.getUserId(token));
                LoginUserInfoHelper.setUsername(username);
                //通过username从redis中获取数据
                String authString = (String)redisTemplate.opsForValue().get(username);
//                redisTemplate.delete(username);
                //把redis获取字符串权限数据转换要求集合类型List<SimpleGrantedAuthority>
                if(!StringUtils.isEmpty(authString)){
                    List<Map> maps = JSON.parseArray(authString, Map.class);
                    System.out.println(maps);
                    List<SimpleGrantedAuthority> authorities=new ArrayList<>();
                    maps.stream().forEach(map -> {authorities.add(new SimpleGrantedAuthority((String)map.get("authority")));});
                    return new UsernamePasswordAuthenticationToken(username, null, authorities);
                }else {
                    return new UsernamePasswordAuthenticationToken(username,null,new ArrayList<>());
                }
            }
        }
        return null;
    }
}
