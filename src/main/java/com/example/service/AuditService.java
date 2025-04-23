package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.JwtTokenUtils;
import com.example.dao.UserDao;
import com.example.dao.AuditDao;
import com.example.entity.User;
import com.example.entity.Audit;
import com.example.entity.Params;
import com.example.exception.CustomException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class AuditService {

    @Resource
    private AuditDao auditDao;
    @Resource
    private UserDao userDao;



    public void add(Audit audit) {auditDao.insertSelective(audit);}

    public void update(Audit audit) {auditDao.updateByPrimaryKeySelective(audit);
    }

    public PageInfo<Audit> findBySearch(Params params) {
        // 控制
        User user = JwtTokenUtils.getCurrentUser();
        if (ObjectUtil.isNull(user)) {
            throw new CustomException("Unable to extract user information from the token, please log in again");
        }
        if ("ROLE_DOCTOR".equals(user.getRole())) {
            params.setUserId(user.getId());
        }

        // 开启分页查询，下一次查询就会分页
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        // 接下来的查询会自动按照当前开启的分页设置来查询
        List<Audit> list = auditDao.findBySearch(params);
//        for(Audit audit : list) {
//            if (ObjectUtil.isNotEmpty(audit.getUserId())) {
//               User user = userDao.selectByPrimaryKey(audit.getUserId());
//                if (ObjectUtil.isNotEmpty(user)) {
//                    audit.setUserName(user.getName());
//                }
//            }
//        }

        return PageInfo.of(list);
    }

    public void delete(Integer id) {
        auditDao.deleteByPrimaryKey(id);
    }

}
