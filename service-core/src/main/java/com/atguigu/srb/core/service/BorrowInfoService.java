package com.atguigu.srb.core.service;

import com.atguigu.srb.core.pojo.entity.BorrowInfo;
import com.atguigu.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author Helen
 * @since 2023-12-21
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    BigDecimal getBorrowAmount(Long userId);

    Integer getStatusByUserId(Long userId);

    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    List<BorrowInfo> borrowInfoList();

    Map<String, Object> getBorrowInfoDetail(Integer id);

    void approval(BorrowInfoApprovalVO borrowInfoApprovalVO);
}
