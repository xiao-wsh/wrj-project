package com.md.basePlatform.common.response; // 通用响应模型包

public class ApiResponse<T> { // 泛型响应包装类

    private int code; // 业务状态码字段
    private String message; // 文本说明字段
    private T data; // 泛型数据载荷字段

    public static <T> ApiResponse<T> success(T data) { // 静态工厂：成功响应
        ApiResponse<T> response = new ApiResponse<T>(); // 新建空实例
        response.setCode(200); // 约定 200 表示业务成功
        response.setMessage("success"); // 固定英文成功提示（可按项目国际化调整）
        response.setData(data); // 写入业务数据（允许 null）
        return response; // 返回给控制器方法
    }

    public static <T> ApiResponse<T> fail(int code, String message) { // 静态工厂：失败响应
        ApiResponse<T> response = new ApiResponse<T>(); // 新建空实例
        response.setCode(code); // 设置失败码（常见 400/500）
        response.setMessage(message); // 设置失败原因文案
        return response; // 返回（data 保持默认 null）
    }

    public int getCode() { // JavaBean：读取 code
        return code; // 返回当前码值
    }

    public void setCode(int code) { // JavaBean：写入 code
        this.code = code; // 赋值给字段
    }

    public String getMessage() { // JavaBean：读取 message
        return message; // 返回说明字符串
    }

    public void setMessage(String message) { // JavaBean：写入 message
        this.message = message; // 赋值
    }

    public T getData() { // JavaBean：读取 data
        return data; // 返回泛型载荷
    }

    public void setData(T data) { // JavaBean：写入 data
        this.data = data; // 赋值
    }
}
