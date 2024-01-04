package com.atguigu.srb.oss.controller;

import com.atguigu.common.exception.BusinessException;
import com.atguigu.common.result.R;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.srb.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Api(tags = "阿里云文件管理")
@RestController
@Slf4j
//@CrossOrigin
@RequestMapping("/api/oss/file")
public class FileController {
    @Autowired
    private FileService fileService;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public R uploadFile(
            @ApiParam("上传的文件")
            @RequestParam("file") MultipartFile file,
            @ApiParam("上传模块名称")
            @RequestParam("module") String module) {
        String resultUrl = null;
        try {
            //获取文件名
            String filename = file.getOriginalFilename();
            //获取io流
            InputStream inputStream = file.getInputStream();
            //上传文件
            resultUrl = fileService.upload(inputStream, module, filename);
            //返回结果对象
            return R.ok().data("url", resultUrl);
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }
    }

    @ApiOperation("删除文件接口")
    @DeleteMapping("/delete/{url}")
    public R removeFile(
            @ApiParam(value = "要删除的文件", required = true)
            @RequestParam("url") String url) {
        fileService.removeFile(url);

        return R.ok().message("删除成功");
    }
}
