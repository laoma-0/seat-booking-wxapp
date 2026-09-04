package com.seat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seat.entity.Seat;

@Mapper 
public interface SeatMapper extends BaseMapper<Seat> {
    @Select ("SELECT * FROM t_seat WHERE area_id = #{areaId} order by seat_no")
    List<Seat> selectByAreaId(Long areaId);

}
