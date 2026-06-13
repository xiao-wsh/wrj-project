package com.md.basePlatform.common.exception; // 全局异常处理所在包

import com.md.basePlatform.common.response.ApiResponse; // 统一响应体
import org.springframework.validation.BindException; // 表单绑定校验异常类型
import org.springframework.web.bind.MethodArgumentNotValidException; // JSON 体字段校验异常类型
import org.springframework.web.bind.annotation.ExceptionHandler; // 声明本方法处理特定异常类型
import org.springframework.web.bind.annotation.RestControllerAdvice; // 全局控制器增强（AOP 风格）

/**
 * 全局异常处理：将业务异常、参数校验异常等统一转换为 {@link ApiResponse} JSON，避免栈信息直接暴露给前端。
 */
@RestControllerAdvice // 作用于所有 @RestController（及带 @ResponseBody 的控制器方法）抛出的异常
public class GlobalExceptionHandler { // 全局异常处理器类

    @ExceptionHandler(BusinessException.class) // 捕获业务异常
    public ApiResponse<Object> handleBusiness(BusinessException ex) { // 入参为抛出的异常实例
        return ApiResponse.fail(400, ex.getMessage()); // HTTP 语义上仍 200，body 内 code=400，message 为业务文案
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // 捕获 @Valid + @RequestBody 校验失败
    public ApiResponse<Object> handleValidation(MethodArgumentNotValidException ex) { // 可读取 BindingResult
        String message = ex.getBindingResult().getFieldError() == null // 若没有具体字段错误
                ? "参数校验失败" // 使用兜底中文提示
                : ex.getBindingResult().getFieldError().getDefaultMessage(); // 否则取第一个字段错误默认消息
        return ApiResponse.fail(400, message); // 返回 400 业务码与字段级提示
    }

    @ExceptionHandler(BindException.class) // 捕获表单模型绑定校验失败
    public ApiResponse<Object> handleBind(BindException ex) { // 常见于非 JSON 提交场景
        String message = ex.getBindingResult().getFieldError() == null // 同上，判空第一个字段错误
                ? "参数绑定失败" // 绑定阶段失败兜底文案
                : ex.getBindingResult().getFieldError().getDefaultMessage(); // 取具体字段 message
        return ApiResponse.fail(400, message); // 统一失败结构
    }

    @ExceptionHandler(Exception.class) // 捕获所有未单独声明的异常（兜底）
    public ApiResponse<Object> handleOther(Exception ex) { // 入参为任意 Throwable 子类
        return ApiResponse.fail(500, ex.getMessage()); // 返回 500 与异常 message（生产可改为固定文案）
    }
}
