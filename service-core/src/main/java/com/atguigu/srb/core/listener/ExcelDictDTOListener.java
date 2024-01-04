package com.atguigu.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.srb.core.mapper.DictMapper;
import com.atguigu.srb.core.pojo.dto.ExcelDictDTO;
import com.atguigu.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.generator.config.IFileCreate;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {
    private DictMapper dictMapper;

    public ExcelDictDTOListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }


    private  static  final int BATCH=5;


    ArrayList<ExcelDictDTO> list = new ArrayList<>();
    @Override
    public void invoke(ExcelDictDTO data, AnalysisContext analysisContext) {
      list.add(data);
        if (list.size()>=BATCH){
            saveList();
            //存储完成清理
            list.clear();
        }
    }



    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
//剩余不足batch条数的数据在此处理

        saveList();
        log.info("所有数据存储完成");
    }

    private void saveList( ) {
        log.info("{}条数据开始存入数据库",list.size());
        dictMapper.insertBatch(list);
        log.info("数据存储完成");
    }
}
