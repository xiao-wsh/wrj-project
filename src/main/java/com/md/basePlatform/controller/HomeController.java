package com.md.basePlatform.controller; // 控制器包

import org.springframework.stereotype.Controller; // MVC 控制器 stereotype
import org.springframework.web.bind.annotation.GetMapping; // GET 映射注解

@Controller // 声明为控制器组件
public class HomeController { // 首页跳转专用类

    @GetMapping("/") // 映射网站根路径 GET /
    public String home() { // 无参：不依赖查询串或路径变量
        return "redirect:/drones"; // 返回重定向视图指令，浏览器地址变为 /drones
    }
}
