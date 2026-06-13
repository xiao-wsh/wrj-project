package com.md.basePlatform.service.api; // 业务接口包：定义业务层的契约（接口），供控制器层调用

import com.md.basePlatform.domain.dto.DroneRequest; // 引入入参 DTO 类型，用于接收前端提交的表单或 JSON 数据
import com.md.basePlatform.domain.entity.Drone;

import java.util.List;

// 无人机业务接口
public interface DroneService {

    List<Drone> list(String keyword);

    Drone getById(Long id);

    void create(DroneRequest request);// 前端传来的待修改字段封装实体，存放需要更新的各项新属性；

    void update(Long id, DroneRequest request);

    // 软删除无人机
    void delete(Long id);

    // AI生成无人机参数（仅预览，不入库）
    Drone generateByAi(String model, String usageLevel);
}