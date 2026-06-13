package com.md.basePlatform.domain.dto; // 数据传输对象包

import javax.validation.constraints.NotBlank; // 非空字符串约束
import javax.validation.constraints.Size; // 长度上限约束

public class DroneRequest { // 入参载体，不经持久化注解映射

    @Size(max = 64, message = "无人机编码长度不能超过64") // 编码长度校验
    private String droneCode; // 业务编码，可空由服务端生成

    @NotBlank(message = "名称不能为空") // 名称必填
    @Size(max = 100, message = "名称长度不能超过100") // 名称长度上限
    private String name; // 无人机名称

    @NotBlank(message = "型号不能为空")
    @Size(max = 100, message = "型号长度不能超过100")
    private String model;

    @Size(max = 100, message = "厂商长度不能超过100")
    private String manufacturer; // 厂商，可空

    private Integer maxPayload; // 最大载重，可空表示未填
    private Integer enduranceMinutes; // 续航分钟，可空
    private Integer cruiseSpeed; // 巡航速度，可空

    @Size(max = 32, message = "状态长度不能超过32") // 状态字符串长度上限
    private String status; // 业务状态，可空由服务默认 READY

    private Boolean attributesFromAi; // 前端标记：是否通过 AI 填过性能字段

    public String getDroneCode() { // 读取编码
        return droneCode; // 返回字段值
    }

    public void setDroneCode(String droneCode) { // 写入编码
        this.droneCode = droneCode; // 赋值
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getMaxPayload() { // 读取最大载重
        return maxPayload; // 可能 null
    }

    public void setMaxPayload(Integer maxPayload) { // 写入最大载重
        this.maxPayload = maxPayload; // 赋值载重
    }

    public Integer getEnduranceMinutes() { // 读取续航
        return enduranceMinutes; // 返回续航
    }

    public void setEnduranceMinutes(Integer enduranceMinutes) { // 写入续航
        this.enduranceMinutes = enduranceMinutes; // 赋值续航
    }

    public Integer getCruiseSpeed() { // 读取巡航速度
        return cruiseSpeed; // 返回速度
    }

    public void setCruiseSpeed(Integer cruiseSpeed) { // 写入巡航速度
        this.cruiseSpeed = cruiseSpeed; // 赋值速度
    }

    public String getStatus() { // 读取状态
        return status; // 返回状态
    }

    public void setStatus(String status) { // 写入状态
        this.status = status; // 赋值状态
    }

    public Boolean getAttributesFromAi() { // 读取 AI 填参标记
        return attributesFromAi; // 可能 null 表示未声明
    }

    public void setAttributesFromAi(Boolean attributesFromAi) { // 写入 AI 填参标记
        this.attributesFromAi = attributesFromAi; // 赋值布尔包装
    }
}
