package com.lzc.reggie.controller;

import com.lzc.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 主要是用于文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController
{
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 进行文件的处理
     *
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file)
    {
        String uuid = UUID.randomUUID().toString();
        String filePath = null;
        log.info(file.toString());
        try
        {
            filePath = basePath + uuid + "_" + file.getOriginalFilename();
            file.transferTo(new File(filePath));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return R.success(filePath);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response)
    {
        // 表示以图片的形式发送
        response.setContentType("image/jpeg");

        FileInputStream fileInputStream = null;

        log.info("图片地址" + name);

        // 通过输入流 读取文件内容
        try
        {
            fileInputStream = new FileInputStream(name);
            ServletOutputStream outputStream = response.getOutputStream();
            // 通过流的形式进行流的转换
            IOUtils.copy(fileInputStream, outputStream);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }


    }
}
