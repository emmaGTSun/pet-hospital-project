package com.example.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Params;
import com.example.entity.Type;
import com.example.exception.CustomException;
import com.example.service.TypeService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/type")
public class TypeController {

    @Resource
    private TypeService typeService;

    @GetMapping
    public Result findAll() {
        return Result.success(typeService.findAll());
    }

    @PostMapping
    public Result save(@RequestBody Type type) {
        if (type.getId() == null) {
            typeService.add(type);
        } else {
            typeService.update(type);
        }
        return Result.success();
    }

    @GetMapping("/search")
    public Result findBySearch(Params params) {
        PageInfo<Type> info = typeService.findBySearch(params);
        return Result.success(info);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        typeService.delete(id);
        return Result.success();
    }

    @PutMapping("/delBatch")
    public Result delBatch(@RequestBody List<Type> list) {
        for (Type type : list) {
            typeService.delete(type.getId());
        }
        return Result.success();
    }

    @GetMapping("/export")
    public Result export(HttpServletResponse response) throws IOException {

        // 1. 从数据库中查询出所有数据
        List<Type> all = typeService.findAll();

        if (CollectionUtil.isEmpty(all)) {
            throw new CustomException("No Data Found");
        }

        // 2. 定义一个 List，存储处理之后的数据，用于塞到 list 里
        List<Map<String, Object>> list = new ArrayList<>(all.size());

        // 3. 定义Map<key,value> 出来，遍历每一条数据，然后封装到 Map<key,value> 里，把这个 map 塞到 list 里
        for (Type type : all) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Type Name", type.getName());           // 先添加 Type Name
            row.put("Type Description", type.getDescription()); // 然后添加 Type Description

            list.add(row);
        }

        // 4. 创建一个 ExcelWriter，把 list 数据用这个writer写出来（生成出来）
        ExcelWriter wr = ExcelUtil.getWriter(true);
        wr.write(list, true);

        // 5. 把这个 excel 下载下来
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition","attachment;filename=type.xlsx");  //type 可替换成指定文件名
        ServletOutputStream out = response.getOutputStream();
        wr.flush(out, true);
        wr.close();
        IoUtil.close(System.out);


        return Result.success();
    }

    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        System.out.println("hahahahahahhaha");
        List<Type> infoList = ExcelUtil.getReader(file.getInputStream()).readAll(Type.class);
        if (!CollectionUtil.isEmpty(infoList)) {
            for (Type type : infoList) {
                try {
                    typeService.add(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.success();
    }

}
