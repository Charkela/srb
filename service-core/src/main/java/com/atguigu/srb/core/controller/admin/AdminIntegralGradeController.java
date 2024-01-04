package com.atguigu.srb.core.controller.admin;


import com.atguigu.common.exception.BusinessException;
import com.atguigu.common.result.R;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.srb.core.pojo.entity.IntegralGrade;
import com.atguigu.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2023-12-21
 */
@Api(tags="积分等级管理")
@RestController
@RequestMapping("admin/core/integralGrade")
//@CrossOrigin
@Slf4j
public class AdminIntegralGradeController {
    @Autowired
    private IntegralGradeService integralGradeService;
    @GetMapping("/list")
    @ApiOperation(value = "积分等级列表")
    public R list(){
        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list",list);
    }
    @DeleteMapping("/remove/{id}")
    @ApiOperation(value = "根据Id删除记录",notes = "逻辑删除数据记录")
    public R remove(@PathVariable Integer id){
        boolean isDelete = integralGradeService.removeById(id);
        if (isDelete){
            return R.ok().message("删除成功");
        }else {
            return R.error().message("删除失败");
        }
    }
    @ApiOperation("新增积分等级")
    @PostMapping("/save")
    public R save(
            @ApiParam(value = "积分等级对象",required = true)
            @RequestBody IntegralGrade integralGrade){
        //如果借款额度为空就手动抛出一个自定义的异常！
        if(integralGrade.getBorrowAmount() == null){
            //BORROW_AMOUNT_NULL_ERROR(-201, "借款额度不能为空"),
            throw new BusinessException(ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        }
        boolean save = integralGradeService.save(integralGrade);
        if (save){
            return R.ok().message("保存成功");
        }else{
            return R.error().message("新增积分等级对象失败");
        }

    }
    @ApiOperation("根据id获取积分等级")
    @GetMapping("/get/{id}")
    public R getById(
            @ApiParam(value = "对象id",required = true,example = "1")
            @PathVariable Integer id){
        IntegralGrade integralGrade = integralGradeService.getById(id);
        if (integralGrade!=null){
            return R.ok().data("record",integralGrade);
        }else {
            return R.error().message("获取失败");
        }
    }

}

