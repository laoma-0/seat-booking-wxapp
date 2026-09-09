package com.seat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seat.entity.Seat;
import com.seat.vo.SeatVO;

@Mapper 
public interface SeatMapper extends BaseMapper<Seat> {
    @Select ("SELECT * FROM t_seat WHERE area_id = #{areaId} order by seat_no")
    List<Seat> selectSeatsByAreaId(Long areaId);
   // 查询指定区域的所有座位，包含该时段的占用情况
    @Select("SELECT s.*, " +
        "EXISTS (SELECT 1 FROM t_booking b " +
        "        WHERE b.seat_id = s.seat_id " +
        "          AND b.book_date = #{bookDate} " +
        "          AND b.start_time < #{endTime} " +
        "          AND b.end_time   > #{startTime} " +
        "          AND b.status IN (0, 1)) AS occupied " +
        "FROM t_seat s " +
        "WHERE s.area_id = #{areaId} " +
        "ORDER BY s.seat_no")
    List<SeatVO> selectByAreaId(
        @Param("areaId") Long areaId,
        @Param("bookDate") String bookDate,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime);


}
