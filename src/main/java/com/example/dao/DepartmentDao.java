package com.example.dao;

import com.example.entity.Department;
import com.example.entity.Params;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Repository
public interface DepartmentDao extends Mapper<Department> {


    List<Department> findBySearch(@Param("params") Params params);

}