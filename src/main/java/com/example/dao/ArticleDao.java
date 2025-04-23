package com.example.dao;

import com.example.entity.Article;
import com.example.entity.Diagnosis;
import com.example.entity.Params;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Repository
public interface ArticleDao extends Mapper<Article> {

    List<Article> findBySearch(@Param("params") Params params);



}