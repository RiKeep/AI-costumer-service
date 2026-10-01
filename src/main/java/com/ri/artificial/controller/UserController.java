package com.ri.artificial.controller;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ri
 * @date 2026-10-01 16:29
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    @GetMapping("/login")
    public String login(String username, String password) {
        // 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对
        if("zhang".equals(username) && "123456".equals(password)) {
            StpUtil.login(10001);
            return "登录成功";
        }
        return "登录失败";
    }
    @GetMapping("/logout")
    public String logout() {
        StpUtil.logout(10001);
        return "注销成功";
    }
}
