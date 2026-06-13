package com.md.basePlatform.ai.api; // AI 服务接口包

import com.md.basePlatform.domain.entity.Drone; // 生成目标类型

/**
 * 无人机性能参数「AI 生成」能力抽象：由具体实现（如规则引擎）根据型号与场景给出建议值。
 */
public interface DroneAttributeAiService { // 可替换为真实大模型客户端实现

    Drone generate(String model, String usageLevel); // 根据型号与场景生成性能字段填充的新实体
}
