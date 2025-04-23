package com.example.dao;

import com.example.entity.Diagnosis;
import com.example.entity.Params;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Repository
public interface DiagnosisDao extends Mapper<Diagnosis> {

    List<Diagnosis> findBySearch(@Param("params") Params params);

    @Select("select diagnosis.*, type.name as typeName from diagnosis left join type on diagnosis.typeId = type.id")
    List<Diagnosis> findAll();

}