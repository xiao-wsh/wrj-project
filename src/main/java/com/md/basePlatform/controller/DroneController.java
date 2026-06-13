package com.md.basePlatform.controller; // 控制器层包：处理 HTTP 请求与页面跳转

import com.md.basePlatform.common.response.ApiResponse; // 统一 JSON 响应体封装
import com.md.basePlatform.domain.dto.DroneRequest; // 前端提交的新增/编辑入参 DTO
import com.md.basePlatform.domain.entity.Drone;
import com.md.basePlatform.service.api.DroneService;
import org.springframework.stereotype.Controller; // 声明为 Spring MVC 控制器（可返回视图名）
import org.springframework.ui.Model; // 向 Thymeleaf 模板传递模型数据
import org.springframework.validation.annotation.Validated; // 开启方法级参数校验（如需要可扩展）
import org.springframework.web.bind.annotation.*; // Rest 风格映射注解集合

import javax.validation.Valid; // 标记请求体需按 Bean Validation 规则校验
import java.util.List;

@Controller // 本类方法可返回视图名由视图解析器渲染 HTML
@RequestMapping("/drones") // 本类所有映射 URL 均以 /drones 为前缀
@Validated // 类级别启用校验（与 @Valid 等配合）
public class DroneController {

    private final DroneService droneService; // 持有业务层引用，由构造器注入且不可变

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    @GetMapping // 处理 GET /drones：展示列表页
    public String list(@RequestParam(required = false) String keyword, Model model) {// 无人机列表查询接口，keyword为可选搜索关键词，model用于向前端传参
        List<Drone> drones = droneService.list(keyword);
        model.addAttribute("drones", drones); // 把列表数据放进模型，键名 drones 供 list.html 使用
        model.addAttribute("keyword", keyword); // 回显当前搜索词，便于页面展示输入框内容
        return "list"; // 返回视图逻辑名 list，对应 templates/list.html
    }

    @GetMapping("/new") // 处理 GET /drones/new：打开新增表单
    public String newForm(Model model) { // model 用于向表单页提供空对象或默认值
        model.addAttribute("drone", new Drone()); // 绑定一个空实体，表单各字段初始为空
        return "form"; // 返回表单视图 form → templates/form.html
    }

    @GetMapping("/{id}/edit") // 处理 GET /drones/{id}/edit：打开编辑表单
    public String editForm(@PathVariable Long id, Model model) { // 路径变量 id 为数据库主键
        model.addAttribute("drone", droneService.getById(id)); // 查出当前记录并放入模型供表单回显
        return "form"; // 与新增共用同一套表单模板
    }

    @PostMapping // 处理 POST /drones：JSON 新增接口
    @ResponseBody // 返回值序列化为 JSON 而非视图名
    public ApiResponse<Object> create(@Valid @RequestBody DroneRequest request) { // 请求体反序列化为 DTO 并校验
        droneService.create(request); // 调用业务层落库新增逻辑
        return ApiResponse.success(null); // 无额外数据时 data 置 null，仅表示成功
    }

    @PutMapping("/{id}") // 处理 PUT /drones/{id}：按主键更新
    @ResponseBody // 返回 JSON
    public ApiResponse<Object> update(@PathVariable Long id, @Valid @RequestBody DroneRequest request) { // 路径 id +
                                                                                                         // 请求体字段
        droneService.update(id, request); // 业务层合并 DTO 与库中旧记录后更新
        return ApiResponse.success(null); // 成功占位响应
    }

    @GetMapping("/del/{id}") // 处理 GET /drones/del/{id}：供页面链接删除后重定向（非 REST 风格但便于纯链接）
    public String deleteAndRedirect(@PathVariable Long id) { // 从路径解析要删的主键
        droneService.delete(id); // 执行软删除
        return "redirect:/drones"; // 302 重定向回列表，避免浏览器重复提交
    }

    @DeleteMapping("/{id}") // 处理 DELETE /drones/{id}：REST 删除接口
    @ResponseBody // JSON 响应
    public ApiResponse<Object> delete(@PathVariable Long id) { // 仅依赖路径中的 id
        droneService.delete(id); // 软删
        return ApiResponse.success(null); // 返回成功结构
    }

    @GetMapping("/ai/generate") // 处理 GET /drones/ai/generate：根据型号等生成建议参数（不落库）
    @ResponseBody // JSON
    public ApiResponse<Drone> aiGenerate( // 返回体中带 Drone 结构（仅生成字段有值）
            @RequestParam String model, // 必填：无人机型号字符串
            @RequestParam(required = false, defaultValue = "normal") String usageLevel // 可选：使用场景，默认 normal
    ) { // 方法签名结束，进入方法体
        Drone drone = droneService.generateByAi(model, usageLevel); // 委托业务层调用 AI 服务填充性能建议值
        return ApiResponse.success(drone); // 将内存中的 Drone 作为 data 返回给前端
    }
}
