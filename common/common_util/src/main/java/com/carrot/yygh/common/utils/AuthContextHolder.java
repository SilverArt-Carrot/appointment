package com.carrot.yygh.common.utils;

import com.carrot.yygh.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

// 获取用户信息工具类
public class AuthContextHolder {

    // 获取用户id
    public static Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("token");

        return JwtHelper.getUserId(token);
    }

    // 获取用户名称
    public static String getUserName(HttpServletRequest request) {
        String token = request.getHeader("token");

        return JwtHelper.getUserName(token);
    }
}
