package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.dao.ArticleDao;
import com.example.dao.UserDao;
import com.example.entity.Article;
import com.example.entity.Params;
import com.example.entity.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleService {
    @Resource
    private ArticleDao articleDao;
    @Resource
    private UserDao userDao;


    public PageInfo<Article> findBySearch(Params params) {
        // 开启分页查询
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        // 接下来的查询会自动按照当前开启的分页设置来查询
        List<Article> list = articleDao.findBySearch(params);
        if (CollectionUtil.isEmpty(list)) {
            return PageInfo.of(new ArrayList<>());
        }
        for (Article article : list) {
            if (ObjectUtil.isNotEmpty(article.getUserId())) {
                User user = userDao.selectByPrimaryKey(article.getUserId());
                if (ObjectUtil.isNotEmpty(user)) {
                    article.setUserName(user.getName());
                }
            }
        }

        return PageInfo.of(list);
    }

    public void add(Article article) {

        articleDao.insertSelective(article);
    }

    public void update(Article article) {
        articleDao.updateByPrimaryKeySelective(article);

    }

    public void delete(Integer id) {
        articleDao.deleteByPrimaryKey(id);
    }
}

