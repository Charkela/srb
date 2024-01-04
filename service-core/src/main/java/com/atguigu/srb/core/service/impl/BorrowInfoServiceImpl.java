package com.atguigu.srb.core.service.impl;

import com.atguigu.common.exception.Assert;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.srb.core.enums.BorrowAuthEnum;
import com.atguigu.srb.core.enums.BorrowInfoStatusEnum;
import com.atguigu.srb.core.enums.BorrowerStatusEnum;
import com.atguigu.srb.core.enums.UserBindEnum;
import com.atguigu.srb.core.mapper.*;
import com.atguigu.srb.core.pojo.entity.BorrowInfo;
import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.pojo.entity.IntegralGrade;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.atguigu.srb.core.pojo.vo.BorrowerDetailVO;
import com.atguigu.srb.core.service.BorrowInfoService;
import com.atguigu.srb.core.service.BorrowerService;
import com.atguigu.srb.core.service.DictService;
import com.atguigu.srb.core.service.LendService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2023-12-21
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private  BorrowInfoMapper borrowInfoMapper;
    @Resource
    private DictService dictService;
    @Resource
    private BorrowerService borrowerService;
    @Resource
    private LendService lendService;

    @Resource
    private IntegralGradeMapper integralGradeMapper;
    @Resource
    private BorrowerMapper borrowerMapper;
    @Override
    public BigDecimal getBorrowAmount(Long userId) {
        //根据用户信息查询分数
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Integer integral = userInfo.getIntegral();
        //查询可借款金额
        LambdaQueryWrapper<IntegralGrade> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(IntegralGrade::getIntegralStart,integral)
                .ge(IntegralGrade::getIntegralEnd,integral);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(queryWrapper);
        System.out.println(integralGrade.toString()+"==============================================");
        if (integralGrade==null){
            return new BigDecimal(0);
        }
        return integralGrade.getBorrowAmount();
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        borrowInfoQueryWrapper.select("status").eq("user_id", userId);
        List<Object> objects = baseMapper.selectObjs(borrowInfoQueryWrapper);

        if(objects.size() == 0){
            //借款人尚未提交信息
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        }
        Integer status = (Integer)objects.get(0);
        return status;
    }

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        //获取userInfo的用户数据
        UserInfo userInfo = userInfoMapper.selectById(userId);

        //判断用户绑定状态
        Assert.isTrue(
                userInfo.getBindStatus().intValue() == UserBindEnum.BIND_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_BIND_ERROR);

        //判断用户信息是否审批通过
        Assert.isTrue(
                userInfo.getBorrowAuthStatus().intValue() == BorrowerStatusEnum.AUTH_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_AMOUNT_ERROR);

        //判断借款额度是否足够
        BigDecimal borrowAmount = this.getBorrowAmount(userId);
        Assert.isTrue(
                borrowInfo.getAmount().doubleValue() <= borrowAmount.doubleValue(),
                ResponseEnum.USER_AMOUNT_LESS_ERROR);

        //存储数据
        borrowInfo.setUserId(userId);
        //百分比转成小数
        borrowInfo.setBorrowYearRate( borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        baseMapper.insert(borrowInfo);
    }

    @Override
    public List<BorrowInfo> borrowInfoList() {
        List<BorrowInfo> borrowInfoList = baseMapper.selectBorrowInfoList();
        borrowInfoList.forEach(borrowInfo -> {
            String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
            String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
            String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
            borrowInfo.getParam().put("returnMethod", returnMethod);
            borrowInfo.getParam().put("moneyUse", moneyUse);
            borrowInfo.getParam().put("status", status);
        });

        return borrowInfoList;
    }

    @Override
    public Map<String, Object> getBorrowInfoDetail(Integer id) {
        //查询借款信息
        BorrowInfo borrowInfo = baseMapper.selectById(id);
        //组装信息
        borrowInfo.getParam().put("status", BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus()));
        borrowInfo.getParam().put("returnMethod",dictService.getNameByParentDictCodeAndValue("returnMethod",borrowInfo.getReturnMethod()));
        borrowInfo.getParam().put("moneyUse",dictService.getNameByParentDictCodeAndValue("moneyUse",borrowInfo.getMoneyUse()));
        //根据借款信息表id查询借款人id
        LambdaQueryWrapper<Borrower> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Borrower::getUserId,borrowInfo.getUserId());
        Borrower borrower = borrowerMapper.selectOne(queryWrapper);
        //查询借款人信息
        BorrowerDetailVO borrowerDetailVOById = borrowerService.getBorrowerDetailVOById(borrower.getId());
        //组装信息返回
        Map<String, Object> map = new HashMap<>();
        map.put("borrowInfo",borrowInfo);
        map.put("borrower",borrowerDetailVOById);

        return map;
    }

    @Override
    public void approval(BorrowInfoApprovalVO borrowInfoApprovalVO) {
        //修改借款状态
        Long id = borrowInfoApprovalVO.getId();
        BorrowInfo borrowInfo = borrowInfoMapper.selectById(id);
        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        borrowInfoMapper.updateById(borrowInfo);
        //审核通过新增标的
        if (borrowInfoApprovalVO.getStatus().intValue()==BorrowInfoStatusEnum.CHECK_OK.getStatus().intValue()){
        //新增标的
            lendService.createLend(borrowInfoApprovalVO,borrowInfo);
        }
    }
}
