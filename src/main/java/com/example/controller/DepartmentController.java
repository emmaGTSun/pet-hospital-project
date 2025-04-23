package com.example.controller;

import com.example.common.Result;
import com.example.entity.Department;
import com.example.entity.Params;
import com.example.service.DepartmentService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@CrossOrigin // Solving Cross-Domain Issues
@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Resource
    private DepartmentService departmentService;

    @GetMapping("/search")
    public Result findBySearch(Params params) {
        PageInfo<Department> info= departmentService.findBySearch(params);
        return Result.success(info);
    }

    @PostMapping
    public Result save(@RequestBody Department department) {
        if (department.getId() == null) {
            departmentService.add(department);
        } else {
            departmentService.update(department);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")

    public Result delete(@PathVariable Integer id) {
        departmentService.delete(id);
        return Result.success();
    }





}
