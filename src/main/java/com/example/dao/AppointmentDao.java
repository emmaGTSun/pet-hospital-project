package com.example.dao;

import com.example.entity.Appointment;
import com.example.entity.Params;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Repository
public interface AppointmentDao extends Mapper<Appointment> {


    List<Appointment> findBySearch(@Param("params") Params params);

}