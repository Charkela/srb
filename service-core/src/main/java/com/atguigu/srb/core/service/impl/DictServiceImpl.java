package com.atguigu.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.srb.core.listener.ExcelDictDTOListener;
import com.atguigu.srb.core.pojo.dto.ExcelDictDTO;
import com.atguigu.srb.core.pojo.entity.Dict;
import com.atguigu.srb.core.mapper.DictMapper;
import com.atguigu.srb.core.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2023-12-21
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Autowired
    RedisTemplate redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importData(InputStream inputStream) {
        EasyExcel.read(inputStream, ExcelDictDTO.class, new ExcelDictDTOListener(baseMapper)).sheet().doRead();
        log.info("数据字典导入成功");
    }

    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dicts = baseMapper.selectList(null);
        ArrayList<ExcelDictDTO> excelDictDTOS = new ArrayList<>(dicts.size());
        dicts.forEach(
                e -> {
                    ExcelDictDTO excelDictDTO = new ExcelDictDTO();
                    BeanUtils.copyProperties(e, excelDictDTO);
                    excelDictDTOS.add(excelDictDTO);
                }

        );
        return excelDictDTOS;
    }

    @Override
    public List<Dict> listByParentId(Long parentId) {
        //查询redis中是否有缓存
        List<Dict> list = (List<Dict>) redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);

        //存在直接返回
        if (list != null) {
         return list;
        }
        //不存在则从数据库查询
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getParentId, parentId);
        List<Dict> dictList = baseMapper.selectList(queryWrapper);
        dictList.forEach(
                dict -> {

                    dict.setHasChildren(hasChildren(dict));
                }
        );
        //将数据放入缓存
        redisTemplate.opsForValue().set("srb:core:dictList:" + parentId, dictList,5, TimeUnit.MINUTES);
        return dictList;
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getDictCode,dictCode);
        Dict dict = baseMapper.selectOne(queryWrapper);
//        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Dict::getParentId,dict.getId());
//        List<Dict> dictList = baseMapper.selectList(queryWrapper);
        List<Dict> dictList = this.listByParentId(dict.getId());
        return dictList;
    }

    @Override
    public String getNameByParentDictCodeAndValue(String dictCode, Integer value) {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getDictCode,dictCode);
        Dict dict = baseMapper.selectOne(queryWrapper);
        Long parentId = dict.getId();
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getParentId,parentId)
                .eq(Dict::getValue,value);
        String name = baseMapper.selectOne(wrapper).getName();
        return name;
    }

    private boolean hasChildren(Dict dict) {
        Long id = dict.getId();
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<Dict>().eq(Dict::getParentId, id);
        List<Dict> list = baseMapper.selectList(queryWrapper);
        if (list.size() == 0) {
            return false;
        }
        return true;

    }
}
