package com.md.basePlatform.service.impl; // 业务实现包：存放 Service 接口的具体实现类

import com.md.basePlatform.ai.api.DroneAttributeAiService; // 引入 AI 属性生成服务接口，用于 AI 辅助填参
import com.md.basePlatform.common.exception.BusinessException; // 引入业务异常类，用于抛出可预期的失败信息
import com.md.basePlatform.domain.dto.DroneRequest; // 引入前端请求 DTO，作为方法入参的数据载体
import com.md.basePlatform.domain.entity.Drone;
import com.md.basePlatform.mapper.api.DroneMapper;
import com.md.basePlatform.service.api.DroneService;
import org.springframework.stereotype.Service; // Spring 的 @Service 注解，将本类标记为容器管理的业务 Bean

import java.util.List;
import java.util.concurrent.ThreadLocalRandom; // 引入线程安全的随机数工具，用于生成无人机编码

@Service // 告诉 Spring：这是一个业务层 Bean，请帮我创建并管理它的生命周期
public class DroneServiceImpl implements DroneService { // 实现 DroneService 接口中定义的全部方法

    private final DroneMapper droneMapper; // 持有数据访问层引用，用于执行 SQL 操作（由构造器注入，不可变）
    private final DroneAttributeAiService aiService; // 持有 AI 服务引用，用于生成无人机建议参数

    // 构造器注入（Spring 推荐方式）
    // Spring 会自动从容器中找到 DroneMapper 和 DroneAttributeAiService 的实例并传入
    public DroneServiceImpl(DroneMapper droneMapper, DroneAttributeAiService aiService) { // 构造器：Spring 自动注入两个依赖
        this.droneMapper = droneMapper;
        this.aiService = aiService;
    }

