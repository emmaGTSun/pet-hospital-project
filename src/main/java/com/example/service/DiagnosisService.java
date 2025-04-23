package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.dao.DiagnosisDao;
import com.example.dao.TypeDao;
import com.example.dao.UserDao;
import com.example.entity.Diagnosis;
import com.example.entity.Params;
import com.example.entity.Type;
import com.example.entity.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiagnosisService {
    @Resource
    private DiagnosisDao diagnosisDao;
    @Resource
    private TypeDao typeDao;
    @Resource
    private UserDao userDao;


    public PageInfo<Diagnosis> findBySearch(Params params) {
        // 开启分页查询
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        // 接下来的查询会自动按照当前开启的分页设置来查询
        List<Diagnosis> list = diagnosisDao.findBySearch(params);
        if (CollectionUtil.isEmpty(list)) {
            return PageInfo.of(new ArrayList<>());
        }
        for (Diagnosis diagnosis : list) {
            if (ObjectUtil.isNotEmpty(diagnosis.getTypeId())) {
                Type type = typeDao.selectByPrimaryKey(diagnosis.getTypeId());
                if (ObjectUtil.isNotEmpty(type)) {
                    diagnosis.setTypeName(type.getName());
                }
            }
            if (ObjectUtil.isNotEmpty(diagnosis.getUserId())) {
                User user = userDao.selectByPrimaryKey(diagnosis.getUserId());
                if (ObjectUtil.isNotEmpty(user)) {
                    diagnosis.setUserName(user.getName());
                }
            }

        }


        return PageInfo.of(list);
    }

    public void add(Diagnosis diagnosis) {

        diagnosisDao.insertSelective(diagnosis);
    }

    public void update(Diagnosis diagnosis) {
        diagnosisDao.updateByPrimaryKeySelective(diagnosis);

    }

    public void delete(Integer id) {
        diagnosisDao.deleteByPrimaryKey(id);
    }

    public List<Diagnosis> findAll() {
        return diagnosisDao.findAll();
    }
}

