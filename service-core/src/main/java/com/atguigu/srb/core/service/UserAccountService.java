package com.atguigu.srb.core.service;

import com.atguigu.common.result.R;
import com.atguigu.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author Helen
 * @since 2023-12-21
 */
public interface UserAccountService extends IService<UserAccount> {

    String commitAmount(BigDecimal chargeAmt, Long userId);

    String notify(Map<String, Object> map);
}
