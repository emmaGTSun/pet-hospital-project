package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.dao.DepartmentDao;
import com.example.dao.UserDao;
import com.example.entity.Department;
import com.example.entity.Params;
import com.example.entity.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService {
    @Resource
    private DepartmentDao departmentDao;
    @Resource
    private UserDao userDao;

    public PageInfo<Department> findBySearch(Params params) {
        // 开启分页查询
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        // 接下来的查询会自动按照当前开启的分页设置来查询
        List<Department> list = departmentDao.findBySearch(params);
        if (CollectionUtil.isEmpty(list)) {
            return PageInfo.of(new ArrayList<>());
        }
        for (Department department : list) {
            if (ObjectUtil.isNotEmpty(department.getUserId())) {
                User user = userDao.selectByPrimaryKey(department.getUserId());
                if (ObjectUtil.isNotEmpty(user)) {
                    department.setUserName(user.getName());
                }
            }
        }

        return PageInfo.of(list);
    }

    public void add(Department department) { departmentDao.insertSelective(department);}

    public void update(Department department) { departmentDao.updateByPrimaryKeySelective(department);}

    public void delete(Integer id) {
        departmentDao.deleteByPrimaryKey(id);
    }
}

