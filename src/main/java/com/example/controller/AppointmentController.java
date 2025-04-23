package com.example.controller;

import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.dao.DepartmentDao;
import com.example.entity.Appointment;
import com.example.entity.Department;
import com.example.entity.Params;
import com.example.service.AppointmentService;
import com.example.service.DepartmentService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@CrossOrigin // Solving Cross-Domain Issues
@RestController
@RequestMapping("/appointment")
public class AppointmentController {
    @Resource
    private AppointmentService appointmentService;
    @Resource
    private DepartmentDao departmentDao;

    @GetMapping("/search")
    public Result findBySearch(Params params) {
        PageInfo<Appointment> info= appointmentService.findBySearch(params);
        return Result.success(info);
    }

    @PostMapping
    public Result save(@RequestBody Appointment appointment) {
        /// 1. Check if the remaining number of bookable departments is zero;
        // it can be booked only if it is greater than zero.
        Department department = departmentDao.selectByPrimaryKey(appointment.getDepartmentId());
        if (department.getNum() == 0) {
            return Result.error("Sorry, there are no more available appointments.");
        }

        // 2. Insert a booking record into the appointment table.
        appointment.setTime(DateUtil.now());
        appointmentService.add(appointment);

        // 3. Decrease the count of the available bookable departments by one.
        department.setNum(department.getNum() - 1);
        departmentDao.updateByPrimaryKeySelective(department);
        return Result.success();
    }

    @DeleteMapping("/{id}")

    public Result delete(@PathVariable Integer id) {
        appointmentService.delete(id);
        return Result.success();
    }





}
