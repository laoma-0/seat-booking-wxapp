package com.seat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.seat.entity.Booking;
import com.seat.vo.BookingVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BookingMapper extends BaseMapper<Booking> {
   @Select("SELECT b.booking_id, b.user_id, b.seat_id, b.area_id, b.status, " +
        "b.book_date, b.start_time, b.end_time, b.create_time, " +
        "s.seat_no, a.area_name " +
        "FROM t_booking b " +
        "LEFT JOIN t_seat s ON b.seat_id = s.seat_id " +
        "LEFT JOIN t_area a ON b.area_id = a.area_id " +
        "WHERE b.user_id = #{userId} " +
        "ORDER BY CASE WHEN TIMESTAMP(b.book_date, b.end_time) >= NOW() THEN 0 ELSE 1 END ASC, " +
        "CASE WHEN TIMESTAMP(b.book_date, b.end_time) >= NOW() THEN TIMESTAMP(b.book_date, b.start_time) END ASC, " +
        "TIMESTAMP(b.book_date, b.start_time) DESC")
  List<BookingVO> selectMyBookings(@Param("userId") String userId);


}
