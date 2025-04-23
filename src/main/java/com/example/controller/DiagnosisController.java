package com.example.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.AutoLog;
import com.example.common.Result;
import com.example.entity.Diagnosis;
import com.example.entity.Params;
import com.example.service.DiagnosisService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@CrossOrigin // Solving Cross-Domain Issues
@RestController
@RequestMapping("/diagnosis")
public class DiagnosisController {
    @Resource
    private DiagnosisService diagnosisService;

    @GetMapping("/search")
    public Result findBySearch(Params params) {
        PageInfo<Diagnosis> info= diagnosisService.findBySearch(params);
        return Result.success(info);
    }

    @PostMapping
    @AutoLog("Modify the diagnostic information")
    public Result save(@RequestBody Diagnosis diagnosis) {
        if (diagnosis.getId() == null) {
            diagnosisService.add(diagnosis);
        } else {
            diagnosisService.update(diagnosis);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @AutoLog("Delete diagnostic information")
    public Result delete(@PathVariable Integer id) {
        diagnosisService.delete(id);
        return Result.success();
    }
// 饼图信息
    @GetMapping("/echarts/bie")
    public Result bie() {
        // Retrieve all diagnosis records
        List<Diagnosis> list = diagnosisService.findAll();
        // Group by 'typeName' and count the occurrences
        Map<String, Long> collect = list.stream()
                .filter(x -> ObjectUtil.isNotEmpty(x.getTypeName()))
                .collect(Collectors.groupingBy(Diagnosis::getTypeName, Collectors.counting()));
        // The final data structure returned to the frontend
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(collect)) {
            for (String key : collect.keySet()) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", key);
                map.put("value", collect.get(key));
                mapList.add(map);
            }
        }
        return Result.success(mapList);
    }

    @GetMapping("/echarts/bar")
    public Result bar() {
        // 查询出所有图书
        List<Diagnosis> list = diagnosisService.findAll();
        Map<String, Long> collect = list.stream()
                .filter(x -> ObjectUtil.isNotEmpty(x.getTypeName()))
                .collect(Collectors.groupingBy(Diagnosis::getTypeName, Collectors.counting()));

        List<String> xAxis = new ArrayList<>();
        List<Long> yAxis = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(collect)) {
            for (String key : collect.keySet()) {
                xAxis.add(key);
                yAxis.add(collect.get(key));
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("xAxis", xAxis);
        map.put("yAxis", yAxis);

        return Result.success(map);
    }

}
