package com.seat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.seat.entity.Booking;



import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookingMapper extends BaseMapper<Booking> {
  
}
