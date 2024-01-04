package com.atguigu.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.exception.Assert;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.srb.core.hfb.FormHelper;
import com.atguigu.srb.core.hfb.HfbConst;
import com.atguigu.srb.core.hfb.RequestHelper;
import com.atguigu.srb.core.mapper.UserInfoMapper;
import com.atguigu.srb.core.pojo.entity.UserAccount;
import com.atguigu.srb.core.mapper.UserAccountMapper;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.service.UserAccountService;
import com.atguigu.srb.core.utils.LendNoUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2023-12-21
 */
@Slf4j
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {
@Resource
private UserInfoMapper userInfoMapper;
    @Override
    public String commitAmount(BigDecimal chargeAmt, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();
        //判断账户绑定状态
        Assert.notEmpty(bindCode, ResponseEnum.USER_NO_BIND_ERROR);

        HashMap<String, Object> map = new HashMap<>();
        map.put("agentId", HfbConst.AGENT_ID);
        map.put("agentBillNo", LendNoUtils.getChargeNo());
        map.put("bindCode",userInfo.getBindCode());
        map.put("chargeAmt",chargeAmt);
        map.put("feeAmt",new BigDecimal(0));
        map.put("notifyUrl",HfbConst.RECHARGE_NOTIFY_URL);
        map.put("returnUrl",HfbConst.RECHARGE_RETURN_URL);
        map.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(map);
        map.put("sign",sign);
        String form = FormHelper.buildForm(HfbConst.RECHARGE_URL, map);
        return form;
    }

    @Override
    public String notify(Map<String, Object> map) {
        log.info("充值成功：" + JSONObject.toJSONString(map));

        String bindCode = (String)map.get("bindCode"); //充值人绑定协议号
        String chargeAmt = (String)map.get("chargeAmt"); //充值金额

        //优化
        baseMapper.updateAccount(bindCode, new BigDecimal(chargeAmt), new BigDecimal(0));
        return "success";
    }
}
