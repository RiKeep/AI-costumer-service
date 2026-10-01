package com.ri.artificial.interceptors;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Ri
 * @date 2026-10-01 21:34
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，定义详细认证规则
        registry.addInterceptor(new SaInterceptor(handler -> StpUtil.checkLogin()))
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除登录和注销接口
                .excludePathPatterns("/user/login", "/user/logout");
    }
}
