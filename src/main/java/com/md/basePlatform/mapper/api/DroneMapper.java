package com.md.basePlatform.mapper.api; // MyBatis Mapper 接口包：定义数据操作接口

import com.md.basePlatform.domain.entity.Drone;

import java.util.List;

public interface DroneMapper { // MyBatis 的 Mapper 接口，不需要实现类，由框架动态代理

    List<Drone> selectList(String keyword, String likeKeyword);

    Drone selectById(Long id);

    int insert(Drone drone);

    int updateById(Drone drone);

    // 软删除：将 deleted 字段标记为 1，并非物理删除数据
    int softDeleteById(Long id); // 根据 id 软删除一条无人机记录（将 deleted 设为 1），保留数据可恢复
}