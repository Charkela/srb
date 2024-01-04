package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.enums.LendStatusEnum;
import com.atguigu.srb.core.mapper.BorrowerMapper;
import com.atguigu.srb.core.pojo.entity.BorrowInfo;
import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.pojo.entity.Lend;
import com.atguigu.srb.core.mapper.LendMapper;
import com.atguigu.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.atguigu.srb.core.pojo.vo.BorrowerDetailVO;
import com.atguigu.srb.core.service.BorrowerService;
import com.atguigu.srb.core.service.DictService;
import com.atguigu.srb.core.service.LendService;
import com.atguigu.srb.core.utils.LendNoUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2023-12-21
 */
@Service
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {
    @Resource
    private DictService dictService;
    @Resource
    private BorrowerMapper borrowerMapper;
    @Resource
    private BorrowerService borrowerService;

    @Override
    public void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo) {
        Lend lend = new Lend();
        lend.setUserId(borrowInfo.getUserId());
        lend.setBorrowInfoId(borrowInfo.getId());
        lend.setLendNo(LendNoUtils.getLendNo());
        lend.setTitle(borrowInfoApprovalVO.getTitle());
        lend.setAmount(borrowInfo.getAmount());
        lend.setPeriod(borrowInfo.getPeriod());
        //年化利率
        lend.setLendYearRate(borrowInfoApprovalVO.getLendYearRate().divide(new BigDecimal(100)));
        //平台服务费率
        lend.setServiceRate(borrowInfoApprovalVO.getServiceRate().divide(new BigDecimal(100)));
        //还款方式
        lend.setReturnMethod(borrowInfo.getReturnMethod());
        //最低投资金额
        lend.setLowestAmount(new BigDecimal(100));
        //已投金额
        lend.setInvestAmount(new BigDecimal(0));
        //投资人数
        lend.setInvestNum(0);
        //发布日期
        lend.setPublishDate(LocalDateTime.now());
        //开始日期
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate lendStartDate = LocalDate.parse(borrowInfoApprovalVO.getLendStartDate(), pattern);
        lend.setLendStartDate(lendStartDate);
        //结束日期
        LocalDate lendEndDate = lendStartDate.plusMonths(borrowInfo.getPeriod());
        lend.setLendEndDate(lendEndDate);
        //说明
        lend.setLendInfo(borrowInfoApprovalVO.getLendInfo());
        //平台预期收益

        BigDecimal amount = borrowInfo.getAmount();//总金额
        BigDecimal monthRate = lend.getLendYearRate().divide(new BigDecimal(12), 8, BigDecimal.ROUND_DOWN);//每期收益率
        BigDecimal expectAmount=amount.multiply(monthRate.multiply(new BigDecimal(lend.getPeriod())));
        lend.setExpectAmount(expectAmount);
        //实际收益
        lend.setRealAmount(new BigDecimal(0));
        //状态
        lend.setStatus(LendStatusEnum.INVEST_RUN.getStatus());
        //审核时间
        lend.setCreateTime(LocalDateTime.now());
        //审核人
        lend.setCheckAdminId(1L);
        //放款时间


        //存入数据库
        baseMapper.insert(lend);
    }

    @Override
    public List<Lend> lendList() {
        List<Lend> list = baseMapper.selectList(null);
        list.forEach(lend -> {
            front(lend);
        });
        return list;
    }

    private void front(Lend lend) {
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", lend.getReturnMethod());
        String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
        lend.getParam().put("returnMethod",returnMethod);
        lend.getParam().put("status",status);
    }

    @Override
    public Map<String, Object> getLendDetail(Long id) {
        Lend lend = baseMapper.selectById(id);
        Lend lend1 = lend;
        front(lend1);

        //借款人对象
        Long userId = lend.getUserId();
        LambdaQueryWrapper<Borrower> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Borrower::getUserId,userId);
        Borrower borrower = borrowerMapper.selectOne(queryWrapper);

        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(borrower.getId());

        //拼装信息返回
        Map<String, Object> result=new HashMap<>();
        result.put("lend",lend1);
        result.put("borrower",borrowerDetailVO);
        return result;
    }


}
