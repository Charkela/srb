package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.pojo.entity.UserLoginRecord;
import com.atguigu.srb.core.mapper.UserLoginRecordMapper;
import com.atguigu.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.javassist.runtime.Desc;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2023-12-21
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

    @Override
    public List<UserLoginRecord> listTop50(Long userId) {
        LambdaQueryWrapper<UserLoginRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLoginRecord::getUserId,userId)
                .orderByDesc(UserLoginRecord::getId)
                .last("limit 50");
        List<UserLoginRecord> recordList = baseMapper.selectList(queryWrapper);
        return recordList;
    }
}
