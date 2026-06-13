package com.md.basePlatform.domain.entity; // 领域实体包

/**
 * 无人机持久化实体：与表 {@code drone} 字段一一映射，供 MyBatis 与 Thymeleaf 使用。
 */
public class Drone { // 无人机表行映射对象

    private Long id; // 主键，自增
    private String droneCode;
    private String name;
    private String model;
    private String manufacturer;
    private Integer maxPayload; // 最大载重（千克）
    private Integer enduranceMinutes; // 续航（分钟）
    private Integer cruiseSpeed; // 巡航速度（千米每小时）
    private String status; // 业务状态枚举字符串
    private Integer aiGenerated; // 是否 AI 生成字段：1 是 0 否
    private Integer deleted; // 软删标记：1 已删 0 未删
    private String createdAt; // 创建时间字符串（由 DB 默认填充）
    private String updatedAt; // 最后更新时间字符串

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id; // 赋值主键字段
    }

    public String getDroneCode() {
        return droneCode;
    }

    public void setDroneCode(String droneCode) { // 写入业务编码
        this.droneCode = droneCode; // 赋值编码字段
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { // 写入名称
        this.name = name; // 赋值名称字段
    }

    public String getModel() { // 读取型号
        return model; // 返回型号
    }

    public void setModel(String model) { // 写入型号
        this.model = model; // 赋值型号字段
    }

    public String getManufacturer() { // 读取厂商
        return manufacturer; // 返回厂商
    }

    public void setManufacturer(String manufacturer) { // 写入厂商
        this.manufacturer = manufacturer; // 赋值厂商字段
    }

    public Integer getMaxPayload() { // 读取最大载重
        return maxPayload; // 可能为 null
    }

    public void setMaxPayload(Integer maxPayload) { // 写入最大载重
        this.maxPayload = maxPayload; // 赋值载重字段
    }

    public Integer getEnduranceMinutes() { // 读取续航分钟数
        return enduranceMinutes; // 返回 Integer 包装
    }

    public void setEnduranceMinutes(Integer enduranceMinutes) { // 写入续航
        this.enduranceMinutes = enduranceMinutes; // 赋值续航字段
    }

    public Integer getCruiseSpeed() { // 读取巡航速度
        return cruiseSpeed; // 返回速度值
    }

    public void setCruiseSpeed(Integer cruiseSpeed) { // 写入巡航速度
        this.cruiseSpeed = cruiseSpeed; // 赋值速度字段
    }

    public String getStatus() { // 读取状态
        return status; // 返回状态字符串
    }

    public void setStatus(String status) { // 写入状态
        this.status = status; // 赋值状态字段
    }

    public Integer getAiGenerated() { // 读取 AI 生成标记
        return aiGenerated; // 0/1
    }

    public void setAiGenerated(Integer aiGenerated) { // 写入 AI 生成标记
        this.aiGenerated = aiGenerated; // 赋值标记字段
    }

    public Integer getDeleted() { // 读取软删标记
        return deleted; // 0/1
    }

    public void setDeleted(Integer deleted) { // 写入软删标记
        this.deleted = deleted; // 赋值删除字段
    }

    public String getCreatedAt() { // 读取创建时间
        return createdAt; // 返回时间字符串
    }

    public void setCreatedAt(String createdAt) { // 写入创建时间
        this.createdAt = createdAt; // 赋值创建时间字段
    }

    public String getUpdatedAt() { // 读取更新时间
        return updatedAt; // 返回更新时间字符串
    }

    public void setUpdatedAt(String updatedAt) { // 写入更新时间
        this.updatedAt = updatedAt; // 赋值更新时间字段
    }
}
