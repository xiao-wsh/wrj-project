package com.md.basePlatform.ai.impl; // AI服务实现类所在包

import com.md.basePlatform.ai.api.DroneAttributeAiService; // 引入AI服务接口
import com.md.basePlatform.domain.entity.Drone;
import org.springframework.stereotype.Service; // Spring服务注解，用于容器管理

import java.util.concurrent.ThreadLocalRandom; // 线程安全的随机数工具类

@Service // 交给Spring管理，可被其他类自动注入使用
public class RuleBasedDroneAttributeAiService implements DroneAttributeAiService { // 实现接口：规则引擎 + 随机数生成伪AI属性

    @Override
    public Drone generate(String model, String usageLevel) { // 实现接口方法：根据型号和场景生成性能参数
        Drone drone = new Drone();

        // 统一处理型号：为空则设为空字符串，否则去空格并转小写，方便匹配
        String normalizedModel = model == null ? "" : model.trim().toLowerCase();

        // 统一处理使用场景：为空则设为空字符串，否则去空格并转小写
        String normalizedUsage = usageLevel == null ? "" : usageLevel.trim().toLowerCase();

        // 将型号和用途拼接成一个关键字，用于统一判断
        String key = normalizedModel + "|" + normalizedUsage;

        // ===================== 默认基础参数 =====================
        int payloadBase = 12; // 默认最大载重（kg）
        int enduranceBase = 60; // 默认续航时间（分钟）
        int speedBase = 35; // 默认巡航速度（km/h）

        // ===================== 根据关键词匹配不同类型无人机，设置对应基础参数 =====================
        if (containsAny(key, "heavy", "cargo", "lift", "delivery", "transport", "物流", "载重")) { // 匹配重载/物流关键词
            payloadBase = 24; // 物流无人机载重较大：24kg
            enduranceBase = 50; // 物流机续航适中：50分钟
            speedBase = 40; // 物流机速度较快：40km/h
        }
        // 测绘、建模类无人机
        else if (containsAny(key, "survey", "mapping", "map", "测绘", "建模")) {
            payloadBase = 9; // 测绘机载重较小：9kg（仅带摄像头/传感器）
            enduranceBase = 90;
            speedBase = 30;
        }
        // 巡检、电力、线路类无人机
        else if (containsAny(key, "inspect", "inspection", "巡检", "电力", "line")) { // 匹配巡检/电力关键词
            payloadBase = 14; // 巡检机中等载重：14kg
            enduranceBase = 70;
            speedBase = 36;
        }
        // 竞速、FPV类无人机
        else if (containsAny(key, "fpv", "race", "racing", "竞速")) { // 匹配竞速/FPV关键词
            payloadBase = 5; // 竞速机载重最小：5kg
            enduranceBase = 25;
            speedBase = 85;
        }
        // 农业、植保类无人机
        else if (containsAny(key, "agri", "spray", "farm", "农业", "植保")) { // 匹配农业/植保关键词
            payloadBase = 18; // 植保机需要携带农药：18kg
            enduranceBase = 40;
            speedBase = 32;
        }

        // ===================== 生成随机波动值，让每次结果不同 =====================
        ThreadLocalRandom rnd = ThreadLocalRandom.current(); // 获取当前线程的随机数生成器（比 new Random() 更高效且线程安全）

        int payloadExtra = rnd.nextInt(0, 10); // 载重随机增加 0~9
        int enduranceExtra = rnd.nextInt(0, 25); // 续航随机增加 0~24
        int speedExtra = rnd.nextInt(0, 15); // 速度随机增加 0~14

        // ===================== 将最终计算结果赋值给无人机对象 =====================
        drone.setMaxPayload(payloadBase + payloadExtra); // 设置最大载重
        drone.setEnduranceMinutes(enduranceBase + enduranceExtra); // 设置续航时间
        drone.setCruiseSpeed(speedBase + speedExtra); // 设置巡航速度

        drone.setAiGenerated(1); // 设置为1表示"是AI生成的"，前端可据此显示AI标签

        drone.setStatus("READY"); // 默认状态设为READY（就绪），表示无人机可投入使用的初始状态

        // 返回生成完成的无人机对象
        return drone;
    }

    private boolean containsAny(String source, String... patterns) { // 私有工具：判断源字符串是否包含任意一个关键词（可变参数）
        // 遍历所有关键词
        for (String p : patterns) {
            // 如果包含当前关键词，直接返回true
            if (source.contains(p)) { // String.contains: 判断源串中是否出现该关键词子串
                return true;
            }
        }
        // 所有关键词都不匹配，返回false
        return false;
    }
}