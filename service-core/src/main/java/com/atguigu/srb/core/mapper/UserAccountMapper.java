package com.atguigu.srb.core.mapper;

import com.atguigu.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author Helen
 * @since 2023-12-21
 */
@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    void updateAccount(String bindCode, BigDecimal amount, BigDecimal freezeAmount);
}