    @Override // 表示本方法是重写（实现）了 DroneService 接口中定义的 list 方法
    public List<Drone> list(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) { // 判断关键字是否为空：null、空串或纯空格都视为"无搜索条件"
            return droneMapper.selectList(null, null); // 无搜索条件时，传两个 null 给 Mapper，由 SQL 中判断跳过 WHERE 条件
        }
        String normalizedKeyword = keyword.trim(); // 去掉用户输入首尾的空白字符（空格、Tab等），防止干扰匹配
        String likeKeyword = "%" + normalizedKeyword + "%"; // 拼接 % 通配符实现模糊匹配：前后加 % 表示"包含该关键词即可"
        return droneMapper.selectList(normalizedKeyword, likeKeyword); // param1=精确匹配原关键词，param2=带通配符的模糊匹配
    }

    @Override // 重写接口的 getById 方法
    public Drone getById(Long id) {
        Drone drone = droneMapper.selectById(id);
        if (drone == null) {
            throw new BusinessException("无人机不存在"); // 抛出自定义业务异常，由全局异常处理器转为 JSON 错误响应（code=400）
        }
        return drone;
    }

    @Override // 重写接口的 create 方法
    public void create(DroneRequest request) {
        Drone drone = mapFromRequest(request); // 调用私有方法，将 DTO 中的字段值复制到新的 Entity 对象中
        if (drone.getDroneCode() == null || drone.getDroneCode().trim().isEmpty()) { // 如果前端没填编码，或者填了空串
            drone.setDroneCode(generateDroneCode()); // 调用编码生成方法，自动生成一个全局唯一的业务编码
        }
        drone.setAiGenerated(Boolean.TRUE.equals(request.getAttributesFromAi()) ? 1 : 0); // 如果前端标记了"AI填参"，则设置
                                                                                          // aiGenerated=1，否则为 0
        drone.setDeleted(0); // 新记录默认为未删除状态，deleted=0 表示"正常"
        int rows = droneMapper.insert(drone); // 执行 MyBatis 的 insert SQL，将实体写入 drone 表，rows 是受影响行数
        if (rows == 0) { // 如果受影响行数为 0，表示 insert 没有成功写入任何数据
            throw new BusinessException("新增失败"); // 抛出业务异常，提示前端新增操作失败
        }
    }

    // 更新无人机：先从库中查出旧记录，再用新值覆盖后写回
    // 重要：编码（droneCode）、AI标记（aiGenerated）、软删标记（deleted）这三个字段
    // 由旧记录保持，不被前端提交的值覆盖，以保证数据一致性。
    @Override // 重写接口的 update 方法
    public void update(Long id, DroneRequest request) { // 入参：id 为要更新的记录主键，request 为要覆盖的字段值
        Drone current = getById(id); // 先调用 getById 查出库中当前记录，若不存在会直接抛异常
        Drone update = mapFromRequest(request); // 将 DTO 映射为新的 Entity 对象（此时 id 为空，需后续设置）
        update.setId(current.getId()); // 把旧记录的主键 ID 赋给更新对象，确保 UPDATE 语句的 WHERE 条件正确
        update.setDroneCode(current.getDroneCode()); // 编码保持旧值不变，防止前端恶意修改业务编码
        update.setAiGenerated(current.getAiGenerated()); // AI 标记保持旧值不变，新增时的 AI 标记不应被编辑覆盖
        update.setDeleted(current.getDeleted()); // 软删标记保持旧值不变，不应在编辑时被意外改动
        int rows = droneMapper.updateById(update); // 执行 MyBatis 的 updateById SQL，返回受影响行数
        if (rows == 0) { // 受影响行数为 0，表示更新失败（可能记录在查出来后到写回前被其他操作删除了）
            throw new BusinessException("更新失败"); // 抛出业务异常提示更新操作失败
        }
    }

    // 软删除：将 deleted 字段标记为 1，并不真正从数据库中移除记录
    @Override // 重写接口的 delete 方法
    public void delete(Long id) { // 入参 id 为要软删的无人机主键
        int rows = droneMapper.softDeleteById(id); // 调用 Mapper 的软删除方法，SQL：UPDATE drone SET deleted=1 WHERE id=#{id} AND
                                                   // deleted=0
        if (rows == 0) { // 受影响行为 0，说明该 id 记录不存在，或者已经被软删过了（deleted 已经为 1）
            throw new BusinessException("删除失败或记录不存在"); // 抛出异常提示操作失败
        }
    }

    // AI 生成：委托 AI 服务根据型号和场景生成建议性能参数
    // 注意：此方法只返回内存中的 Drone 对象，不写入数据库
    @Override // 重写接口的 generateByAi 方法
    public Drone generateByAi(String model, String usageLevel) { // model 必填，usageLevel 可空默认为 normal
        return aiService.generate(model, usageLevel); // 直接委托给 AI 服务处理，返回填充了建议性能参数的 Drone 对象
    }

    // 工具方法：将前端请求 DTO 映射为领域实体 Entity
    // 这是一个纯数据搬运方法，不包含业务逻辑
    private Drone mapFromRequest(DroneRequest request) { // 私有方法，仅本类内部使用，将 DTO 转为 Entity
        Drone drone = new Drone(); // 创建一个空的无人机实体对象，所有字段初始为 null
        drone.setDroneCode(request.getDroneCode()); // 将 DTO 中的编码复制到实体（可能为 null，后续在 create 中补全）
        drone.setName(request.getName()); // 将 DTO 中的名称复制到实体（必填字段，已由 @NotBlank 校验）
        drone.setModel(request.getModel()); // 将 DTO 中的型号复制到实体（必填字段）
        drone.setManufacturer(request.getManufacturer()); // 将 DTO 中的厂商复制到实体（选填，可为 null）
        drone.setMaxPayload(request.getMaxPayload()); // 将 DTO 中的最大载重复制到实体（选填，可为 null）
        drone.setEnduranceMinutes(request.getEnduranceMinutes()); // 将 DTO 中的续航时间复制到实体（选填）
        drone.setCruiseSpeed(request.getCruiseSpeed()); // 将 DTO 中的巡航速度复制到实体（选填）
        drone.setStatus(request.getStatus() == null ? "READY" : request.getStatus()); // 状态：前端没填则默认设为 "READY"（就绪状态）
        return drone; // 返回组装好的实体对象，交给调用方继续处理
    }

    // 工具方法：生成全局唯一的无人机业务编码
    // 编码规则：字母 D + 当前毫秒时间戳 + 3位随机数，基本保证唯一性
    private String generateDroneCode() { // 私有方法，在新增且编码为空时自动调用
        long now = System.currentTimeMillis(); // 获取当前时间的毫秒值，保证编码具有时间唯一性
        int rand = ThreadLocalRandom.current().nextInt(100, 1000); // 生成 100~999 之间的随机三位数，进一步降低重复概率
        return "D" + now + rand; // 拼接：前缀 "D" + 毫秒时间戳 + 随机三位数，形成唯一业务编码
    }
}