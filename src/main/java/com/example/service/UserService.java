package com.example.service;

import com.example.common.JwtTokenUtils;
import com.example.dao.UserDao;
import com.example.entity.Params;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserDao userDao;

    public List<User> findAll() {
        return userDao.selectAll();
    }

    public PageInfo<User> findBySearch(Params params) {
        // Enable Pagination Query
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        // The following query will automatically be performed according to the current pagination settings.
        List<User> list = userDao.findBySearch(params);
        return PageInfo.of(list);
    }

    public void add(User user) {
        // 1. A username is mandatory; without it, new additions are not permitted (as the username is required for future logins)
        if (user.getName() == null || "".equals(user.getName())) {
            throw new CustomException("Username cannot be empty.");
        }
        // 2. Perform duplicate checks; users with the same name are not allowed to be added multiple times:
        // Simply query the database based on the username to check.

        // Indicates that it already exists. Here, we need to inform the front-end that adding new entries is not allowed.
        User existingUser = userDao.findByName(user.getName());
        if (existingUser != null) {
            // If a user is found, throw an exception to indicate that new users cannot be added with duplicate names.
            throw new CustomException("The username already exists, please choose a different username.");
        }
        // Initialize a password
        if (user.getPassword() == null) {
            user.setPassword("123456");
        }
        userDao.insertSelective(user);
    }

    public void update(User user) {
        userDao.updateByPrimaryKeySelective(user);
    }

    public void delete(Integer id) {
        userDao.deleteByPrimaryKey(id);
    }

    public User login(User user) {
        // 1. Perform some non-null checks
        if (user.getName() == null || "".equals(user.getName())) {
            throw new CustomException("Username cannot be empty.");
        }
        if (user.getPassword() == null || "".equals(user.getPassword())) {
            throw new CustomException("Password cannot be empty.");
        }
        //  2. Query the database for user information based on the provided username and password.
        User existingUser = userDao.findByNameAndPassword(user.getName(), user.getPassword());
        if (existingUser == null) {
            // If not found, it means the entered username or password is incorrect. Notify the user that login is not allowed.
            throw new CustomException("Username or password is incorrect.");
        }
        //    If a user is found, it confirms that the user exists and the entered username and password are correct.
        // 生成该登录用户对应的token，然后跟着user一起返回到前台
        String token = JwtTokenUtils.genToken(existingUser.getId().toString(), existingUser.getPassword());
        existingUser.setToken(token);
        return existingUser;
    }

    public void updatePassword(Integer userId, String oldPassword, String newPassword) {
        // 根据用户ID查询现有用户
        User existingUser = userDao.selectByPrimaryKey(userId);
        if (existingUser == null) {
            throw new CustomException("User not found.");
        }
        // 检查旧密码是否匹配
        if (!existingUser.getPassword().equals(oldPassword)) {
            throw new CustomException("Old password is incorrect.");
        }
        // 更新为新密码，这里应该对新密码进行加密处理
        existingUser.setPassword(newPassword); // 注意：这里应该使用密码加密方法，比如 BCrypt 加密
        userDao.updateByPrimaryKeySelective(existingUser);
    }

    public User findById(Integer id) {
        return userDao.selectByPrimaryKey(id);
    }



}

