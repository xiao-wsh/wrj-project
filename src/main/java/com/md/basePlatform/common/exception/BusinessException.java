package com.md.basePlatform.common.exception; // 异常类型包

/**
 * 业务层主动抛出的运行时异常：表示可预期的失败（如记录不存在、乐观锁失败等），由全局处理器转为 HTTP 400。
 */
public class BusinessException extends RuntimeException { // 继承非检查异常，调用方无需强制 try-catch

    public BusinessException(String message) { // 仅携带消息文本的构造器
        super(message); // 交给父类存储 detailMessage，便于日志与响应输出
    }
}
