package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.JwtTokenUtils;
import com.example.dao.AppointmentDao;
import com.example.dao.DepartmentDao;
import com.example.dao.UserDao;
import com.example.entity.Appointment;
import com.example.entity.Department;
import com.example.entity.Params;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {
    @Resource
    private AppointmentDao appointmentDao;
    @Resource
    private DepartmentDao departmentDao;
    @Resource
    private UserDao userDao;


    public PageInfo<Appointment> findBySearch(Params params) {
        // Get current user information
        User user = JwtTokenUtils.getCurrentUser();
        // Check if user information exists, throw an exception if it does not exist
        if (ObjectUtil.isNull(user)) {
            throw new CustomException("Unable to extract user information from the token, please log in again");
        }
        // If the user's role is "ROLE_OWNER", set the user ID in the query parameters to the current user ID
        if ("ROLE_OWNER".equals(user.getRole())) {
            params.setUserId(user.getId());
        }
        // Enable pagination for the query
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        // Subsequent queries will automatically follow the pagination settings enabled here
        List<Appointment> list = appointmentDao.findBySearch(params);
        if (CollectionUtil.isEmpty(list)) {
            return PageInfo.of(new ArrayList<>());
        }
        for (Appointment appointment : list) {
            if (ObjectUtil.isNotEmpty(appointment.getDepartmentId())) {
                Department department = departmentDao.selectByPrimaryKey(appointment.getDepartmentId());
                if (ObjectUtil.isNotEmpty(department)) {
                    appointment.setDepartmentName(department.getName());
                }
            }
            // Use different variable names to avoid redefinition
            if (ObjectUtil.isNotEmpty(appointment.getUserId())) {
                User currUser = userDao.selectByPrimaryKey(appointment.getUserId());
                if (ObjectUtil.isNotEmpty(currUser)) {
                    appointment.setUserName(currUser.getName());
                }
            }
        }
        return PageInfo.of(list);
    }

    public void add(Appointment appointment) { appointmentDao.insertSelective(appointment);}

    public void update(Appointment appointment) {appointmentDao.updateByPrimaryKeySelective(appointment);}

    public void delete(Integer id) {
        appointmentDao.deleteByPrimaryKey(id);
    }
}

