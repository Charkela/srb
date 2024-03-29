package com.atguigu.srb.core.controller.admin;


import com.atguigu.common.result.R;
import com.atguigu.srb.core.pojo.entity.Lend;
import com.atguigu.srb.core.service.LendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2023-12-21
 */
@Api(tags = "标的管理")
@RestController
@RequestMapping("/admin/core/lend")
@Slf4j
public class AdminLendController {
    @Resource
    private LendService lendService;

    @ApiOperation("标的列表")
    @GetMapping("/list")
    public R lendList() {
        List<Lend> list=lendService.lendList();
        return R.ok().data("list",list);
    }
    @ApiOperation("获取标的信息")
    @GetMapping("/show/{id}")
    public R show(@ApiParam(value = "标的编号")
                  @PathVariable Long id){
        Map<String, Object> result = lendService.getLendDetail(id);
        return R.ok().data("lendDetail",result);
    }
}

